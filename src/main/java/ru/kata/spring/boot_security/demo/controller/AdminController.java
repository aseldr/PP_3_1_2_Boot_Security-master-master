package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.repository.UserRepository;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.security.Principal;
import java.util.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String adminHomeAndAllUsers(Model model, Principal principal) {
        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("principal", principal);
        model.addAttribute("userPrincipal", userService.findUserById(Long.valueOf(principal.getName())));
        return "users";
    }

    @PostMapping
    public String adminHomePost(@RequestParam("id") Long id, @RequestParam("action") String action) {
        if (action.equals("delete")) {
            userService.deleteUser(id);
        } else if (action.equals("edit")) {
            return "redirect:/admin/update?id=" + id;
        }
        return "redirect:/admin";
    }

    @GetMapping("/create")
    public String createUserForm(Model model) {
        model.addAttribute("user", new User());
        UserRepository userRepository = null;
        model.addAttribute("allRoles", userRepository.findAll());
        return "create";
    }

    @PostMapping("/create")
    public String createUser(@ModelAttribute("user") User user, @RequestParam("authorities") List<String> roles) {
        userService.createUser(user);
        return "redirect:/admin";
    }

    @GetMapping("/update")
    public String edit(Model model, @RequestParam("id") Long id) {
        User user = userService.findUserById(id);
        model.addAttribute("user", user);
        model.addAttribute("userRole", user.getAuthorities());
        model.addAttribute("isAdmin", user.getAuthorities().contains("ROLE_ADMIN"));
        return "update";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute("user") User user, @RequestParam("id") Long id, @RequestParam("role") List<String> roles) {
        userService.updateUser(id, user, roles);
        return "redirect:/admin";
    }
}
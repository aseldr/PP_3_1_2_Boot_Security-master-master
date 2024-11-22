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

import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private RoleRepository roleRepository;

    public AdminController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Autowired
    private UserRepository userRepository;

    public UserRepository getUserRepository(UserRepository userRepository) {
        return userRepository;
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    public PasswordEncoder getPasswordEncoder(PasswordEncoder passwordEncoder) {
        return passwordEncoder;
    }

    @GetMapping
    public String adminHomeAndAllUsers(Model model, Principal principal) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("principal", principal);
        model.addAttribute("userPrincipal", userRepository.findByUsername(principal.getName()));
        return "users";
    }

    @PostMapping
    public String adminHomePost(@RequestParam("id") Long id, @RequestParam("action") String action) {
        User user = userRepository.findById(id).get();
        if (action.equals("delete")) {
            userRepository.delete(user);
        } else if (action.equals("edit")) {
            return "redirect:/admin/update?id=" + user.getId();
        }
        return "redirect:/admin";
    }


    @GetMapping("/create")
    public String createUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("allRoles", roleRepository.findAll());
        return "create";
    }

    @PostMapping("/create")
    public String createUser(@ModelAttribute("user") User user, @RequestParam("authorities") List<String> roles) {
        if (roles.contains("ROLE_ADMIN")) {
            user.setRoles(new HashSet<>(roleRepository.findAll()));
        } else {
            Set<Role> roleSet = new HashSet<>(roleRepository.getRolesByName("ROLE_USER"));
            user.setRoles(roleSet);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "redirect:/admin";
    }

    @GetMapping("/update")
    public String edit(Model model, @RequestParam("id") Long id) {
        User user = userRepository.findById(id).get();
        model.addAttribute("user", user);
        model.addAttribute("userRole", user.getAuthorities());
        model.addAttribute("isAdmin", user.getAuthorities().contains("ROLE_ADMIN"));
        return "update";
    }

    @PostMapping("/update")
    public String update(@ModelAttribute("user") User user, @RequestParam("id") Long id, @RequestParam("role") List<String> roles) {
        User updatedUser = userRepository.findById(id).stream()
                .peek(up -> {
                    up.setUsername(user.getUsername());
                    up.setPassword(passwordEncoder.encode(user.getPassword()));
                    up.setEmail(user.getEmail());
                    if (roles.contains("ROLE_ADMIN")) {
                        up.setRoles(new HashSet<>(roleRepository.findAll()));
                    } else {
                        up.setRoles(new HashSet<>(Collections.singleton(roleRepository.findByName("ROLE_USER"))));
                    }
                }).findFirst().get();
        userRepository.save(updatedUser);
        return "redirect:/admin";
    }
}

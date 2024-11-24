package ru.kata.spring.boot_security.demo.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.service.UserServiceImpl;

import java.io.IOException;

@Controller
@RequestMapping("/login")
public class LoginController {

    private final UserServiceImpl UserServiceImpl;
    private final UserServiceImpl userServiceImpl;

    @Autowired
    public LoginController(UserServiceImpl UserServiceImpl, UserServiceImpl userServiceImpl) {
        this.UserServiceImpl = userServiceImpl;
        this.userServiceImpl = userServiceImpl;
    }

    @GetMapping
    public String loginForm() {
        return "/login";
    }

    @PostMapping
    public String login(@RequestParam("email") String email, @RequestParam("password") String password, Model model, HttpServletRequest request, HttpServletResponse response) {
        try {
            UserServiceImpl.authenticateUser(email, password, request, response);
            return null;
        } catch (BadCredentialsException | IOException e) {
            model.addAttribute("error", "Invalid email or password");
            return "/login";
        }
    }
}

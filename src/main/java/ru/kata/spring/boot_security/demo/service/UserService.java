package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.models.User;

public interface UserService {
    User loadUserByUsername(String email);
}

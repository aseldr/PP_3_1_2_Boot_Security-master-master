package ru.kata.spring.boot_security.demo.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.configs.SuccessUserHandler;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;

import java.io.IOException;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Autowired
    private UserRepository userRepository;

    public UserServiceImpl() {
        this.userRepository = userRepository;
    }

    @Query(value = "select u from User u left join fetch u.roles where u.email=:email")
    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {
        User user= userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("User %s not found", email));
        }
        return user;
    }


    private AuthenticationManager authenticationManager;
    private SuccessUserHandler successUserHandler;

    @Autowired
    public void UserServiceImpl(AuthenticationManager authenticationManager, SuccessUserHandler successUserHandler) {
        this.authenticationManager = authenticationManager;
        this.successUserHandler = successUserHandler;
    }

    public void authenticateUser(String email, String password, HttpServletRequest request, HttpServletResponse response) throws IOException, BadCredentialsException {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(email, password);
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        successUserHandler.onAuthenticationSuccess(request, response, authentication);
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void updateUser(Long id, User user, List<String> roles) {
        User updatedUser = findUserById(id);
        updatedUser.setUsername(user.getUsername());
        userRepository.save(updatedUser);
    }

    @Override
    public User findUserById(Long id) {
        User findUserById = findUserById(id);
        return findUserById;
    }

    @Override
    public Optional<User> updateUser(Long id, User user) {
        return userRepository.findById(id);

    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
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

@Service
public class UserServiceImpl implements UserService {

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
}
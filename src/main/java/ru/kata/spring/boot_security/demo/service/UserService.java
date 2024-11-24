package ru.kata.spring.boot_security.demo.service;

import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.kata.spring.boot_security.demo.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    @Query(value = "select u from User u left join fetch u.roles where u.email=:email")
    User loadUserByUsername(String email) throws UsernameNotFoundException;

    User createUser(User user);
    void updateUser(Long id, User user, List<String> roles);
    Optional<User> updateUser(Long id, User user);
    void deleteUser(Long id);
    User findByUsername(String name);
    Object findAllUsers();
    User findUserById(Long id);
}

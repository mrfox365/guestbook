package com.example.core.service;

import com.example.core.domain.User;
import com.example.core.ports.UserRepositoryPort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserService implements UserDetailsService {

    private final UserRepositoryPort userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepositoryPort userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public User getUser(Long id) {
        return userRepo.findUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Long getUserId(String username) {
        return userRepo.findUserByUsername(username)
                .map(User::id)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    public void registerUser(String username, String rawPassword) {
        String encodedPassword = passwordEncoder.encode(rawPassword);
        User newUser = new User(null, username, encodedPassword, "USER");
        userRepo.saveUser(newUser);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.username())
                .password(user.password())
                .roles(user.role())
                .build();
    }
}
package com.example.core.ports;

import com.example.core.domain.User;
import java.util.Optional;

public interface UserRepositoryPort {
    Optional<User> findUserById(Long id);
    Optional<User> findUserByUsername(String username);
    User saveUser(User user);
}
package com.example.core.service;

import com.example.core.domain.User;
import com.example.core.ports.UserRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepositoryPort repo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService service;

    @Test
    void getUser_shouldReturnUser_whenFound() {
        User user = new User(1L, "dima", "pass", "USER");
        when(repo.findUserById(1L)).thenReturn(Optional.of(user));

        User result = service.getUser(1L);

        assertEquals("dima", result.username());
    }

    @Test
    void getUserId_shouldReturnId_whenFound() {
        User user = new User(55L, "admin", "pass", "ADMIN");
        when(repo.findUserByUsername("admin")).thenReturn(Optional.of(user));

        Long id = service.getUserId("admin");

        assertEquals(55L, id);
    }

    @Test
    void getUserId_shouldThrow_whenNotFound() {
        when(repo.findUserByUsername("ghost")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.getUserId("ghost"));
    }

    @Test
    void registerUser_shouldEncodePasswordAndSave() {
        // Arrange
        String rawPass = "secret123";
        String encodedPass = "$2a$10$hashedstring...";
        when(passwordEncoder.encode(rawPass)).thenReturn(encodedPass);

        // Act
        service.registerUser("newUser", rawPass);

        // Assert: Capture what was passed to saveUser
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(repo).saveUser(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals("newUser", savedUser.username());
        assertEquals(encodedPass, savedUser.password()); // Verify that hash is saved
        assertEquals("USER", savedUser.role()); // Verify default role
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetails() {
        // Arrange
        User domainUser = new User(1L, "testUser", "hashedPass", "ADMIN");
        when(repo.findUserByUsername("testUser")).thenReturn(Optional.of(domainUser));

        // Act
        UserDetails userDetails = service.loadUserByUsername("testUser");

        // Assert
        assertNotNull(userDetails);
        assertEquals("testUser", userDetails.getUsername());
        assertEquals("hashedPass", userDetails.getPassword());

        // Verify that ADMIN role is converted to Authority ROLE_ADMIN
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void loadUserByUsername_shouldThrow_whenNotFound() {
        when(repo.findUserByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                service.loadUserByUsername("unknown")
        );
    }
}
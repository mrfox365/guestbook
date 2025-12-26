package com.example.web.config;

import com.example.core.ports.BookRepositoryPort;
import com.example.core.ports.CommentRepositoryPort;
import com.example.core.ports.UserRepositoryPort;
import com.example.core.service.BookService;
import com.example.core.service.CommentService;
import com.example.core.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ServiceConfig {

    @Bean
    public BookService bookService(BookRepositoryPort repo) {
        return new BookService(repo);
    }

    @Bean
    public CommentService commentService(CommentRepositoryPort repo) {
        return new CommentService(repo);
    }

    @Bean
    public UserService userService(UserRepositoryPort repo, PasswordEncoder encoder) {
        return new UserService(repo, encoder);
    }
}
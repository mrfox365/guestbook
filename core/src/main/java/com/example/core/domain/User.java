package com.example.core.domain;

public record User(
        Long id,
        String username,
        String password,
        String role
) {}
package com.example.core.domain;
import java.time.LocalDateTime;

public record Comment(Long id, String author, String text, LocalDateTime createdAt) {}
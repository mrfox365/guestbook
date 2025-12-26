package com.example.core.service;

import com.example.core.exception.CommentTooOldException;
import com.example.core.exception.InvalidCommentDeleteException;
import com.example.core.exception.CommentTextTooLongException;
import com.example.core.ports.CommentRepositoryPort;
import com.example.core.domain.Comment;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public class CommentService {

    private final CommentRepositoryPort repo;

    public CommentService(CommentRepositoryPort repo) {
        this.repo = repo;
    }

    // Методи для читання/запису (перенесені з старого сервісу)
    public List<Comment> getCommentsByUser(Long userId) {
        return repo.findCommentsByUserId(userId);
    }

    public List<Comment> getCommentsByBook(Long bookId) {
        return repo.findCommentsByBookId(bookId);
    }

    public void addComment(Long bookId, Long userId, String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment text cannot be empty");
        }

        if (text.length() > 500) {
            throw new CommentTextTooLongException("Comment is too long (max 500 characters). Current: " + text.length());
        }

        repo.saveComment(bookId, userId, text);
    }

    // Логіка з Лабораторної
    public void delete(long bookId, long commentId, Instant createdAt) {
        if (bookId <= 0 || commentId <= 0) {
            throw new InvalidCommentDeleteException("Book ID and Comment ID must be greater than 0");
        }

        if (createdAt == null) {
            throw new InvalidCommentDeleteException("Creation date cannot be null");
        }

        long hoursPassed = Duration.between(createdAt, Instant.now()).toHours();

        if (hoursPassed >= 24) {
            throw new CommentTooOldException("Comment is older than 24 hours. Deletion is not allowed.");
        }

        repo.deleteComment(bookId, commentId);
    }
}
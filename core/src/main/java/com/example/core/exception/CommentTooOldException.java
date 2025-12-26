package com.example.core.exception;

public class CommentTooOldException extends RuntimeException {
    public CommentTooOldException(String message) {
        super(message);
    }
}
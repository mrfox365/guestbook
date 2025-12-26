package com.example.core.exception;

public class CommentTextTooLongException extends RuntimeException {
    public CommentTextTooLongException(String message) {
        super(message);
    }
}
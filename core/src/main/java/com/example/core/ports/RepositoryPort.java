package com.example.core.ports;

import com.example.core.domain.*;
import java.util.List;
import java.util.Optional;

public interface RepositoryPort {
    // Comment methods
    Comment saveComment(Comment comment);
    List<Comment> findAllComments();
    Optional<Comment> findCommentById(Long id);
    void deleteComment(Long id);

    // Book methods
    List<Book> searchBooks(PageRequest request);
    Optional<Book> findBookById(Long id);
    void initSchema(); // Helper
}
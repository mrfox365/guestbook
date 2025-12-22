package com.example.core.service;

import com.example.core.domain.*;
import com.example.core.ports.RepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class GuestbookService {
    private static final Logger log = LoggerFactory.getLogger(GuestbookService.class);

    @Autowired
    private RepositoryPort repository;

    public GuestbookService() {}

    public Comment addComment(String author, String text) {
        if (author == null || author.isBlank() || text == null || text.isBlank()) {
            throw new IllegalArgumentException("Author and text are required");
        }
        Comment comment = new Comment(null, author, text, LocalDateTime.now());
        Comment saved = repository.saveComment(comment);
        log.info("INFO New comment created: id={}, author={}", saved.id(), saved.author());
        return saved;
    }

    public void deleteComment(Long id) {
        Comment comment = repository.findCommentById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        // Бізнес-правило: видалення тільки протягом 24 годин
        if (comment.createdAt().isBefore(LocalDateTime.now().minusHours(24))) {
            throw new IllegalStateException("Cannot delete comment older than 24 hours");
        }

        repository.deleteComment(id);
        log.info("INFO Comment deleted: id={}", id);
    }

    public List<Book> searchBooks(int page, int size, String sort, String query) {
        // Дефолтні значення
        if (page < 1) page = 1;
        if (size < 1) size = 10;
        if (sort == null || sort.isBlank()) sort = "id";

        return repository.searchBooks(new PageRequest(page, size, sort, query));
    }

    public Book getBook(Long id) {
        return repository.findBookById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
    }
}
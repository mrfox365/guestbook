package com.example.core.service;

import com.example.core.domain.Book;
import com.example.core.domain.Comment;
import com.example.core.domain.User;
import com.example.core.ports.GuestbookRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GuestbookService {

    private final GuestbookRepositoryPort repository;

    // Spring автоматично підставить сюди реалізацію з модуля Persistence
    public GuestbookService(GuestbookRepositoryPort repository) {
        this.repository = repository;
    }

    public List<Book> getAllBooks() {
        return repository.findAllBooks();
    }

    public Book getBook(Long id) {
        return repository.findBookById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
    }

    public void addBook(Book book) {
        repository.saveBook(book);
    }

    public void deleteBook(Long id) {
        repository.deleteBookById(id);
    }

    public List<Comment> getCommentsByUser(Long userId) {
        return repository.findCommentsByUserId(userId);
    }

    public List<Comment> getCommentsByBook(Long bookId) {
        return repository.findCommentsByBookId(bookId);
    }

    public void addComment(Long bookId, Long userId, String text) {
        repository.saveComment(bookId, userId, text);
    }

    public User getUser(Long id) {
        return repository.findUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
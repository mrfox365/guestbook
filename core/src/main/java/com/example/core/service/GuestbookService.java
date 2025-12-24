package com.example.core.service;

import com.example.core.domain.Book;
import com.example.core.domain.PageRequest;
import com.example.core.ports.RepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GuestbookService {
    private static final Logger log = LoggerFactory.getLogger(GuestbookService.class);

    private final RepositoryPort repository;

    public GuestbookService(RepositoryPort repository) {
        this.repository = repository;
    }

    // --- Методи для Книг ---

    public void addBook(String title, String author, String isbn, int year) {
        // Тут можна додати валідацію
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title is required");
        }
        Book book = new Book(null, title, author, isbn, year);

        repository.saveBook(book);
        log.info("INFO New book added: title={}", title);
    }

    public void deleteBook(Long id) {
        // Перевірка існування
        repository.findBookById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        repository.deleteBook(id);
        log.info("INFO Book deleted: id={}", id);
    }

    public List<Book> searchBooks(int page, int size, String sort, String query) {
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
package com.example.core.service;

import com.example.core.domain.Book;
import com.example.core.ports.BookRepositoryPort;

import java.util.List;

public class BookService {
    private final BookRepositoryPort bookRepo;

    public BookService(BookRepositoryPort bookRepo) {
        this.bookRepo = bookRepo;
    }

    public List<Book> getAllBooks() {
        return bookRepo.findAllBooks();
    }

    public Book getBook(Long id) {
        return bookRepo.findBookById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
    }

    public void addBook(Book book) {
        bookRepo.saveBook(book);
    }

    public void deleteBook(Long id) {
        bookRepo.deleteBookById(id);
    }
}
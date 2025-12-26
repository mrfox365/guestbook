package com.example.core.ports;

import com.example.core.domain.Book;
import java.util.List;
import java.util.Optional;

public interface BookRepositoryPort {
    List<Book> findAllBooks();
    Book saveBook(Book book);
    Optional<Book> findBookById(Long id);
    void deleteBookById(Long id);
}
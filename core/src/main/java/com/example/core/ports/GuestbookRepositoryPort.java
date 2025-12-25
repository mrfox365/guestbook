package com.example.core.ports;

import com.example.core.domain.Book;
import com.example.core.domain.Comment;
import com.example.core.domain.User;

import java.util.List;
import java.util.Optional;

public interface GuestbookRepositoryPort {
    // Books
    List<Book> findAllBooks();
    Book saveBook(Book book);
    Optional<Book> findBookById(Long id);
    void deleteBookById(Long id);

    // Comments
    List<Comment> findCommentsByUserId(Long userId);
    List<Comment> findCommentsByBookId(Long bookId);
    void saveComment(Long bookId, Long userId, String text);

    // Users
    Optional<User> findUserById(Long id);
}
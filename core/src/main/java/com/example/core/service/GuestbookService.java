package com.example.core.service;

import com.example.core.domain.Book;
import com.example.core.domain.Comment;
import com.example.core.domain.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.core.ports.GuestbookRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GuestbookService implements UserDetailsService {

    private final GuestbookRepositoryPort repository;
    private final PasswordEncoder passwordEncoder; // Будемо інжектити

    public GuestbookService(GuestbookRepositoryPort repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
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

    public Long getUserId(String username) {
        return repository.findUserByUsername(username)
                .map(User::id)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    // --- Метод для Spring Security ---
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Перетворюємо наш Domain User у Spring Security User
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.username())
                .password(user.password()) // Тут має бути хеш
                .roles(user.role())        // "ADMIN" або "USER"
                .build();
    }

    // --- Метод реєстрації ---
    public void registerUser(String username, String rawPassword) {
        String encodedPassword = passwordEncoder.encode(rawPassword);
        User newUser = new User(null, username, encodedPassword, "USER");
        repository.saveUser(newUser);
    }
}
package com.example.persistence.adapter;

import com.example.core.domain.Book;
import com.example.core.domain.Comment;
import com.example.core.domain.User;
import com.example.core.ports.BookRepositoryPort;
import com.example.core.ports.CommentRepositoryPort;
import com.example.core.ports.UserRepositoryPort;
import com.example.persistence.entity.BookEntity;
import com.example.persistence.entity.CommentEntity;
import com.example.persistence.entity.UserEntity;
import com.example.persistence.repository.BookRepository;
import com.example.persistence.repository.CommentRepository;
import com.example.persistence.repository.UserRepository;
import com.example.persistence.mapper.BookMapper;
import com.example.persistence.mapper.CommentMapper;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Transactional
public class GuestbookRepositoryAdapter implements BookRepositoryPort, CommentRepositoryPort, UserRepositoryPort {

    private final BookRepository bookRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BookMapper bookMapper;
    private final CommentMapper commentMapper;

    public GuestbookRepositoryAdapter(BookRepository bookRepository, CommentRepository commentRepository, UserRepository userRepository, BookMapper bookMapper, CommentMapper commentMapper) {
        this.bookRepository = bookRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.bookMapper = bookMapper;
        this.commentMapper = commentMapper;
    }

    // ==========================================
    // BookRepositoryPort
    // ==========================================
    @Override
    public List<Book> findAllBooks() {
        return bookRepository.findAll().stream()
                .map(bookMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Book saveBook(Book book) {
        BookEntity entity = bookMapper.toEntity(book);
        BookEntity saved = bookRepository.save(entity);
        return bookMapper.toDomain(saved);
    }

    @Override
    public Optional<Book> findBookById(Long id) {
        return bookRepository.findById(id).map(bookMapper::toDomain);
    }

    @Override
    public void deleteBookById(Long id) {
        bookRepository.deleteById(id);
    }

    // ==========================================
    // CommentRepositoryPort
    // ==========================================
    @Override
    public List<Comment> findCommentsByUserId(Long userId) {
        return commentRepository.findByUserId(userId).stream()
                .map(commentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Comment> findCommentsByBookId(Long bookId) {
        return commentRepository.findByBookId(bookId).stream()
                .map(commentMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void saveComment(Long bookId, Long userId, String text) {
        BookEntity book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CommentEntity comment = commentMapper.createEntity(text, user, book);
        commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Long bookId, Long commentId) {
        // Просте видалення за ID.
        // Якщо потрібно перевірити, чи належить коментар книзі, це можна зробити тут,
        // але зазвичай достатньо видалення за ID коментаря.
        commentRepository.deleteById(commentId);
    }

    // ==========================================
    // UserRepositoryPort
    // ==========================================
    @Override
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id)
                .map(u -> new User(u.getId(), u.getUsername(), u.getPassword(), u.getRole()));
    }

    @Override
    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(entity -> new User(
                        entity.getId(),
                        entity.getUsername(),
                        entity.getPassword(),
                        entity.getRole()));
    }

    @Override
    public User saveUser(User user) {
        UserEntity entity = new UserEntity();
        entity.setUsername(user.username());
        entity.setPassword(user.password());
        entity.setRole(user.role());

        var saved = userRepository.save(entity);
        return new User(saved.getId(), saved.getUsername(), saved.getPassword(), saved.getRole());
    }
}
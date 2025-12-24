package com.example.persistence;

import com.example.core.domain.Book;
import com.example.core.domain.Comment;
import com.example.core.domain.PageRequest;
import com.example.core.ports.RepositoryPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcRepository implements RepositoryPort {

    private final JdbcTemplate jdbc;

    public JdbcRepository(DataSource dataSource) {
        this.jdbc = new JdbcTemplate(dataSource);
        initSchema(); // Ініціалізуємо таблиці при запуску
    }

    @Override
    public void initSchema() {
        // Таблиця для коментарів (стара)
        jdbc.execute("CREATE TABLE IF NOT EXISTS guestbook (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "author VARCHAR(255), " +
                "text VARCHAR(1000), " +
                "created_at TIMESTAMP)");

        // Таблиця для КНИГ (нова)
        jdbc.execute("CREATE TABLE IF NOT EXISTS books (" +
                "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                "title VARCHAR(255), " +
                "author VARCHAR(255), " +
                "isbn VARCHAR(50), " +
                "publication_year INT)");
    }

    // --- Реалізація методів для BOOK ---

    @Override
    public void saveBook(Book book) {
        String sql = "INSERT INTO books (title, author, isbn, publication_year) VALUES (?, ?, ?, ?)";
        jdbc.update(sql, book.title(), book.author(), book.isbn(), book.year());
    }

    @Override
    public void deleteBook(Long id) {
        String sql = "DELETE FROM books WHERE id = ?";
        jdbc.update(sql, id);
    }

    @Override
    public List<Book> searchBooks(PageRequest request) {
        // Проста реалізація без складного сортування для прикладу
        String sql = "SELECT * FROM books LIMIT ? OFFSET ?";
        return jdbc.query(sql, bookRowMapper(), request.size(), request.getOffset());
    }

    @Override
    public Optional<Book> findBookById(Long id) {
        String sql = "SELECT * FROM books WHERE id = ?";
        List<Book> books = jdbc.query(sql, bookRowMapper(), id);
        return books.stream().findFirst();
    }

    private RowMapper<Book> bookRowMapper() {
        return (rs, rowNum) -> new Book(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("isbn"),
                rs.getInt("publication_year")
        );
    }

    // --- Реалізація методів для COMMENT (залишаємо, щоб не ламати старий код) ---

    @Override
    public Comment saveComment(Comment comment) {
        String sql = "INSERT INTO guestbook (author, text, created_at) VALUES (?, ?, ?)";
        jdbc.update(sql, comment.author(), comment.text(), comment.createdAt());
        // Для простоти повертаємо той самий об'єкт (в реальності треба отримати згенерований ID)
        return comment;
    }

    @Override
    public List<Comment> findAllComments() {
        return jdbc.query("SELECT * FROM guestbook", commentRowMapper());
    }

    @Override
    public Optional<Comment> findCommentById(Long id) {
        List<Comment> list = jdbc.query("SELECT * FROM guestbook WHERE id = ?", commentRowMapper(), id);
        return list.stream().findFirst();
    }

    @Override
    public void deleteComment(Long id) {
        jdbc.update("DELETE FROM guestbook WHERE id = ?", id);
    }

    private RowMapper<Comment> commentRowMapper() {
        return (rs, rowNum) -> new Comment(
                rs.getLong("id"),
                rs.getString("author"),
                rs.getString("text"),
                rs.getObject("created_at", LocalDateTime.class)
        );
    }
}
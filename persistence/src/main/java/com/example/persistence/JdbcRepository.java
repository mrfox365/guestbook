package com.example.persistence;

import com.example.core.domain.*;
import com.example.core.ports.RepositoryPort;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
public class JdbcRepository implements RepositoryPort {

    private final DataSource dataSource;

    // ДЕМОНСТРАЦІЯ: Constructor Injection (Ін'єкція через конструктор)
    // Spring знайде бін DataSource (створений автоконфігурацією) і передасть сюди.
    public JdbcRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void initialize() {
        initSchema();
    }

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void initSchema() {
        try (Connection conn = getConnection(); Statement st = conn.createStatement()) {
            st.execute("create table if not exists comments (id bigint generated always as identity primary key, author varchar(64), text varchar(1000), created_at timestamp)");
            st.execute("create table if not exists books (id bigint generated always as identity primary key, title varchar(255), author varchar(255), isbn varchar(20), year_published int)");

            // Seed data if empty
            ResultSet rs = st.executeQuery("select count(*) from books");
            if (rs.next() && rs.getInt(1) == 0) {
                st.execute("insert into books (title, author, isbn, year_published) values ('Clean Code', 'Robert Martin', '978-0132350884', 2008)");
                st.execute("insert into books (title, author, isbn, year_published) values ('Effective Java', 'Joshua Bloch', '978-0134685991', 2017)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Comment saveComment(Comment c) {
        try (Connection conn = getConnection();
             PreparedStatement st = conn.prepareStatement("INSERT INTO comments (author, text, created_at) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, c.author());
            st.setString(2, c.text());
            st.setTimestamp(3, Timestamp.valueOf(c.createdAt()));
            st.executeUpdate();
            ResultSet keys = st.getGeneratedKeys();
            if (keys.next()) {
                return new Comment(keys.getLong(1), c.author(), c.text(), c.createdAt());
            }
        } catch (SQLException e) { throw new RuntimeException("DB Error", e); }
        return null;
    }

    @Override
    public Optional<Comment> findCommentById(Long id) {
        try (Connection conn = getConnection();
             PreparedStatement st = conn.prepareStatement("SELECT * FROM comments WHERE id = ?")) {
            st.setLong(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) return Optional.of(mapComment(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    @Override
    public void deleteComment(Long id) {
        try (Connection conn = getConnection();
             PreparedStatement st = conn.prepareStatement("DELETE FROM comments WHERE id = ?")) {
            st.setLong(1, id);
            st.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Comment> findAllComments() {
        // Implement logic similar to searchBooks if needed
        return List.of();
    }

    @Override
    public List<Book> searchBooks(PageRequest req) {
        List<Book> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM books WHERE 1=1 ");

        if (req.query() != null && !req.query().isBlank()) {
            sql.append("AND lower(title) LIKE ? ");
        }

        // Very basic sorting whitelist to prevent SQL Injection
        String safeSort = List.of("title", "author", "year_published").contains(req.sortField()) ? req.sortField() : "id";
        sql.append("ORDER BY ").append(safeSort).append(" LIMIT ? OFFSET ?");

        try (Connection conn = getConnection();
             PreparedStatement st = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (req.query() != null && !req.query().isBlank()) {
                st.setString(paramIndex++, "%" + req.query().toLowerCase() + "%");
            }
            st.setInt(paramIndex++, req.size());
            st.setInt(paramIndex++, req.getOffset());

            ResultSet rs = st.executeQuery();
            while (rs.next()) list.add(mapBook(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return list;
    }

    @Override
    public Optional<Book> findBookById(Long id) {
        try (Connection conn = getConnection();
             PreparedStatement st = conn.prepareStatement("SELECT * FROM books WHERE id = ?")) {
            st.setLong(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.next()) return Optional.of(mapBook(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return Optional.empty();
    }

    private Comment mapComment(ResultSet rs) throws SQLException {
        return new Comment(rs.getLong("id"), rs.getString("author"), rs.getString("text"), rs.getTimestamp("created_at").toLocalDateTime());
    }

    private Book mapBook(ResultSet rs) throws SQLException {
        return new Book(rs.getLong("id"), rs.getString("title"), rs.getString("author"), rs.getString("isbn"), rs.getInt("year_published"));
    }
}
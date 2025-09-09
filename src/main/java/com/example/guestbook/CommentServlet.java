package com.example.guestbook;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;

import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.util.*;

@WebServlet(name="CommentServlet", urlPatterns={"/comments"})
public class CommentServlet extends HttpServlet {
    private Connection conn;
    private ObjectMapper mapper = new ObjectMapper();

    @Override
    public void init() throws ServletException {
        try {
            conn = DriverManager.getConnection(
                "jdbc:h2:file:./data/guest;AUTO_SERVER=TRUE", "sa", ""
            );
            conn.createStatement().executeUpdate(
                "create table if not exists comments (" +
                "id bigint generated always as identity primary key," +
                "author varchar(64) not null," +
                "text varchar(1000) not null)"
            );
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json");
        try (PreparedStatement st = conn.prepareStatement(
                "select id, author, text from comments order by id desc")) {
            ResultSet rs = st.executeQuery();
            List<Map<String, Object>> list = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> c = new HashMap<>();
                c.put("id", rs.getLong("id"));
                c.put("author", rs.getString("author"));
                c.put("text", rs.getString("text"));
                list.add(c);
            }
            mapper.writeValue(resp.getOutputStream(), list);
        } catch (SQLException e) {
            resp.sendError(500, "DB error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String author = req.getParameter("author");
        String text = req.getParameter("text");

        if (author == null || author.isBlank() || author.length() > 64 ||
            text == null || text.isBlank() || text.length() > 1000) {
            resp.sendError(400, "Validation error");
            return;
        }

        try (PreparedStatement st = conn.prepareStatement(
                "insert into comments(author, text) values(?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            st.setString(1, author);
            st.setString(2, text);
            st.executeUpdate();
            ResultSet keys = st.getGeneratedKeys();
            if (keys.next()) {
                long id = keys.getLong(1);
                System.out.printf("INFO New comment: id=%d, author=%s, length=%d%n",
                        id, author, text.length());
            }
            resp.setStatus(204);
        } catch (SQLException e) {
            resp.sendError(500, "DB error");
        }
    }

    @Override
    public void destroy() {
        try { conn.close(); } catch (Exception ignore) {}
    }
}
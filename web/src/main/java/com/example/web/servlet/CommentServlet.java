package com.example.web.servlet;

import com.example.core.domain.Comment;
import com.example.core.service.GuestbookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(urlPatterns = "/comments")
public class CommentServlet extends HttpServlet {
    private GuestbookService service;
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void init(ServletConfig config) {
        this.service = (GuestbookService) config.getServletContext().getAttribute("guestbookService");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String author = req.getParameter("author");
            String text = req.getParameter("text");

            service.addComment(author, text);
            resp.setStatus(201);
        } catch (Exception e) {
            ErrorHandler.handle(resp, e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String idStr = req.getParameter("id");
            if (idStr == null) throw new IllegalArgumentException("ID required");
            service.deleteComment(Long.parseLong(idStr));
            resp.setStatus(204);
        } catch (Exception e) {
            ErrorHandler.handle(resp, e);
        }
    }
}
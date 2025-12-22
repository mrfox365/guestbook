package com.example.web.servlet;

import com.example.core.domain.Book;
import com.example.core.service.GuestbookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = {"/books", "/books/*"})
public class BookServlet extends HttpServlet {
    private GuestbookService service;
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void init(ServletConfig config) {
        this.service = (GuestbookService) config.getServletContext().getAttribute("guestbookService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        String pathInfo = req.getPathInfo();

        try {
            if (pathInfo != null && pathInfo.length() > 1) {
                // GET /books/{id}
                String idStr = pathInfo.substring(1);
                Book book = service.getBook(Long.parseLong(idStr));
                mapper.writeValue(resp.getOutputStream(), book);
            } else {
                // GET /books?q=...&page=...
                String q = req.getParameter("q");
                int page = parseInt(req.getParameter("page"), 1);
                int size = parseInt(req.getParameter("size"), 10);
                String sort = req.getParameter("sort");

                List<Book> books = service.searchBooks(page, size, sort, q);
                mapper.writeValue(resp.getOutputStream(), books);
            }
        } catch (RuntimeException e) {
            ErrorHandler.handle(resp, e);
        }
    }

    private int parseInt(String val, int def) {
        try { return Integer.parseInt(val); } catch (NumberFormatException e) { return def; }
    }
}
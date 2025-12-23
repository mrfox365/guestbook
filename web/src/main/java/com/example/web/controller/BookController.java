package com.example.web.controller;

import com.example.core.domain.Book;
import com.example.core.service.GuestbookService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private final GuestbookService service;

    public BookController(GuestbookService service) {
        this.service = service;
    }

    // GET /books?page=1&size=10&sort=id&q=search
    @GetMapping
    public List<Book> getBooks(@RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "10") int size,
                               @RequestParam(defaultValue = "id") String sort,
                               @RequestParam(required = false) String q) {
        return service.searchBooks(page, size, sort, q);
    }

    // GET /books/{id}
    @GetMapping("/{id}")
    public Book getBook(@PathVariable Long id) {
        return service.getBook(id);
    }
}
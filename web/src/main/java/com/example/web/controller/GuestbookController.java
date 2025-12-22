package com.example.web.controller;

import com.example.core.domain.Book;
import com.example.core.service.GuestbookService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class GuestbookController {

    private final GuestbookService service;
    private final String appName;

    public GuestbookController(GuestbookService service, @Qualifier("appName") String appName) {
        this.service = service;
        this.appName = appName;
    }

    @GetMapping("/info")
    public String info() {
        return "Running: " + appName;
    }

    @GetMapping("/books")
    public List<Book> getBooks(@RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "10") int size,
                               @RequestParam(defaultValue = "id") String sort,
                               @RequestParam(required = false) String q) {
        return service.searchBooks(page, size, sort, q);
    }

    @GetMapping("/books/{id}")
    public Book getBook(@PathVariable Long id) {
        return service.getBook(id);
    }

    @PostMapping("/comments")
    public ResponseEntity<Void> addComment(@RequestParam String author, @RequestParam String text) {
        service.addComment(author, text);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        service.deleteComment(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleValidation(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
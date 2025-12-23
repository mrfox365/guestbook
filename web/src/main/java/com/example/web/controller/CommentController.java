package com.example.web.controller;

import com.example.core.service.GuestbookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final GuestbookService service;

    public CommentController(GuestbookService service) {
        this.service = service;
    }

    // POST /comments
    @PostMapping
    public ResponseEntity<Void> addComment(@RequestParam String author,
                                           @RequestParam String text) {
        service.addComment(author, text);
        // Повертаємо 201 Created
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // DELETE /comments/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        service.deleteComment(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}
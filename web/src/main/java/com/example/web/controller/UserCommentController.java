package com.example.web.controller;

import com.example.core.service.CommentService;
import com.example.core.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@Controller
public class UserCommentController {

    private final CommentService commentService;
    private final UserService userService;

    public UserCommentController(CommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }

    @GetMapping("/users/{id}/comments")
    public String userComments(@PathVariable Long id, Model model) {
        model.addAttribute("comments", commentService.getCommentsByUser(id));
        model.addAttribute("username", userService.getUser(id).username());
        return "user-comments";
    }

    // --- REST API метод для видалення (вимагається завданням) ---
    @PostMapping("/comments/delete")
    @ResponseBody // Це означає, що ми повертаємо JSON, а не HTML
    public ResponseEntity<?> deleteComment(
            @RequestParam Long bookId,
            @RequestParam Long commentId,
            @RequestParam String createdAt // Фронт має передати дату у форматі ISO-8601
    ) {
        // Парсимо рядок дати в Instant
        Instant createdInst = Instant.parse(createdAt);

        commentService.delete(bookId, commentId, createdInst);

        return ResponseEntity.ok(Map.of("message", "Comment deleted successfully"));
    }
}
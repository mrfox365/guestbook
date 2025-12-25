package com.example.web.controller;

import com.example.core.service.GuestbookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class UserCommentController {

    private final GuestbookService service;

    public UserCommentController(GuestbookService service) {
        this.service = service;
    }

    @GetMapping("/users/{id}/comments")
    public String userComments(@PathVariable Long id, Model model) {
        model.addAttribute("comments", service.getCommentsByUser(id));
        model.addAttribute("username", service.getUser(id).username());
        return "user-comments"; // Spring автоматично знайде user-comments.html
    }


}
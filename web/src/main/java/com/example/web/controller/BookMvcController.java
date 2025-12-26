package com.example.web.controller;

import com.example.core.domain.Book;
import com.example.core.service.BookService;
import com.example.core.service.CommentService;
import com.example.core.service.UserService;
import com.example.web.service.MailService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/books")
public class BookMvcController {

    private final BookService bookService;
    private final CommentService commentService;
    private final UserService userService;
    private final MailService mailService;

    public BookMvcController(BookService bookService, CommentService commentService, UserService userService, MailService mailService) {
        this.bookService = bookService;
        this.commentService = commentService;
        this.userService = userService;
        this.mailService = mailService;
    }

    @GetMapping
    public String listBooks(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        return "books";
    }

    @GetMapping("/{id}")
    public String viewBook(@PathVariable Long id, Model model) {
        model.addAttribute("book", bookService.getBook(id));
        model.addAttribute("comments", commentService.getCommentsByBook(id));
        return "book-details";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("book", new Book(null, "", "", "", 2024));
        return "book-form";
    }

    @PostMapping
    public String addBook(@ModelAttribute Book book) {
        bookService.addBook(book);
        mailService.sendNewBookEmail(book);
        return "redirect:/books";
    }

    @PostMapping("/{bookId}/comments")
    public String addComment(@PathVariable Long bookId,
                             @RequestParam String text,
                             Principal principal) {
        Long userId = userService.getUserId(principal.getName());
        commentService.addComment(bookId, userId, text);
        return "redirect:/books/" + bookId;
    }

    @PostMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return "redirect:/books";
    }
}
package com.example.web.controller;

import com.example.core.domain.Book;
import com.example.core.service.GuestbookService;
import com.example.web.service.MailService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/books")
public class BookMvcController {

    private final GuestbookService service;
    private final MailService mailService;

    public BookMvcController(GuestbookService service, MailService mailService) {
        this.service = service;
        this.mailService = mailService;
    }

    @GetMapping
    public String listBooks(Model model) {
        var books = service.searchBooks(1, 100, "id", "");
        model.addAttribute("books", books);
        return "books";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("book", new Book(null, "", "", "", 2024));
        return "book-form";
    }

    @PostMapping
    public String addBook(@ModelAttribute Book book) {
        service.addBook(book.title(), book.author(), book.isbn(), book.year());
        mailService.sendNewBookEmail(book);
        return "redirect:/books";
    }

    @PostMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        try {
            service.deleteBook(id);
        } catch (Exception e) {
            System.err.println("Delete failed: " + e.getMessage());
        }
        return "redirect:/books";
    }
}
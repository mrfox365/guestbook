package com.example.web.controller;

import com.example.core.domain.Book; // Імпортуємо Record з Core
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
        model.addAttribute("books", service.getAllBooks());
        return "books"; // Шаблон Thymeleaf
    }

    @GetMapping("/{id}")
    public String viewBook(@PathVariable Long id, Model model) {
        model.addAttribute("book", service.getBook(id)); // Сама книга
        model.addAttribute("comments", service.getCommentsByBook(id)); // Коментарі до неї
        return "book-details"; // Шаблон Thymeleaf
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        // Створюємо порожній Record для форми (id = null)
        model.addAttribute("book", new Book(null, "", "", "", 2024));
        return "book-form";
    }

    @PostMapping
    public String addBook(@ModelAttribute Book book) {
        service.addBook(book);

        // Відправляємо лист
        mailService.sendNewBookEmail(book);

        return "redirect:/books";
    }

    @PostMapping("/{bookId}/comments")
    public String addComment(@PathVariable Long bookId,
                             @RequestParam Long userId,
                             @RequestParam String text) {
        service.addComment(bookId, userId, text);
        return "redirect:/books/" + bookId;
    }

    @PostMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        service.deleteBook(id);
        return "redirect:/books";
    }
}
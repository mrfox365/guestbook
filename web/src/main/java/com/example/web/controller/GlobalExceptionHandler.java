package com.example.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Controller
@ControllerAdvice
public class GlobalExceptionHandler {

    // 1. Помилки валідації (невірні аргументи, пусті поля)
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleValidation(IllegalArgumentException e, Model model) {
        model.addAttribute("errorTitle", "Помилка валідації");
        model.addAttribute("errorMessage", e.getMessage());
        return "error";
    }

    // 2. Помилки бізнес-логіки (конфлікти станів)
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleBusinessRule(IllegalStateException e, Model model) {
        model.addAttribute("errorTitle", "Операція відхилена");
        model.addAttribute("errorMessage", e.getMessage());
        return "error";
    }

    // 3. НОВЕ: Помилка доступу (Security 403)
    // Спрацьовує, коли юзер залогінений, але не має прав (ролі)
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDenied(AccessDeniedException e, Model model) {
        model.addAttribute("errorTitle", "Доступ заборонено (403)");
        model.addAttribute("errorMessage", "У вас недостатньо прав для виконання цієї дії.");
        return "error";
    }

    // 4. НОВЕ: Сторінка не знайдена (404)
    // (Працює для Spring Boot 3+, якщо ви звертаєтесь до неіснуючих статичних ресурсів або URL)
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(NoResourceFoundException e, Model model) {
        model.addAttribute("errorTitle", "Сторінку не знайдено (404)");
        model.addAttribute("errorMessage", "Ресурс, який ви шукаєте, не існує.");
        return "error";
    }

    // 5. Всі інші помилки (Catch-all)
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleAllExceptions(Exception e, Model model) {
        // Логуємо помилку в консоль, щоб розробник бачив деталі
        e.printStackTrace();

        String message = e.getMessage();

        // Ручна перевірка на "Not Found", якщо сервіс кидає загальний RuntimeException
        if (message != null && message.toLowerCase().contains("not found")) {
            model.addAttribute("errorTitle", "Не знайдено");
            model.addAttribute("errorMessage", message);
            return "error";
        }

        model.addAttribute("errorTitle", "Критична помилка");
        model.addAttribute("errorMessage", "Щось пішло не так. Спробуйте пізніше. (" + e.getClass().getSimpleName() + ")");
        return "error";
    }

    @GetMapping("/access-denied")
    public String accessDenied(Model model) {
        model.addAttribute("errorTitle", "Доступ заборонено (403)");
        model.addAttribute("errorMessage", "Вибачте, але у вас недостатньо прав для виконання цієї дії. Зверніться до адміністратора.");
        return "error"; // Повертає ваш шаблон error.html
    }
}
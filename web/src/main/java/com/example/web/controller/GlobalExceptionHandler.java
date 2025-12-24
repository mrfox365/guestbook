package com.example.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Обробка помилок валідації (наприклад, пусті поля)
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleValidation(IllegalArgumentException e, Model model) {
        model.addAttribute("errorTitle", "Помилка валідації");
        model.addAttribute("errorMessage", e.getMessage());
        return "error"; // Шукає template/error.html
    }

    // Обробка бізнес-правил (наприклад, видалення старого коментаря)
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleBusinessRule(IllegalStateException e, Model model) {
        model.addAttribute("errorTitle", "Операція відхилена");
        model.addAttribute("errorMessage", e.getMessage());
        return "error";
    }

    // Всі інші помилки
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleRuntime(RuntimeException e, Model model) {
        String message = e.getMessage();

        // Перевірка на "Not Found", якщо сервіс кидає RuntimeException
        if (message != null && message.contains("not found")) {
            model.addAttribute("errorTitle", "Не знайдено");
            model.addAttribute("errorMessage", message);
            // Тут статус буде 500, якщо не налаштувати динамічно,
            return "error";
        }

        model.addAttribute("errorTitle", "Критична помилка");
        model.addAttribute("errorMessage", "Щось пішло не так: " + message);
        return "error";
    }
}
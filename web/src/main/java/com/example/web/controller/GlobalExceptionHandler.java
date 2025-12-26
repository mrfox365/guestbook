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

    // 1. Validation errors
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleValidation(IllegalArgumentException e, Model model) {
        model.addAttribute("errorTitle", "Validation Error");
        model.addAttribute("errorMessage", e.getMessage());
        return "error";
    }

    // 2. Business logic errors
    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleBusinessRule(IllegalStateException e, Model model) {
        model.addAttribute("errorTitle", "Operation Declined");
        model.addAttribute("errorMessage", e.getMessage());
        return "error";
    }

    // 3. Security (403)
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDenied(AccessDeniedException e, Model model) {
        model.addAttribute("errorTitle", "Access Denied (403)");
        model.addAttribute("errorMessage", "You do not have permission to perform this action.");
        return "error";
    }

    // 4. Not Found (404)
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(NoResourceFoundException e, Model model) {
        model.addAttribute("errorTitle", "Page Not Found (404)");
        model.addAttribute("errorMessage", "The resource you are looking for does not exist.");
        return "error";
    }

    // 5. Catch-all
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleAllExceptions(Exception e, Model model) {
        e.printStackTrace(); // Log to console

        String message = e.getMessage();

        if (message != null && message.toLowerCase().contains("not found")) {
            model.addAttribute("errorTitle", "Not Found");
            model.addAttribute("errorMessage", message);
            return "error";
        }

        model.addAttribute("errorTitle", "Critical Error");
        model.addAttribute("errorMessage", "Something went wrong. Please try again later. (" + e.getClass().getSimpleName() + ")");
        return "error";
    }

    @GetMapping("/access-denied")
    public String accessDenied(Model model) {
        model.addAttribute("errorTitle", "Access Denied (403)");
        model.addAttribute("errorMessage", "Sorry, you do not have permission to perform this action.");
        return "error";
    }
}
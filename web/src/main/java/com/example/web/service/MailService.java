package com.example.web.service;

import com.example.core.domain.Book;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.internet.MimeMessage;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class MailService {

    private final JavaMailSender mailSender;
    private final EmailTemplateProcessor templateProcessor;

    public MailService(JavaMailSender mailSender, EmailTemplateProcessor templateProcessor) {
        this.mailSender = mailSender;
        this.templateProcessor = templateProcessor;
    }

    public void sendNewBookEmail(Book book) {
        Map<String, Object> model = new HashMap<>();
        // Оскільки Book - це Record, використовуємо методи доступу без get (або стандартні, якщо це клас)
        model.put("title", book.title());
        model.put("author", book.author());
        model.put("year", book.year());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        model.put("added", LocalDateTime.now().format(formatter));

        // Рендеримо HTML
        String html = templateProcessor.process("new_book.ftl", model);

        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");

            helper.setTo("mrfoxxx365@gmail.com");
            helper.setSubject("Нова книга: " + book.title());
            helper.setText(html, true); // true означає, що це HTML
            helper.setFrom("dmitrii.berezhnyi@student.sumdu.edu.ua");

            mailSender.send(message);
            System.out.println("Email sent successfully regarding book: " + book.title());

        } catch (Exception e) {
            // Логуємо помилку, але не зупиняємо програму, щоб книга все одно зберіглась у БД
            System.err.println("Email sending failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
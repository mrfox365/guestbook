package com.example.persistence.mapper;

import com.example.core.domain.Comment;
import com.example.persistence.entity.BookEntity;
import com.example.persistence.entity.CommentEntity;
import com.example.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
@Component
public class CommentMapper {

    public Comment toDomain(CommentEntity entity) {
        if (entity == null) return null;

        // Витягуємо дані про книгу безпечно
        Long bookId = (entity.getBook() != null) ? entity.getBook().getId() : null;
        String bookTitle = (entity.getBook() != null) ? entity.getBook().getTitle() : "Unknown";
        String authorName = (entity.getUser() != null) ? entity.getUser().getUsername() : "Anonymous";

        return new Comment(
                entity.getId(),
                authorName,
                entity.getText(),
                entity.getCreatedAt(),
                bookId,
                bookTitle
        );
    }

    public CommentEntity createEntity(String text, UserEntity user, BookEntity book) {
        CommentEntity entity = new CommentEntity();
        entity.setText(text);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUser(user);
        entity.setBook(book);
        return entity;
    }

}
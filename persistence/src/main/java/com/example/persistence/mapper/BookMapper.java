package com.example.persistence.mapper;

import com.example.core.domain.Book;
import com.example.persistence.entity.BookEntity;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    // Entity -> Domain (DTO)
    public Book toDomain(BookEntity entity) {
        if (entity == null) return null;
        return new Book(
                entity.getId(),
                entity.getTitle(),
                entity.getAuthor(),
                entity.getIsbn(),
                entity.getYear()
        );
    }

    // Domain (DTO) -> Entity
    public BookEntity toEntity(Book book) {
        if (book == null) return null;
        return new BookEntity(
                book.id(),
                book.title(),
                book.author(),
                book.isbn(),
                book.year(),
                null // список коментарів ініціалізується пустим у самому Entity
        );
    }
}
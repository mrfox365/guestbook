package com.example.core.service;

import com.example.core.domain.Book;
import com.example.core.ports.BookRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepositoryPort repo;

    @InjectMocks
    private BookService service;

    @Test
    void getAllBooks_shouldReturnList() {
        // Arrange
        List<Book> books = List.of(
                new Book(1L, "Title 1", "Author 1", "ISBN1", 2000),
                new Book(2L, "Title 2", "Author 2", "ISBN2", 2001)
        );
        when(repo.findAllBooks()).thenReturn(books);

        // Act
        List<Book> result = service.getAllBooks();

        // Assert
        assertEquals(2, result.size());
        verify(repo).findAllBooks();
    }

    @Test
    void getBook_shouldReturnBook_whenExists() {
        Book book = new Book(1L, "Title", "Author", "ISBN", 2020);
        when(repo.findBookById(1L)).thenReturn(Optional.of(book));

        Book result = service.getBook(1L);

        assertNotNull(result);
        assertEquals("Title", result.title());
    }

    @Test
    void getBook_shouldThrowException_whenNotFound() {
        when(repo.findBookById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () ->
                service.getBook(99L)
        );

        assertTrue(exception.getMessage().contains("Book not found"));
    }

    @Test
    void addBook_shouldCallSave() {
        Book book = new Book(null, "New", "Author", "123", 2024);

        service.addBook(book);

        verify(repo).saveBook(book);
    }

    @Test
    void deleteBook_shouldCallDelete() {
        service.deleteBook(10L);
        verify(repo).deleteBookById(10L);
    }
}
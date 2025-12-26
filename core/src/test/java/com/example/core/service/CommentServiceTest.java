package com.example.core.service;

import com.example.core.domain.Comment;
import com.example.core.exception.CommentTooOldException;
import com.example.core.exception.InvalidCommentDeleteException;
import com.example.core.exception.CommentTextTooLongException;
import com.example.core.ports.CommentRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepositoryPort repo;

    @InjectMocks
    private CommentService service;

    // --- Search and Add Tests ---

    @Test
    void getCommentsByBook_shouldReturnList() {
        Comment mockComment = new Comment(
                1L, "User", "Text",
                LocalDateTime.now(), 1L, "Book Title"
        );

        when(repo.findCommentsByBookId(1L)).thenReturn(List.of(mockComment));

        List<Comment> result = service.getCommentsByBook(1L);

        assertEquals(1, result.size());
        verify(repo).findCommentsByBookId(1L);
    }

    @Test
    void getCommentsByUser_shouldReturnList() {
        service.getCommentsByUser(5L);
        verify(repo).findCommentsByUserId(5L);
    }

    @Test
    void addComment_shouldCallSave() {
        service.addComment(1L, 2L, "Nice book!");
        verify(repo).saveComment(1L, 2L, "Nice book!");
    }

    // --- Deletion Logic Tests (using Instant as Service accepts Instant) ---

    @Test
    void delete_shouldThrow_whenIdsInvalid() {
        assertThrows(InvalidCommentDeleteException.class, () ->
                service.delete(0, 1, Instant.now()));

        verifyNoInteractions(repo);
    }

    @Test
    void delete_shouldThrow_whenDateIsNull() {
        assertThrows(InvalidCommentDeleteException.class, () ->
                service.delete(1, 1, null));

        verifyNoInteractions(repo);
    }

    @Test
    void delete_shouldThrow_whenCommentIsTooOld() {
        // Create a date 25 hours ago
        Instant oldDate = Instant.now().minus(25, ChronoUnit.HOURS);

        assertThrows(CommentTooOldException.class, () ->
                service.delete(1, 1, oldDate));

        verifyNoInteractions(repo);
    }

    @Test
    void delete_shouldCallRepo_whenValid() {
        // Create a date 1 hour ago
        Instant freshDate = Instant.now().minus(1, ChronoUnit.HOURS);

        service.delete(10L, 20L, freshDate);

        verify(repo).deleteComment(10L, 20L);
    }

    @Test
    void addComment_shouldThrow_whenTextIsTooLong() {
        // 501 symbols
        String longText = "a".repeat(501);

        assertThrows(CommentTextTooLongException.class, () ->
                service.addComment(1L, 1L, longText)
        );

        verifyNoInteractions(repo);
    }

    @Test
    void addComment_shouldThrow_whenTextIsEmpty() {
        assertThrows(IllegalArgumentException.class, () ->
                service.addComment(1L, 1L, "")
        );
        verifyNoInteractions(repo);
    }
}
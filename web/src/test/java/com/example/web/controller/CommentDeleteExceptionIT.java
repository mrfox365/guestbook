package com.example.web.controller;

import com.example.core.exception.CommentTooOldException;
import com.example.core.service.CommentService;
import com.example.web.GuestbookApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // For Spring Boot 3.4+
// Or import org.springframework.boot.test.mock.mockito.MockBean; // For older versions
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = GuestbookApplication.class)
@AutoConfigureMockMvc
public class CommentDeleteExceptionIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CommentService commentService;

    @Test
    void shouldReturn400_whenCommentIsTooOld() throws Exception {
        // Arrange: Simulate service throwing exception
        doThrow(new CommentTooOldException("Too old comment"))
                .when(commentService).delete(eq(1L), eq(2L), any(Instant.class));

        // Act & Assert
        mockMvc.perform(post("/comments/delete")
                        .param("bookId", "1")
                        .param("commentId", "2")
                        .param("createdAt", "2020-01-01T10:00:00Z")
                        .with(user("user").roles("USER")) // Simulate logged-in user
                        .with(csrf())) // Important for POST requests with Security
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Too old comment"));
    }
}
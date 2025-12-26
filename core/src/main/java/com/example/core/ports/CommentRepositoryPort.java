package com.example.core.ports;

import com.example.core.domain.Comment;
import java.util.List;

public interface CommentRepositoryPort {
    List<Comment> findCommentsByUserId(Long userId);
    List<Comment> findCommentsByBookId(Long bookId);
    void saveComment(Long bookId, Long userId, String text);
    void deleteComment(Long bookId, Long commentId);
}
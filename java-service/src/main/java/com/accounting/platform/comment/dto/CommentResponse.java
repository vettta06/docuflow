package com.accounting.platform.comment.dto;

import com.accounting.platform.entity.Comment;
import java.time.LocalDateTime;

public record CommentResponse(
    Long id,
    Long authorId,
    String authorName,
    Long documentId,
    Long operationId,
    String text,
    LocalDateTime createdAt) {
  public static CommentResponse from(Comment comment) {
    return new CommentResponse(
        comment.getId(),
        comment.getAuthor().getId(),
        comment.getAuthor().getFullName(),
        comment.getDocument() != null ? comment.getDocument().getId() : null,
        comment.getOperation() != null ? comment.getOperation().getId() : null,
        comment.getText(),
        comment.getCreatedAt());
  }
}

package com.accounting.platform.comment.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;

public record CreateCommentRequest(
    Long operationId,
    Long documentId,
    @NotBlank(message = "Текст комментария обязателен") String text) {
  @AssertTrue(
      message =
          "Комментарий должен быть привязан либо к операции, либо к документу, но не к обоим сразу")
  public boolean isValidTarget() {
    return (operationId != null && documentId == null)
        || (operationId == null && documentId != null);
  }
}

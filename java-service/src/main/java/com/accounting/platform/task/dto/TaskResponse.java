package com.accounting.platform.task.dto;

import com.accounting.platform.entity.Task;
import java.time.LocalDateTime;

public record TaskResponse(
    Long id,
    Long operationId,
    Long documentId,
    Long createdBy,
    Long assignedTo,
    String documentType,
    String comment,
    String status,
    LocalDateTime createdAt,
    LocalDateTime fulfilledAt) {
  public static TaskResponse from(Task task) {
    return new TaskResponse(
        task.getId(),
        task.getOperation() != null ? task.getOperation().getId() : null,
        task.getDocument() != null ? task.getDocument().getId() : null,
        task.getCreatedBy() != null ? task.getCreatedBy().getId() : null,
        task.getAssignedTo() != null ? task.getAssignedTo().getId() : null,
        task.getDocumentType(),
        task.getComment(),
        task.getStatus().name(),
        task.getCreatedAt(),
        task.getFulfilledAt());
  }
}

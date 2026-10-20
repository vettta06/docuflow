package com.accounting.platform.document.dto;

import com.accounting.platform.entity.Document;
import java.time.LocalDateTime;

public record DocumentResponse(
    Long id,
    Long organizationId,
    Long operationId,
    Long uploadedBy,
    String documentType,
    String status,
    String fileName,
    Long fileSize,
    String mimeType,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {
  public static DocumentResponse from(Document doc, Long operationId) {
    return new DocumentResponse(
        doc.getId(),
        doc.getOrganization().getId(),
        operationId,
        doc.getUploadedBy() != null ? doc.getUploadedBy().getId() : null,
        doc.getDocumentType().name(),
        doc.getStatus().name(),
        doc.getFileName(),
        doc.getFileSize(),
        doc.getMimeType(),
        doc.getCreatedAt(),
        doc.getUpdatedAt());
  }
}

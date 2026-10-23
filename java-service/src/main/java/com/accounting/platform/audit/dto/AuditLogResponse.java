package com.accounting.platform.audit.dto;

import com.accounting.platform.entity.AuditLog;
import java.time.LocalDateTime;

public record AuditLogResponse(
    Long id,
    String userName,
    String entityType,
    Long entityId,
    String action,
    String details,
    LocalDateTime createdAt) {
  public static AuditLogResponse from(AuditLog log) {
    return new AuditLogResponse(
        log.getId(),
        log.getUser() != null ? log.getUser().getFullName() : "SYSTEM",
        log.getEntityType(),
        log.getEntityId(),
        log.getAction(),
        log.getNewValue(),
        log.getCreatedAt());
  }
}

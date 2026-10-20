package com.accounting.platform.repository;

import com.accounting.platform.entity.AuditLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
  List<AuditLog> findByEntityTypeAndEntityId(String entityType, Long entityId);

  List<AuditLog> findByUserId(Long userId);
}

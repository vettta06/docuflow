package com.accounting.platform.audit;

import com.accounting.platform.entity.AuditLog;
import com.accounting.platform.entity.User;
import com.accounting.platform.repository.AuditLogRepository;
import com.accounting.platform.repository.UserRepository;
import com.accounting.platform.security.SecurityUtils;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditLogService {

  private final AuditLogRepository auditLogRepository;
  private final UserRepository userRepository;

  @Transactional
  public void log(String entityType, Long entityId, String action, String details) {
    var current = SecurityUtils.getCurrentUser();
    User user = null;
    if (current != null) {
      user = userRepository.findById(current.getId()).orElse(null);
    }

    AuditLog auditLog = new AuditLog();
    auditLog.setUser(user);
    auditLog.setEntityType(entityType);
    auditLog.setEntityId(entityId);
    auditLog.setAction(action);
    auditLog.setNewValue(details);
    auditLog.setCreatedAt(LocalDateTime.now());

    auditLogRepository.save(auditLog);
  }

  @Transactional(readOnly = true)
  public java.util.List<com.accounting.platform.audit.dto.AuditLogResponse> getAllLogs() {
    return auditLogRepository.findAll().stream()
        .map(com.accounting.platform.audit.dto.AuditLogResponse::from)
        .toList();
  }
}

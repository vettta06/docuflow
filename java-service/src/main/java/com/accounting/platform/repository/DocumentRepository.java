package com.accounting.platform.repository;

import com.accounting.platform.entity.Document;
import com.accounting.platform.entity.DocumentStatus;
import com.accounting.platform.entity.DocumentType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentRepository extends JpaRepository<Document, Long> {
  List<Document> findByOrganizationId(Long organizationId);

  List<Document> findByStatus(DocumentStatus status);

  List<Document> findByDocumentType(DocumentType documentType);

  List<Document> findByOrganizationIdIn(List<Long> organizationIds);
}

package com.accounting.platform.repository;

import com.accounting.platform.entity.OperationDocument;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationDocumentRepository extends JpaRepository<OperationDocument, Long> {
  List<OperationDocument> findByOperationId(Long operationId);

  List<OperationDocument> findByDocumentId(Long documentId);
}

package com.accounting.platform.repository;

import com.accounting.platform.entity.OperationDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OperationDocumentRepository extends JpaRepository<OperationDocument, Long> {
    List<OperationDocument> findByOperationId(Long operationId);
    List<OperationDocument> findByDocumentId(Long documentId);
}

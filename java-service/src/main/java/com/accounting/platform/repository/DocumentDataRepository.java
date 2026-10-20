package com.accounting.platform.repository;

import com.accounting.platform.entity.DocumentData;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentDataRepository extends JpaRepository<DocumentData, Long> {
  Optional<DocumentData> findByDocumentId(Long documentId);
}

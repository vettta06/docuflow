package com.accounting.platform.repository;

import com.accounting.platform.entity.DocumentData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocumentDataRepository extends JpaRepository<DocumentData, Long> {
    Optional<DocumentData> findByDocumentId(Long documentId);
}

package com.accounting.platform.repository;

import com.accounting.platform.entity.RequiredDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequiredDocumentRepository extends JpaRepository<RequiredDocument, Long> {
    List<RequiredDocument> findByOperationTypeId(Long operationTypeId);
}

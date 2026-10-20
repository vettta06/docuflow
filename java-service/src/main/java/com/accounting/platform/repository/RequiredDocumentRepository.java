package com.accounting.platform.repository;

import com.accounting.platform.entity.RequiredDocument;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequiredDocumentRepository extends JpaRepository<RequiredDocument, Long> {
  List<RequiredDocument> findByOperationTypeId(Long operationTypeId);
}

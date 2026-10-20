package com.accounting.platform.repository;

import com.accounting.platform.entity.OperationType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationTypeRepository extends JpaRepository<OperationType, Long> {
  Optional<OperationType> findByCode(String code);
}

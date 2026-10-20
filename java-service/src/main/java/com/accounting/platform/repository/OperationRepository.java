package com.accounting.platform.repository;

import com.accounting.platform.entity.Operation;
import com.accounting.platform.entity.OperationStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationRepository extends JpaRepository<Operation, Long> {
  List<Operation> findByOrganizationId(Long organizationId);

  List<Operation> findByAccountantId(Long accountantId);

  List<Operation> findByStatus(OperationStatus status);

  List<Operation> findByOrganizationIdIn(List<Long> organizationIds);
}

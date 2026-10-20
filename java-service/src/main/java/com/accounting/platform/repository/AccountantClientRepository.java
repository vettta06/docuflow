package com.accounting.platform.repository;

import com.accounting.platform.entity.AccountantClient;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountantClientRepository extends JpaRepository<AccountantClient, Long> {
  List<AccountantClient> findByAccountantId(Long accountantId);

  List<AccountantClient> findByOrganizationId(Long organizationId);
}

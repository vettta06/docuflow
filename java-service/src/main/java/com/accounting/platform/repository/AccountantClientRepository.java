package com.accounting.platform.repository;

import com.accounting.platform.entity.AccountantClient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountantClientRepository extends JpaRepository<AccountantClient, Long> {
    List<AccountantClient> findByAccountantId(Long accountantId);
    List<AccountantClient> findByOrganizationId(Long organizationId);
}

package com.accounting.platform.repository;

import com.accounting.platform.entity.Organization;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
  Optional<Organization> findByInn(String inn);
}

package com.accounting.platform.manager.dto;

import com.accounting.platform.entity.Organization;
import java.time.LocalDateTime;

public record OrganizationResponse(
    Long id, String name, String inn, String ogrn, String address, LocalDateTime createdAt) {
  public static OrganizationResponse from(Organization o) {
    return new OrganizationResponse(
        o.getId(), o.getName(), o.getInn(), o.getOgrn(), o.getAddress(), o.getCreatedAt());
  }
}

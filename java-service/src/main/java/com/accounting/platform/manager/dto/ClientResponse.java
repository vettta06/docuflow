package com.accounting.platform.manager.dto;

import com.accounting.platform.entity.User;

public record ClientResponse(
    Long id, String email, String fullName, String phone, OrganizationResponse organization) {
  public static ClientResponse from(User user) {
    return new ClientResponse(
        user.getId(),
        user.getEmail(),
        user.getFullName(),
        user.getPhone(),
        user.getOrganization() == null ? null : OrganizationResponse.from(user.getOrganization()));
  }
}

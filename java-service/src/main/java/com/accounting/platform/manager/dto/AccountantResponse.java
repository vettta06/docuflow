package com.accounting.platform.manager.dto;

import com.accounting.platform.entity.Organization;
import com.accounting.platform.entity.User;

import java.util.List;

public record AccountantResponse(
        Long id,
        String email,
        String fullName,
        String phone,
        List<OrganizationResponse> clients
) {
    public static AccountantResponse from(User accountant, List<Organization> organizations) {
        return new AccountantResponse(
                accountant.getId(),
                accountant.getEmail(),
                accountant.getFullName(),
                accountant.getPhone(),
                organizations.stream().map(OrganizationResponse::from).toList()
        );
    }
}

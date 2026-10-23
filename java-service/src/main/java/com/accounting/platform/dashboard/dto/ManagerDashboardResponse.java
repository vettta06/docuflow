package com.accounting.platform.dashboard.dto;

import java.util.List;

public record ManagerDashboardResponse(
    List<AccountantLoad> accountants, long totalOrganizations, long totalDocuments) {}

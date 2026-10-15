package com.accounting.platform.manager.dto;

import jakarta.validation.constraints.NotNull;

public record AssignRequest(

        @NotNull(message = "accountantId обязателен")
        Long accountantId,

        @NotNull(message = "organizationId обязателен")
        Long organizationId
) {
}

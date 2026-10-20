package com.accounting.platform.operation.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CreateOperationRequest(
    @NotNull(message = "typeId обязателен") @Positive(message = "typeId должен быть положительным") Long typeId,
    @NotBlank(message = "Название операции обязательно") @Size(max = 255) String title,
    @DecimalMin(value = "0.0", inclusive = true, message = "Сумма не может быть отрицательной") BigDecimal totalAmount,
    LocalDate operationDate,
    Long organizationId) {}

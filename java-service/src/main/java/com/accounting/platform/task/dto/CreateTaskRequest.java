package com.accounting.platform.task.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateTaskRequest(
    @NotNull(message = "operationId обязателен") Long operationId,
    @NotBlank(message = "Тип документа обязателен") String documentType,
    @NotBlank(message = "Сообщение обязательно") String comment) {}

package com.accounting.platform.document.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangeDocumentStatusRequest(
    @NotBlank(message = "Статус обязателен") String status, String comment) {}

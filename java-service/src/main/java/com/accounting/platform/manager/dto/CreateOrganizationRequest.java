package com.accounting.platform.manager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateOrganizationRequest(
    @NotBlank(message = "Название организации обязательно") @Size(max = 255) String name,
    @NotBlank(message = "ИНН обязателен") @Size(min = 10, max = 12, message = "ИНН должен быть от 10 до 12 цифр") String inn,
    @Size(max = 15) String ogrn,
    @Size(max = 500) String address) {}

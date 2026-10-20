package com.accounting.platform.manager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateClientRequest(
    @NotBlank(message = "Email обязателен") @Email(message = "Некорректный email") String email,
    @NotBlank(message = "Пароль обязателен") @Size(min = 8, max = 72, message = "Пароль должен быть от 8 до 72 символов") String password,
    @NotBlank(message = "Имя обязательно") @Size(max = 255) String fullName,
    @Size(max = 20) String phone,
    @NotBlank(message = "Название организации обязательно") @Size(max = 255) String organizationName,
    @NotBlank(message = "ИНН организации обязателен") @Size(min = 10, max = 12, message = "ИНН должен быть от 10 до 12 цифр") String organizationInn,
    @Size(max = 15) String organizationOgrn,
    @Size(max = 500) String organizationAddress) {}

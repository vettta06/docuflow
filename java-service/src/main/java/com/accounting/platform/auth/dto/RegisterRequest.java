package com.accounting.platform.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank(message = "Email обязателен") @Email(message = "Некорректный email") @Size(max = 255, message = "Email не должен превышать 255 символов") String email,
    @NotBlank(message = "Пароль обязателен") @Size(min = 8, max = 72, message = "Пароль должен быть от 8 до 72 символов") String password,
    @NotBlank(message = "Имя обязательно") @Size(max = 255, message = "Имя не должно превышать 255 символов") String fullName,
    @Size(max = 20, message = "Телефон не должен превышать 20 символов") String phone) {}

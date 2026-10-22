package com.accounting.platform.document.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DocumentFilter(
    Long organizationId,
    String documentType,
    String status,
    String fileName,
    LocalDate dateFrom,
    LocalDate dateTo,
    BigDecimal amountFrom,
    BigDecimal amountTo) {}

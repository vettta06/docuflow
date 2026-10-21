package com.accounting.platform.document.dto;

import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateDocumentDataRequest(
    String number,
    LocalDate date,
    String sellerInn,
    String sellerName,
    String buyerInn,
    String buyerName,
    @DecimalMin(value = "0.0", inclusive = true, message = "Сумма не может быть отрицательной") BigDecimal amount,
    @DecimalMin(value = "0.0", inclusive = true, message = "НДС не может быть отрицательным") BigDecimal vat) {}

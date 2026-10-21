package com.accounting.platform.document.dto;

import com.accounting.platform.entity.Document;
import com.accounting.platform.entity.DocumentData;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record DocumentDetailsResponse(
    Long id,
    String fileName,
    String documentType,
    String status,
    LocalDateTime createdAt,
    String number,
    LocalDate date,
    String sellerInn,
    String sellerName,
    String buyerInn,
    String buyerName,
    BigDecimal amount,
    BigDecimal vat) {
  public static DocumentDetailsResponse from(Document doc, DocumentData data) {
    return new DocumentDetailsResponse(
        doc.getId(),
        doc.getFileName(),
        doc.getDocumentType().name(),
        doc.getStatus().name(),
        doc.getCreatedAt(),
        data != null ? data.getNumber() : null,
        data != null ? data.getDate() : null,
        data != null ? data.getSellerInn() : null,
        data != null ? data.getSellerName() : null,
        data != null ? data.getBuyerInn() : null,
        data != null ? data.getBuyerName() : null,
        data != null ? data.getAmount() : null,
        data != null ? data.getVat() : null);
  }
}

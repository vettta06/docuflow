package com.accounting.platform.operation.dto;

import com.accounting.platform.entity.Operation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record OperationResponse(
        Long id,
        Long organizationId,
        String organizationName,
        Long accountantId,
        OperationTypeResponse type,
        String title,
        BigDecimal totalAmount,
        LocalDate operationDate,
        String status,
        String completenessStatus,
        boolean complete,
        List<RequiredDocumentStatus> requiredDocuments,
        Long createdBy,
        LocalDateTime createdAt,
        LocalDateTime acceptedAt
) {
    public static OperationResponse from(
            Operation op,
            boolean complete,
            List<RequiredDocumentStatus> requiredDocuments
    ) {
        return new OperationResponse(
                op.getId(),
                op.getOrganization().getId(),
                op.getOrganization().getName(),
                op.getAccountant() != null ? op.getAccountant().getId() : null,
                OperationTypeResponse.from(op.getType()),
                op.getTitle(),
                op.getTotalAmount(),
                op.getOperationDate(),
                op.getStatus().name(),
                op.getCompletenessStatus().name(),
                complete,
                requiredDocuments,
                op.getCreatedBy() != null ? op.getCreatedBy().getId() : null,
                op.getCreatedAt(),
                op.getAcceptedAt()
        );
    }
}

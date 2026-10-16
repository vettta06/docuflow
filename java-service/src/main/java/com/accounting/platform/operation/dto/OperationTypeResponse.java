package com.accounting.platform.operation.dto;

import com.accounting.platform.entity.OperationType;

public record OperationTypeResponse(
        Long id,
        String code,
        String name,
        String description
) {
    public static OperationTypeResponse from(OperationType type) {
        return new OperationTypeResponse(
                type.getId(),
                type.getCode(),
                type.getName(),
                type.getDescription()
        );
    }
}

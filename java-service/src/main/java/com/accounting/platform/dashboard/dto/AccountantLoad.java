package com.accounting.platform.dashboard.dto;

public record AccountantLoad(
    Long accountantId,
    String accountantName,
    long activeOperations,
    long waitingClient,
    long confirmedDocs) {}

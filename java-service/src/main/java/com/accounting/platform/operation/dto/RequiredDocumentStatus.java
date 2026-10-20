package com.accounting.platform.operation.dto;

public record RequiredDocumentStatus(String documentType, boolean mandatory, boolean provided) {}

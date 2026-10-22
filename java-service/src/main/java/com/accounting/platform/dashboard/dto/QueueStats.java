package com.accounting.platform.dashboard.dto;

public record QueueStats(long newDocuments, long needsReview, long waitingClient, long confirmed) {}

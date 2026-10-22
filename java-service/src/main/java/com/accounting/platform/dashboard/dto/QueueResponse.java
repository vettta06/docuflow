package com.accounting.platform.dashboard.dto;

import com.accounting.platform.document.dto.DocumentResponse;
import java.util.List;

public record QueueResponse(QueueStats stats, List<DocumentResponse> documents) {}

package com.accounting.platform.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "storage")
public record StorageProperties(
    String uploadDir, List<String> allowedMimeTypes, long maxFileSizeBytes) {}

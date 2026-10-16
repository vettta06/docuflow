package com.accounting.platform.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "storage")
public record StorageProperties(
        String uploadDir,
        List<String> allowedMimeTypes,
        long maxFileSizeBytes
) {
}

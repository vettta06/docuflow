package com.accounting.platform.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bootstrap.manager")
public record BootstrapProperties(String email, String password, String fullName) {}

package com.hms.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "auth.client")
public record AuthClientProperties(String id, String secret, String redirectUri) {}

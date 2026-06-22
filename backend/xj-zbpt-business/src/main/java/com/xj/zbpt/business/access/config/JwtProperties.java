package com.xj.zbpt.business.access.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(String secret, long expirationHours) {
    public JwtProperties {
        if (secret == null || secret.isBlank()) {
            secret = "change-me-in-production-32chars-min!!";
        }
        if (expirationHours <= 0) {
            expirationHours = 8;
        }
    }
}

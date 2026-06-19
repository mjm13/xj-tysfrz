package com.xj.zbpt.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.info")
public record AppInfoProperties(
        String platformName,
        String release,
        boolean maintenance
) {
}

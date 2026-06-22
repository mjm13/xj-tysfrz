package com.xj.zbpt.framework.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(AppInfoProperties.class)
public class AppConfig {
}

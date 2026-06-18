package com.xj.zbpt.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI zbptOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("指标平台 API")
                .description("xj-zbpt 后端接口文档")
                .version("v0.0.1"));
    }
}

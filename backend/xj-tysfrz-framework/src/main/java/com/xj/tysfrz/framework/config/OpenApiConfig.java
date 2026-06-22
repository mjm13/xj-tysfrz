package com.xj.tysfrz.framework.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI platformOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("高校综合身份数据平台 API")
                .description("xj-tysfrz 后端接口文档（OpenAPI 契约来源）")
                .version("v0.0.1"));
    }
}

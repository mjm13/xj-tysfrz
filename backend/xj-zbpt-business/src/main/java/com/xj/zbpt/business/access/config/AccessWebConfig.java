package com.xj.zbpt.business.access.config;

import com.xj.zbpt.business.access.web.CurrentOperatorArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class AccessWebConfig implements WebMvcConfigurer {

    private final CurrentOperatorArgumentResolver currentOperatorArgumentResolver;

    public AccessWebConfig(CurrentOperatorArgumentResolver currentOperatorArgumentResolver) {
        this.currentOperatorArgumentResolver = currentOperatorArgumentResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentOperatorArgumentResolver);
    }
}

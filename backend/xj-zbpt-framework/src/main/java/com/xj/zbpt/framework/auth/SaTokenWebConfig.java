package com.xj.zbpt.framework.auth;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenWebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handle -> SaRouter
                        .match("/**")
                        .notMatch("/api/auth/login")
                        .notMatch("/api/ping/**")
                        .notMatch("/api/system/info")
                        .notMatch("/actuator/**")
                        .notMatch("/swagger-ui/**")
                        .notMatch("/swagger-ui.html")
                        .notMatch("/v3/api-docs/**")
                        .check(r -> StpUtil.checkLogin())))
                .addPathPatterns("/**");
    }
}

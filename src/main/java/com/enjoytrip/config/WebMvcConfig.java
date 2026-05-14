package com.enjoytrip.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final RequestLoggingInterceptor requestLoggingInterceptor;

    public WebMvcConfig(AuthInterceptor authInterceptor, RequestLoggingInterceptor requestLoggingInterceptor) {
        this.authInterceptor = authInterceptor;
        this.requestLoggingInterceptor = requestLoggingInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestLoggingInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/favicon.ico", "/error/**");

        registry.addInterceptor(authInterceptor)
                .addPathPatterns(
                        "/plans/**",
                        "/api/plans/**",
                        "/boards/new", "/boards/*/edit", "/boards/*/delete",
                        "/hotplaces/new", "/hotplaces/*/edit", "/hotplaces/*/delete", "/hotplaces",
                        "/members/**",
                        "/user/logout", "/user/mypage", "/user/modify", "/user/delete"
                )
                .excludePathPatterns(
                        "/",
                        "/attractions/**",
                        "/api/attractions/**",
                        "/user/login", "/user/join",
                        "/css/**", "/favicon.ico", "/error/**"
                );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/css/**").addResourceLocations("/css/");
        registry.addResourceHandler("/uploads/**").addResourceLocations("file:uploads/");
    }
}

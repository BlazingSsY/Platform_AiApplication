package com.starmol.sourcecodereview.config.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.Optional;

import javax.annotation.Resource;

@Configuration
@Order(1)
public class InterceptorConfig implements WebMvcConfigurer {
    @Resource
    private UniAuthInterceptor[] authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        Arrays.stream(Optional.ofNullable(authInterceptor).orElse(new UniAuthInterceptor[0]))
                .forEach(registry::addInterceptor);
    }
}

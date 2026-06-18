package com.starmol.sso.client.rest.config;

import com.starmol.sso.client.rest.properties.SsoProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author huguojun
 */
@Configuration
@ConditionalOnProperty(prefix = "sso.oauth.client.interceptor", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SsoInterceptorConfig implements WebMvcConfigurer {

    @Resource
    private SsoProperties ssoProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SsoAuthInterceptor(ssoProperties));
    }
}

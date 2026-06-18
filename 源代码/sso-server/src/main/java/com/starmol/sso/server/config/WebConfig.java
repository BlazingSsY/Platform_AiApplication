package com.starmol.sso.server.config;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.starmol.sso.server.pojo.dto.param.resolve.OauthAuthorizeParamArgumentResolver;
import com.starmol.sso.server.pojo.dto.param.resolve.OauthFormParamArgumentResolver;
import com.starmol.sso.server.pojo.dto.param.resolve.OauthIntrospectTokenParamArgumentResolver;
import com.starmol.sso.server.pojo.dto.param.resolve.OauthRefreshTokenParamArgumentResolver;
import com.starmol.sso.server.pojo.dto.param.resolve.OauthTokenParamArgumentResolver;

import org.apache.catalina.filters.RemoteIpFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.TimeZone;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new OauthAuthorizeParamArgumentResolver());
        argumentResolvers.add(new OauthFormParamArgumentResolver());
        argumentResolvers.add(new OauthRefreshTokenParamArgumentResolver());
        argumentResolvers.add(new OauthTokenParamArgumentResolver());
        argumentResolvers.add(new OauthIntrospectTokenParamArgumentResolver());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600L);
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (int i = 0; i < converters.size(); i++) {
            if (converters.get(i) instanceof MappingJackson2HttpMessageConverter) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.setPropertyNamingStrategy(new PropertyNamingStrategy.SnakeCaseStrategy());
                objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                objectMapper.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
                converter.setObjectMapper(objectMapper);
                converters.set(i, converter);
                break;
            }
        }
    }

    @Bean
    public RemoteIpFilter addFilter() {
        return new RemoteIpFilter();
    }

}
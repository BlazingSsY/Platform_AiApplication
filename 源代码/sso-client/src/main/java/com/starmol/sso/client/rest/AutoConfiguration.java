package com.starmol.sso.client.rest;

import com.starmol.sso.client.rest.config.SsoInterceptorConfig;
import com.starmol.sso.client.rest.endpoint.SsoCallbackController;
import com.starmol.sso.client.rest.properties.SsoProperties;
import com.starmol.sso.client.rest.service.SsoService;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;


@Configuration
@ConditionalOnClass({SsoService.class})
@EnableConfigurationProperties(SsoProperties.class)
@EnableFeignClients
@Import({SsoCallbackController.class, SsoInterceptorConfig.class})
@EnableScheduling
public class AutoConfiguration {

	@Bean
	@ConditionalOnMissingBean(SsoService.class)
	public SsoService buildSsoService() {
		return new SsoService();
	}
}

package com.starmol.sso.server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;

import lombok.extern.slf4j.Slf4j;


@EnableAsync
@EnableRetry
@ServletComponentScan
@Slf4j
@SpringBootApplication
@EnableFeignClients
public class Application implements CommandLineRunner {

	@Value("${server.port:9091}")
	private String serverPort;

	@Value("${server.servlet.context-path:/sso}")
	private String serverContextPath;

	@Value("${management.server.servlet.context-path:/sso-actuator}")
	private String managementContextPath;

	@Value("${management.server.port:19091}")
	private String managementPort;

	//=================================================================================

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... strings) {
		log.info("=================================Application Startup Success=================================");
	}


}

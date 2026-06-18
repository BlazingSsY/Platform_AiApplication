package com.starmol.portal.backend;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.TimeZone;

import lombok.extern.slf4j.Slf4j;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableAsync
@EnableScheduling
@EnableFeignClients(basePackages = {"com.starmol.portal.backend.service"})
@SpringBootApplication(scanBasePackages = "com.starmol.portal.backend")
@ComponentScan(value = {"com.starmol.portal.backend"}, nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class)
@MapperScan(basePackages = {"com.starmol.portal.backend.repository"})
public class Application {

    public static void main(String[] args) throws UnknownHostException {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        Environment environment = SpringApplication.run(Application.class, args).getEnvironment();
        log.info("\n----------------------------------------------------------\n\t"
                + "Application '{}' is running! Access URLs:\n\t" + "Local: \t\thttp://localhost:{}{}\n\t"
                + "External: \thttp://{}:{}{}\n\t" + "SwaggerUI: \thttp://localhost:{}{}/swagger-ui/index.html\n\t"
                + "----------------------------------------------------------",
            environment.getProperty("spring.application.name"), environment.getProperty("server.port"),
            environment.getProperty("server.servlet.context-path"), InetAddress.getLocalHost().getHostAddress(),
            environment.getProperty("server.port"), environment.getProperty("server.servlet.context-path"),
            environment.getProperty("server.port"), environment.getProperty("server.servlet.context-path"));
    }
}

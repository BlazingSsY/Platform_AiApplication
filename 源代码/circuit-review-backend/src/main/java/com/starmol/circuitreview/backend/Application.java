package com.starmol.circuitreview.backend;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.TimeZone;

import com.starmol.circuitreview.backend.constant.DeploymentTypeEnum;
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
@EnableFeignClients(basePackages = {"com.starmol.circuitreview.backend.service"})
@SpringBootApplication(scanBasePackages = "com.starmol.circuitreview.backend")
@ComponentScan(value = {"com.starmol.circuitreview.backend"}, nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class)
@MapperScan(basePackages = {"com.starmol.circuitreview.backend.repository"})
public class Application {

    public static void main(String[] args) throws UnknownHostException {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        Environment environment = SpringApplication.run(Application.class, args).getEnvironment();
        DeploymentTypeEnum deploymentTypeEnum = DeploymentTypeEnum.getByValue(environment.getProperty("deployment.type"));
        log.info("\n----------------------------------------------------------\n\t"
                + "Application '{}' is running! Deployment type is {}. Access URLs:\n\t" + "Local: \t\thttp://localhost:{}{}\n\t"
                + "External: \thttp://{}:{}{}\n\t" + "SwaggerUI: \thttp://localhost:{}{}/swagger-ui/index.html\n\t"
                + "----------------------------------------------------------",
                environment.getProperty("spring.application.name"), deploymentTypeEnum != null ? deploymentTypeEnum.getName() : "unknown", environment.getProperty("server.port"),
            environment.getProperty("server.servlet.context-path"), InetAddress.getLocalHost().getHostAddress(),
            environment.getProperty("server.port"), environment.getProperty("server.servlet.context-path"),
            environment.getProperty("server.port"), environment.getProperty("server.servlet.context-path"));
    }
}

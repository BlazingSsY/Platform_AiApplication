package com.starmol.portal.backend.config.redis;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;

import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * 条件判断：当且仅当同时满足以下条件时，Redis 相关 Bean 才加载：
 * <ol>
 *   <li>spring.redis.host 不为空</li>
 *   <li>spring.redis.port 不为空</li>
 *   <li>host:port 可以成功建立 TCP 连接</li>
 * </ol>
 */
@Slf4j
public class RedisAvailableCondition implements Condition {

    private static final int CONNECTION_TEST_TIMEOUT_MS = 3000;

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String host = context.getEnvironment().getProperty("spring.redis.host");
        String portStr = context.getEnvironment().getProperty("spring.redis.port");
        String beanName = resolveBeanName(metadata);

        if (StringUtils.isBlank(host) || StringUtils.isBlank(portStr)) {
            log.info("[{}] Redis 配置不完整 (host={}, port={})，跳过加载", beanName, host, portStr);
            return false;
        }

        int port;
        try {
            port = Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            log.warn("[{}] Redis 端口配置无效: port={}，跳过加载", beanName, portStr);
            return false;
        }

        if (isReachable(host, port)) {
            log.info("[{}] Redis 连接检测通过 ({}:{})，加载 Bean", beanName, host, port);
            return true;
        } else {
            log.warn("[{}] Redis 连接检测失败 ({}:{})，跳过 [{}] 加载", host, port, beanName);
            return false;
        }
    }

    /**
     * 从 AnnotatedTypeMetadata 中提取 Bean 的简单类名
     */
    private String resolveBeanName(AnnotatedTypeMetadata metadata) {
        if (metadata instanceof AnnotationMetadata) {
            String className = ((AnnotationMetadata) metadata).getClassName();
            int lastDot = className.lastIndexOf('.');
            return lastDot >= 0 ? className.substring(lastDot + 1) : className;
        }
        return "UnknownBean";
    }

    /**
     * 通过 TCP Socket 检测目标主机端口是否可达
     */
    private boolean isReachable(String host, int port) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), CONNECTION_TEST_TIMEOUT_MS);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

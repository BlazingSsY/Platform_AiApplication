package com.starmol.sso.server.init;

import com.starmol.sso.server.actuator.CustomUPMSApiServerHealthEndpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ApplicationHealthInitRunner implements ApplicationRunner {

    @Autowired
    private CustomUPMSApiServerHealthEndpoint customUPMSApiServerHealthEndpoint;

    @Autowired(required = false)
    private RedisTemplate redisTemplate;

    //=====================================业务处理 start=====================================

    @Override
    public void run(ApplicationArguments args) {
        http();
        redis();
    }

    //=====================================业务处理  end=====================================
    //=====================================私有方法 start=====================================

    private void http() {
        Health customUPMSHealth = customUPMSApiServerHealthEndpoint.health();
        if (customUPMSHealth.getStatus().equals(Status.DOWN)) {
            log.error("启动请求 UPMS 接口失败");
        }
    }

    private void redis() {
        if (redisTemplate == null) {
            log.warn("Redis未配置，跳过Redis健康检查");
            return;
        }
        
        try {
            redisTemplate.getConnectionFactory().getConnection().ping();
            log.info("启动连接 Redis 成功");
        } catch (Exception e) {
            log.error("启动连接 Redis 失败", e);
        }
    }

    //=====================================私有方法  end=====================================

}
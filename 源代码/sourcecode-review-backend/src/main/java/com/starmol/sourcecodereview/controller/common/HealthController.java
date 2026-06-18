package com.starmol.sourcecodereview.controller.common;

import com.starmol.sourcecodereview.aop.Permit;
import com.starmol.sourcecodereview.common.Permission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "健康检查")
@RestController
@RequestMapping("/common/v1/health")
public class HealthController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 健康检查接口，用于检查数据库连通性
     * @return 服务可用时返回200，不可用时返回503
     */
    @GetMapping("/check")
    @Permit(Permission.ALL)
    public ResponseEntity<String> healthCheck() {
        try {
            // 执行简单查询以验证数据库连接
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return ResponseEntity.ok("OK");
        } catch (Exception e) {
            log.error("Health check failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Service unavailable");
        }
    }
}
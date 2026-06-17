package com.starmol.circuitreview.backend.config.runner;

import com.starmol.circuitreview.backend.service.circuitreview.impl.CircuitReviewRuleServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 应用启动后执行的任务
 * 
 * @author system
 * @date 2025-01-07
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class StartupRunner implements ApplicationRunner {

    private final CircuitReviewRuleServiceImpl circuitReviewRuleService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("应用启动完成，开始执行启动任务...");
        
        // 同步规则数据
        circuitReviewRuleService.syncRulesFromExternalService();
        
        log.info("启动任务执行完成");
    }
} 
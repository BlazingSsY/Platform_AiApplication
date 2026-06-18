package com.starmol.sourcecodereview.controller;

import com.starmol.sourcecodereview.bean.dto.GetLogDTO;
import com.starmol.sourcecodereview.common.ResponseWrapper;
import com.starmol.sourcecodereview.service.LogService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import javax.annotation.Resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * 代码审查log控制器
 *
 * @author system
 * @date 2025-01-07
 */
@Slf4j
@RestController
@RequestMapping("/v1/log")
@Tag(name = "代码审查log管理", description = "代码审查log相关接口")
public class LogController {

    @Resource
    private LogService logService;

    @GetMapping
    @Operation(summary = "获取log数据", description = "获取log数据")
    public ResponseWrapper<List<String>> getLogsData(@Parameter(description = "显示行数(范围:0-5000)") @RequestParam(required = false, defaultValue = "1000") Integer num) {
        try {
            List<String> result = logService.getLogs(num);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("获取log数据失败", e);
            return ResponseWrapper.fail("获取log数据失败: " + e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "获取中间层服务的log数据", description = "获取中间层服务的log数据")
    @RequestMapping("/service")
    public ResponseWrapper<List<String>> getServiceLogsData(@Parameter(description = "显示行数(范围:0-5000)") @RequestParam(required = false, defaultValue = "1000") Integer num) {
        try {
            List<String> result = logService.getServiceLogs(num);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("获取中间层服务的log数据失败", e);
            return ResponseWrapper.fail("获取中间层服务的log数据失败: " + e.getMessage());
        }
    }
} 
package com.starmol.circuitreview.backend.controller.common;

import com.starmol.circuitreview.backend.common.ResponseWrapper;
import com.starmol.circuitreview.backend.service.common.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
    public ResponseWrapper<List<String>> getLogsData(@Parameter(description = "显示行数(范围:0-5000)") @RequestParam(required = false, defaultValue = "5000") Integer num, @Parameter(description = "日志名称（debug、fuzzy、result）") @RequestParam(required = false, defaultValue = "debug") String logName) {
        try {
            List<String> result = logService.getLogs(num, logName);
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

    @GetMapping("/download")
    @Operation(summary = "下载log数据", description = "下载log数据为文件")
    public ResponseEntity<byte[]> downloadLogsData(@Parameter(description = "显示行数(范围:0-5000)") @RequestParam(required = false, defaultValue = "5000") Integer num,
                                                    @Parameter(description = "日志名称（debug、fuzzy、result）") @RequestParam(required = false, defaultValue = "debug") String logName) {
        try {
            List<String> result = logService.getLogs(num, logName);
            byte[] fileBytes = String.join(System.lineSeparator(), result).getBytes(StandardCharsets.UTF_8);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            String timestamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
            String fileName = URLEncoder.encode("circuitreview-service_" + timestamp, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            headers.setContentDispositionFormData("attachment;filename*=utf-8''", fileName + ".log");
            headers.setContentLength(fileBytes.length);
            return ResponseEntity.ok().headers(headers).body(fileBytes);
        } catch (Exception e) {
            log.error("下载log数据失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/service/download")
    @Operation(summary = "下载中间层服务的log数据", description = "下载中间层服务的log数据为文件")
    public ResponseEntity<byte[]> downloadServiceLogsData(@Parameter(description = "显示行数(范围:0-5000)") @RequestParam(required = false, defaultValue = "1000") Integer num) {
        try {
            List<String> result = logService.getServiceLogs(num);
            byte[] fileBytes = String.join(System.lineSeparator(), result).getBytes(StandardCharsets.UTF_8);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            String timestamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
            String fileName = URLEncoder.encode("circuitreview-middleware-service_" + timestamp, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            headers.setContentDispositionFormData("attachment;filename*=utf-8''", fileName + ".log");
            headers.setContentLength(fileBytes.length);
            return ResponseEntity.ok().headers(headers).body(fileBytes);
        } catch (Exception e) {
            log.error("下载中间层服务的log数据失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}

package com.starmol.logicreview.controller;

import com.starmol.logicreview.aop.Permit;
import com.starmol.logicreview.bean.dto.SourceCodeReviewStatisticsExportDataDTO;
import com.starmol.logicreview.bean.vo.SourceCodeFileDetailVO;
import com.starmol.logicreview.bean.vo.SourceCodeReviewHomeStatisticsDataVO;
import com.starmol.logicreview.bean.vo.SourceCodeReviewStatisticsDataVO;
import com.starmol.logicreview.bean.vo.SourceCodeReviewStatisticsRequestVO;
import com.starmol.logicreview.bean.vo.StatisticsExportRequestVO;
import com.starmol.logicreview.common.Permission;
import com.starmol.logicreview.common.ResponseWrapper;
import com.starmol.logicreview.service.StatisticsService;
import com.starmol.logicreview.utils.ExcelExportUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.annotation.Resource;

/**
 * 逻辑审查统计控制器
 *
 * @author system
 * @date 2025-01-07
 */
@Slf4j
@RestController
@RequestMapping("/v1/logic-review-statistics")
@Tag(name = "逻辑审查统计管理", description = "逻辑审查统计相关接口")
public class StatisticsController {

    @Resource
    private StatisticsService statisticsService;

    @GetMapping("/home")
    @Operation(summary = "获取逻辑审查首页统计", description = "获取逻辑审查首页统计")
    public ResponseWrapper<SourceCodeReviewHomeStatisticsDataVO> getHomeStatistics() {
        try {
            SourceCodeReviewHomeStatisticsDataVO result = statisticsService.getHomeStatistics();
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("获取逻辑审查首页统计失败", e);
            return ResponseWrapper.fail("获取逻辑审查首页统计失败: " + e.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "获取统计分析页面数据", description = "获取统计分析页面数据")
    public ResponseWrapper<SourceCodeReviewStatisticsDataVO> getStatisticsPageData(@RequestBody SourceCodeReviewStatisticsRequestVO requestVO) {
        try {
            SourceCodeReviewStatisticsDataVO result = statisticsService.getSourceCodeReviewStatistics(requestVO);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("获取统计分析页面数据失败", e);
            return ResponseWrapper.fail("获取统计分析页面数据失败: " + e.getMessage());
        }
    }


    @PostMapping("export-data")
    @Operation(summary = "获取导出统计分析页面数据，只考虑最新版本", description = "获取导出统计分析页面数据，只考虑最新版本")
    public ResponseWrapper<List<SourceCodeReviewStatisticsExportDataDTO>> getStatisticsForExport(@RequestBody StatisticsExportRequestVO requestVO) {
        try {
            List<SourceCodeReviewStatisticsExportDataDTO> result = statisticsService.getStatisticsForExport(requestVO);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("获取统计分析页面数据失败", e);
            return ResponseWrapper.fail("获取统计分析页面数据失败: " + e.getMessage());
        }
    }

    @Permit(value = Permission.ALL)
    @PostMapping("export-excel")
    @Operation(summary = "导出统计分析页面数据为Excel", description = "导出统计分析页面数据为Excel，包含表格和图表")
    public ResponseEntity<byte[]> exportStatisticsToExcel(@RequestBody StatisticsExportRequestVO requestVO) {
        try {
            List<SourceCodeReviewStatisticsExportDataDTO> result = statisticsService.getStatisticsForExport(requestVO);
            byte[] excelData = ExcelExportUtils.exportStatisticsToExcel(result, requestVO);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            String baseFileName = ExcelExportUtils.buildFileName(requestVO);
            // 添加时间戳后缀
            String timestamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
            String fileName = URLEncoder.encode(baseFileName + "_" + timestamp, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            headers.setContentDispositionFormData("attachment;filename*=utf-8''", fileName + ".xlsx");
            headers.setContentLength(excelData.length);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelData);
        } catch (Exception e) {
            log.error("导出统计分析数据为Excel失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("export-table-excel")
    @Operation(summary = "导出统计分析页面表格数据为Excel", description = "导出统计分析页面表格数据为Excel")
    public ResponseEntity<byte[]> exportStatisticsTableToExcel(@RequestBody SourceCodeReviewStatisticsRequestVO requestVO) {
        try {
            List<SourceCodeFileDetailVO> result = statisticsService.getStatisticsFileDetailsByLatestVersion(requestVO);
            byte[] excelData = ExcelExportUtils.exportStatisticsTableToExcel(result);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            // 添加时间戳后缀
            String timestamp = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.now());
            String fileName = URLEncoder.encode("statistics_table" + "_" + timestamp, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            headers.setContentDispositionFormData("attachment;filename*=utf-8''", fileName + ".xlsx");
            headers.setContentLength(excelData.length);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelData);
        } catch (Exception e) {
            log.error("导出统计分析表格数据为Excel失败", e);
            return ResponseEntity.internalServerError().build();
        }
    }
} 
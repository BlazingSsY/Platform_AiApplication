package com.starmol.circuitreview.backend.controller.circuitreview;

import com.starmol.circuitreview.backend.bean.dto.CircuitReviewStatisticsExportDataDTO;
import com.starmol.circuitreview.backend.bean.vo.*;
import com.starmol.circuitreview.backend.common.ResponseWrapper;
import com.starmol.circuitreview.backend.service.circuitreview.HomeStatisticsService;
import com.starmol.circuitreview.backend.service.circuitreview.StatisticsService;
import com.starmol.circuitreview.backend.utils.ExcelExportUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
import java.util.Map;

/**
 * 电路图文件控制器
 *
 * @author system
 * @date 2025-01-07
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/circuit-statistics")
@Tag(name = "统计管理", description = "统计相关接口")
public class StatisticsController {

    private final StatisticsService statisticsService;
    private final HomeStatisticsService homeStatisticsService;

    @GetMapping("/home")
    @Operation(summary = "获取电路审查首页统计", description = "获取电路审查首页统计")
    public ResponseWrapper<CircuitReviewHomeStatisticsDataVO> getHomeStatistics() {
        try {
            CircuitReviewHomeStatisticsDataVO result = homeStatisticsService.getHomeStatistics();
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("获取电路审查首页统计失败", e);
            return ResponseWrapper.fail("获取电路审查首页统计失败: " + e.getMessage());
        }
    }

    @PostMapping
    @Operation(summary = "获取统计分析页面数据，只考虑最新版本", description = "获取统计分析页面数据，只考虑最新版本")
    public ResponseWrapper<CircuitReviewStatisticsDataVO> getStatisticsPageDataForLatestVersion(@RequestBody CircuitReviewStatisticsRequestVO requestVO) {
        try {
            CircuitReviewStatisticsDataVO result = statisticsService.getStatisticsPageDataForLatestVersion(requestVO);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("获取统计分析页面数据失败", e);
            return ResponseWrapper.fail("获取统计分析页面数据失败: " + e.getMessage());
        }
    }

    @GetMapping("rule-type")
    @Operation(summary = "获取统计分析页面规则统计数据，只考虑最新版本", description = "获取统计分析页面规则统计数据，只考虑最新版本")
    public ResponseWrapper<Map<String, Map<String, Object>>> getRuleTypeStatisticsData(@RequestParam Long resultId) {
        try {
            Map<String, Map<String, Object>> result = statisticsService.getRuleTypeChartDataByResultId(resultId);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("获取统计分析页面数据失败", e);
            return ResponseWrapper.fail("获取统计分析页面数据失败: " + e.getMessage());
        }
    }

    @PostMapping("export-data")
    @Operation(summary = "获取导出统计分析页面数据，只考虑最新版本", description = "获取导出统计分析页面数据，只考虑最新版本")
    public ResponseWrapper<List<CircuitReviewStatisticsExportDataDTO>> getStatisticsForExport(@RequestBody StatisticsExportRequestVO requestVO) {
        try {
            List<CircuitReviewStatisticsExportDataDTO> result = statisticsService.getStatisticsForExport(requestVO);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("获取统计分析页面数据失败", e);
            return ResponseWrapper.fail("获取统计分析页面数据失败: " + e.getMessage());
        }
    }

    @PostMapping("export-excel")
    @Operation(summary = "导出统计分析页面数据为Excel", description = "导出统计分析页面数据为Excel，包含表格和图表")
    public ResponseEntity<byte[]> exportStatisticsToExcel(@RequestBody StatisticsExportRequestVO requestVO) {
        try {
            List<CircuitReviewStatisticsExportDataDTO> result = statisticsService.getStatisticsForExport(requestVO);
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
    public ResponseEntity<byte[]> exportStatisticsTableToExcel(@RequestBody CircuitReviewStatisticsRequestVO requestVO) {
        try {
            List<CircuitFileDetailVO> result = statisticsService.getStatisticsFileDetailsByLatestVersion(requestVO);
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
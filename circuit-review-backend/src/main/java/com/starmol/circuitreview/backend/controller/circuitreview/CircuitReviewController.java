package com.starmol.circuitreview.backend.controller.circuitreview;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starmol.circuitreview.backend.aop.Permit;
import com.starmol.circuitreview.backend.bean.vo.CircuitReviewRequestVO;
import com.starmol.circuitreview.backend.bean.vo.CircuitReviewResultVO;
import com.starmol.circuitreview.backend.common.Permission;
import com.starmol.circuitreview.backend.model.circuitreview.CircuitReviewResult;
import com.starmol.circuitreview.backend.service.circuitreview.CircuitReviewResultService;
import com.starmol.circuitreview.backend.common.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

/**
 * 电路审查结果控制器
 *
 * @author system
 * @date 2025-01-07
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/circuit-review")
@Tag(name = "电路审查管理", description = "电路审查相关接口")
public class CircuitReviewController {

    private final CircuitReviewResultService circuitReviewResultService;

    @Value("${deployment.type}")
    private Integer deploymentType;

    @PostMapping
    @Operation(summary = "电路审查", description = "创建新的电路审查")
    public ResponseWrapper<Long> createCircuitReview(@RequestBody CircuitReviewRequestVO circuitReviewRequestVO) {
        try {
            Long taskId = circuitReviewResultService.submitCircuitReview(circuitReviewRequestVO);
            return ResponseWrapper.success(taskId);
        } catch (Exception e) {
            log.error("创建电路审查结果失败", e);
            return ResponseWrapper.fail("创建电路审查结果失败: " + e.getMessage());
        }
    }

    @Permit(Permission.ALL)
    @GetMapping("deployment-type")
    @Operation(summary = "电路审查", description = "获取电路审查部署类型")
    public ResponseWrapper<Integer> getDeploymentType() {
        try {
            return ResponseWrapper.success(deploymentType);
        } catch (Exception e) {
            log.error("获取电路审查部署类型失败", e);
            return ResponseWrapper.fail("获取电路审查部署类型失败: " + e.getMessage());
        }
    }

}

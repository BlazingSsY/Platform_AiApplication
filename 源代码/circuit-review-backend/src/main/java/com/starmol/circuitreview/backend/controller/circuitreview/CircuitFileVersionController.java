package com.starmol.circuitreview.backend.controller.circuitreview;

import com.starmol.circuitreview.backend.bean.vo.CircuitFileVersionVO;
import com.starmol.circuitreview.backend.bean.vo.CircuitReviewResultVO;
import com.starmol.circuitreview.backend.bean.vo.FileExistCheckResultVO;
import com.starmol.circuitreview.backend.common.ResponseWrapper;
import com.starmol.circuitreview.backend.constant.FileSecretLevelEnum;
import com.starmol.circuitreview.backend.model.base.DeleteDTO;
import com.starmol.circuitreview.backend.service.circuitreview.CircuitFileService;
import com.starmol.circuitreview.backend.service.circuitreview.CircuitFileVersionService;
import com.starmol.circuitreview.backend.service.circuitreview.CircuitReviewResultService;
import com.starmol.circuitreview.backend.utils.ExceptionUtils;
import com.starmol.circuitreview.backend.utils.StringUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 电路图文件版本控制器
 *
 * @author system
 * @date 2025-01-07
 */
@Slf4j
@RestController
@RequestMapping("/v1/circuit-file-version")
@Tag(name = "电路图文件版本管理", description = "电路图文件版本相关接口")
public class CircuitFileVersionController {
    @Resource
    private CircuitFileVersionService circuitFileVersionService;

    @Resource
    private CircuitFileService circuitFileService;

    @Resource
    private CircuitReviewResultService circuitReviewResultService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "创建电路图文件版本(直接上传文件)", description = "创建新的电路图文件版本(直接上传文件)")
    public ResponseWrapper<CircuitFileVersionVO> createCircuitFileVersion(@RequestParam("file") MultipartFile file, @RequestParam("fileId") Long fileId, @RequestParam("fileSecretLevelEnum") FileSecretLevelEnum fileSecretLevelEnum) {
        try {
            CircuitFileVersionVO result = circuitFileVersionService.createCircuitFileVersion(file, fileId, fileSecretLevelEnum);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("创建电路图文件失败", e);
            return ResponseWrapper.fail("创建电路图文件失败: " + e.getMessage());
        }
    }

    @GetMapping(value = "/check-in-audit")
    @Operation(summary = "检查该文件是否存在正在复核的版本", description = "检查该文件是否存在正在复核的版本")
    public ResponseWrapper<FileExistCheckResultVO> checkCircuitFileInAudit(@RequestParam("fileId") Long fileId) {
        try {
            FileExistCheckResultVO result = circuitFileService.checkCircuitFileInAudit(fileId);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("检查该文件是否存在正在复核的版本失败", e);
            return ResponseWrapper.fail("检查该文件是否存在正在复核的版本失败: " + e.getMessage());
        }
    }

    @GetMapping("/{versionId}/result")
    @Operation(summary = "获取电路图文件版本的所有审查结果", description = "根据ID获取电路图文件版本的所有审查结果")
    public ResponseWrapper<List<CircuitReviewResultVO>> getCircuitFileVersionResults(
            @Parameter(description = "电路图文件ID") @PathVariable Long versionId) {
        try {
            List<CircuitReviewResultVO> result = circuitReviewResultService.getCircuitFileVersionResults(versionId);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("获取电路图文件版本的所有审查结果失败", e);
            return ResponseWrapper.fail("获取电路图文件版本的所有审查结果失败: " + e.getMessage());
        }
    }

    /**
     * 根据删除对象列表安全删除文件
     */
    @Operation(summary = "粉碎文件指定的版本", description = "粉碎文件指定的版本")
    @PatchMapping
    public ResponseWrapper<String> removeCircuitFileSafely(@RequestBody List<DeleteDTO> removeObjects) {
        ResponseWrapper<String> wrapper = new ResponseWrapper<>();
        String formatIds = StringUtil.cutString(removeObjects.stream().map(StringUtil::getLogDescription).collect(Collectors.joining(",")), 200);
        try {
            circuitFileVersionService.removeCircuitFilesWithTransaction(removeObjects);
            wrapper.setContent(removeObjects.stream().map(m -> m.getId().toString()).collect(Collectors.joining(",")));
            wrapper.setSucc(true);
            wrapper.setMsg("粉碎文件成功");
        } catch (Exception e) {
            wrapper.setSucc(false);
            wrapper.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Remove CircuitFile error:%s", e.getMessage()), e);
        }
        return wrapper;
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取电路图文件版本", description = "根据ID获取电路图文件版本详情")
    public ResponseWrapper<CircuitFileVersionVO> getCircuitFile(
            @Parameter(description = "电路图文件版本ID") @PathVariable Long id) {
        try {
            CircuitFileVersionVO result = circuitFileVersionService.getCircuitFileVersionVOByVersionId(id);
            if (result == null) {
                return ResponseWrapper.fail("电路图文件不存在");
            }
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("获取电路图文件失败", e);
            return ResponseWrapper.fail("获取电路图文件失败: " + e.getMessage());
        }
    }


}

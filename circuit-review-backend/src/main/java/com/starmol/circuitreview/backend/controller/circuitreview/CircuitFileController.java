package com.starmol.circuitreview.backend.controller.circuitreview;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starmol.circuitreview.backend.bean.dto.ChangeFileRecycleStatusDTO;
import com.starmol.circuitreview.backend.bean.dto.CircuitFileDTO;
import com.starmol.circuitreview.backend.bean.dto.FileNameDTO;
import com.starmol.circuitreview.backend.bean.vo.*;
import com.starmol.circuitreview.backend.common.ResponseWrapper;
import com.starmol.circuitreview.backend.constant.CircuitFileSortFieldEnum;
import com.starmol.circuitreview.backend.constant.FileSecretLevelEnum;
import com.starmol.circuitreview.backend.constant.SortDirectionEnum;
import com.starmol.circuitreview.backend.model.base.DeleteDTO;
import com.starmol.circuitreview.backend.model.circuitreview.CircuitFile;
import com.starmol.circuitreview.backend.service.circuitreview.CircuitFileService;
import com.starmol.circuitreview.backend.service.circuitreview.CircuitFileVersionService;
import com.starmol.circuitreview.backend.utils.ExceptionUtils;
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
 * 电路图文件控制器
 *
 * @author system
 * @date 2025-01-07
 */
@Slf4j
@RestController
@RequestMapping("/v1/circuit-file")
@Tag(name = "电路图文件管理", description = "电路图文件相关接口")
public class CircuitFileController {

    @Resource
    private CircuitFileService circuitFileService;

    @Resource
    private CircuitFileVersionService circuitFileVersionService;

    @PostMapping
    @Operation(summary = "创建电路图文件", description = "创建新的电路图文件")
    public ResponseWrapper<CircuitFileVO> createCircuitFile(@RequestBody CircuitFileDTO circuitFile) {
        try {
            CircuitFileVO result = circuitFileService.createCircuitFile(circuitFile);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("创建电路图文件失败", e);
            return ResponseWrapper.fail("创建电路图文件失败: " + e.getMessage());
        }
    }

    @PostMapping(value = "/check-exists")
    @Operation(summary = "检查当前用户下的同名文件是否存在", description = "检查当前用户下的同名文件是否存在(直接上传文件)")
    public ResponseWrapper<FileExistCheckResultVO> checkCircuitFileExist(@RequestBody FileNameDTO fileNameDTO) {
        try {
            String fileName = fileNameDTO.getFileName();
            if(fileNameDTO.getIsTestFile() != null) {
                if(!fileNameDTO.getIsTestFile().equals(0)) {
                    if(!fileName.startsWith("test-")) {
                        fileName = "test-" + fileName;
                    }
                }
            }
            FileExistCheckResultVO result = circuitFileService.checkFileExist(fileName);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("检查当前用户下的同名文件失败", e);
            return ResponseWrapper.fail("检查当前用户下的同名文件失败: " + e.getMessage());
        }
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "创建电路图文件(直接上传文件)", description = "创建新的电路图文件(直接上传文件)")
    public ResponseWrapper<CircuitFileVO> createCircuitFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "fileSecretLevelEnum", required = false) FileSecretLevelEnum fileSecretLevelEnum,
            @RequestParam(value = "compatibleModels", required = false) String compatibleModels,
            @RequestParam(value = "productModel", required = false) String productModel,
            @RequestParam(value = "productName", required = false) String productName,
            @RequestParam(value = "diagramNumber", required = false) String diagramNumber,
            @RequestParam(value = "diagramVersion", required = false) String diagramVersion,
            @RequestParam(value = "isTestFile") Integer isTestFile) {
        try {
            CircuitFileVO result = circuitFileService.createCircuitFile(file, fileSecretLevelEnum, compatibleModels, productModel, productName, diagramNumber, diagramVersion, isTestFile);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("创建电路图文件失败", e);
            return ResponseWrapper.fail("创建电路图文件失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新电路图文件", description = "根据ID更新电路图文件")
    public ResponseWrapper<CircuitFileVO> updateCircuitFile(
            @Parameter(description = "电路图文件ID") @PathVariable Long id,
            @RequestBody CircuitFile circuitFile) {
        try {
            CircuitFileVO result = circuitFileService.updateCircuitFile(id, circuitFile);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("更新电路图文件失败", e);
            return ResponseWrapper.fail("更新电路图文件失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取电路图文件", description = "根据ID获取电路图文件详情")
    public ResponseWrapper<CircuitFileVO> getCircuitFile(
            @Parameter(description = "电路图文件ID") @PathVariable Long id) {
        try {
            CircuitFileVO result = circuitFileService.getCircuitFileVOById(id);
            if (result == null) {
                return ResponseWrapper.fail("电路图文件不存在");
            }
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("获取电路图文件失败", e);
            return ResponseWrapper.fail("获取电路图文件失败: " + e.getMessage());
        }
    }

    @GetMapping("/{fileId}/version")
    @Operation(summary = "获取电路图文件所有版本", description = "根据ID获取电路图文件所有版本")
    public ResponseWrapper<List<CircuitFileVersionVO>> getCircuitFileVersions(
            @Parameter(description = "电路图文件ID") @PathVariable Long fileId) {
        try {
            List<CircuitFileVersionVO> result = circuitFileVersionService.getCircuitFileVersionVOById(fileId);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("获取电路图文件所有版本失败", e);
            return ResponseWrapper.fail("获取电路图文件所有版本失败: " + e.getMessage());
        }
    }

    @GetMapping("/{fileId}/version-result")
    @Operation(summary = "获取电路图文件所有版本和审查结果", description = "根据ID获取电路图文件所有版本和审查结果")
    public ResponseWrapper<List<CircuitFileVersionAndResultVO>> getCircuitFileAllVersionAndResult(
            @Parameter(description = "电路图文件ID") @PathVariable Long fileId) {
        try {
            List<CircuitFileVersionAndResultVO> result = circuitFileVersionService.getCircuitFileVersionAndResultVOById(fileId);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("获取电路图文件所有版本和审查结果失败", e);
            return ResponseWrapper.fail("获取电路图文件所有版本和审查结果失败: " + e.getMessage());
        }
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询电路图文件详情", description = "分页查询电路图文件详情，包括最新的审查结果")
    public ResponseWrapper<IPage<CircuitFileDetailVO>> getCircuitFilePage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Long pageNumber,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long pageSize,
            @Parameter(description = "单位Id") @RequestParam(required = false) Long depId,
            @Parameter(description = "用户Id") @RequestParam(required = false) Long userId,
            @Parameter(description = "文件名") @RequestParam(required = false) String fileName,
            @Parameter(description = "排序字段") @RequestParam(required = false) CircuitFileSortFieldEnum sortField,
            @Parameter(description = "排序方向") @RequestParam(required = false) SortDirectionEnum sortDirection) {
        try {
            Page<CircuitFileDetailVO> page = new Page<>(pageNumber, pageSize);
            IPage<CircuitFileDetailVO> result = circuitFileService.getCircuitFileDetailVOPage(page, depId, userId, fileName, sortField, sortDirection);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("分页查询电路图文件失败", e);
            return ResponseWrapper.fail("分页查询电路图文件失败: " + e.getMessage());
        }
    }

    @GetMapping("/recycle/page")
    @Operation(summary = "分页文件回收站中的电路图文件", description = "分页查询回收站中的电路图文件列表")
    public ResponseWrapper<IPage<CircuitFileDetailVO>> getRecycledCircuitFilePage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Long pageNumber,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long pageSize,
            @Parameter(description = "单位Id") @RequestParam(required = false) Long depId,
            @Parameter(description = "用户Id") @RequestParam(required = false) Long userId,
            @Parameter(description = "文件名") @RequestParam(required = false) String fileName) {
        try {
            Page<CircuitFile> page = new Page<>(pageNumber, pageSize);
            IPage<CircuitFileDetailVO> result = circuitFileService.getRecycledCircuitFileVOPage(page, depId, userId, fileName);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("分页查询回收站中的电路图文件失败", e);
            return ResponseWrapper.fail("分页查询回收站中的电路图文件失败: " + e.getMessage());
        }
    }

    @PutMapping("/is-recycle")
    @Operation(summary = "修改文件'是否移入文件回收站'状态", description = "修改文件'是否移入文件回收站'状态")
    public ResponseWrapper<Boolean> changeCircuitFileRecycle(@RequestBody ChangeFileRecycleStatusDTO changeFileRecycleStatusDTO) {
        try {
            Boolean result = circuitFileService.changeCircuitFileRecycle(changeFileRecycleStatusDTO);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("分页查询回收站中的电路图文件失败", e);
            return ResponseWrapper.fail("分页查询回收站中的电路图文件失败: " + e.getMessage());
        }
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "删除电路图文件", description = "根据ID删除电路图文件")
    public ResponseWrapper<String> deleteCircuitFile(
            @Parameter(description = "电路图文件ID") @PathVariable Long id) {
        try {
            boolean result = circuitFileService.removeById(id);
            if (result) {
                return ResponseWrapper.success("删除成功");
            } else {
                return ResponseWrapper.fail("删除失败");
            }
        } catch (Exception e) {
            log.error("删除电路图文件失败", e);
            return ResponseWrapper.fail("删除电路图文件失败: " + e.getMessage());
        }
    }

    /**
     * 根据删除对象列表安全删除文件
     */
    @Operation(summary = "粉碎文件", description = "粉碎文件所有版本")
    @PatchMapping
    public ResponseWrapper<String> removeCircuitFileSafely(@RequestBody List<DeleteDTO> removeObjects) {
        ResponseWrapper<String> wrapper = new ResponseWrapper<>();
        try {
            circuitFileService.removeCircuitFilesWithTransaction(removeObjects);
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

    /**
     * 根据删除对象列表安全删除文件
     */
    @Operation(summary = "批量删除文件审查结果", description = "批量删除文件审查结果")
    @PatchMapping("result")
    public ResponseWrapper<String> removeCircuitFileReviewResult(@RequestBody List<DeleteDTO> removeObjects) {
        ResponseWrapper<String> wrapper = new ResponseWrapper<>();
        try {
            circuitFileService.removeCircuitFileReviewResult(removeObjects);
            wrapper.setContent(removeObjects.stream().map(m -> m.getId().toString()).collect(Collectors.joining(",")));
            wrapper.setSucc(true);
            wrapper.setMsg("批量删除文件审查结果成功");
        } catch (Exception e) {
            wrapper.setSucc(false);
            wrapper.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Remove CircuitFile review result error:%s", e.getMessage()), e);
        }
        return wrapper;
    }

} 
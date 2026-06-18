package com.starmol.sourcecodereview.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starmol.sourcecodereview.bean.bo.PulverizeFileResultBO;
import com.starmol.sourcecodereview.bean.dto.ChangeFileRecycleStatusDTO;
import com.starmol.sourcecodereview.bean.dto.FileNameDTO;
import com.starmol.sourcecodereview.bean.dto.SourceCodeFileDTO;
import com.starmol.sourcecodereview.bean.vo.*;
import com.starmol.sourcecodereview.common.ResponseWrapper;
import com.starmol.sourcecodereview.constant.FileSecretLevelEnum;
import com.starmol.sourcecodereview.model.base.DeleteDTO;
import com.starmol.sourcecodereview.model.codereview.SourceCodeFile;
import com.starmol.sourcecodereview.service.SourceCodeFileService;
import com.starmol.sourcecodereview.service.SourceCodeFileVersionService;
import com.starmol.sourcecodereview.utils.ExceptionUtils;
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
 * 代码文件控制器
 *
 * @author system
 * @date 2025-01-07
 */
@Slf4j
@RestController
@RequestMapping("/v1/source-code-file")
@Tag(name = "代码文件管理", description = "代码文件相关接口")
public class SourceCodeFileController {

    @Resource
    private SourceCodeFileService sourceCodeFileService;

    @Resource
    private SourceCodeFileVersionService sourceCodeFileVersionService;

    @PostMapping
    @Operation(summary = "创建代码文件", description = "创建新的代码文件")
    public ResponseWrapper<SourceCodeFileVO> createSourceCodeFile(@RequestBody SourceCodeFileDTO sourceCodeFile) {
        try {
            SourceCodeFileVO result = sourceCodeFileService.createSourceCodeFile(sourceCodeFile);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("创建代码文件失败", e);
            return ResponseWrapper.fail("创建代码文件失败: " + e.getMessage());
        }
    }

    @PostMapping(value = "/check-exists")
    @Operation(summary = "检查当前用户下的同名文件是否存在", description = "检查当前用户下的同名文件是否存在(直接上传文件)")
    public ResponseWrapper<FileExistCheckResultVO> checkSourceCodeFileExist(@RequestBody FileNameDTO fileNameDTO) {
        try {
            String fileName = fileNameDTO.getFileName();
            if(fileNameDTO.getIsTestFile() != null) {
                if(!fileNameDTO.getIsTestFile().equals(0)) {
                    if(!fileName.startsWith("test-")) {
                        fileName = "test-" + fileName;
                    }
                }
            }
            FileExistCheckResultVO result = sourceCodeFileService.checkFileExist(fileNameDTO.getFileName());
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("检查当前用户下的同名文件失败", e);
            return ResponseWrapper.fail("检查当前用户下的同名文件失败: " + e.getMessage());
        }
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "创建代码文件(直接上传文件)", description = "创建新的代码文件(检查后无同名文件,直接上传文件)")
    public ResponseWrapper<SourceCodeFileVO> createSourceCodeFile(@RequestParam("file") MultipartFile file,
                                                                  @RequestParam(value = "fileSecretLevelEnum", required = false) FileSecretLevelEnum fileSecretLevelEnum,
                                                                  @RequestParam(value = "compatibleModels", required = false) String compatibleModels,
                                                                  @RequestParam(value = "productModel", required = false) String productModel,
                                                                  @RequestParam(value = "productName", required = false) String productName,
                                                                  @RequestParam(value = "configName", required = false) String configName,
                                                                  @RequestParam(value = "codeFileVersion", required = false) String codeFileVersion,
                                                                  @RequestParam(value = "isTestFile", required = false, defaultValue = "0") Integer isTestFile
    ) {
        try {
            SourceCodeFileVO result = sourceCodeFileService.createSourceCodeFile(file, fileSecretLevelEnum, compatibleModels, productModel, productName, configName, codeFileVersion, isTestFile);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("创建代码文件失败", e);
            return ResponseWrapper.fail("创建代码文件失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新代码文件", description = "根据ID更新代码文件")
    public ResponseWrapper<SourceCodeFileVO> updateSourceCodeFile(
            @Parameter(description = "代码文件ID") @PathVariable Long id,
            @RequestBody SourceCodeFile sourceCodeFile) {
        try {
            SourceCodeFileVO result = sourceCodeFileService.updateSourceCodeFile(id, sourceCodeFile);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("更新代码文件失败", e);
            return ResponseWrapper.fail("更新代码文件失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取代码文件", description = "根据ID获取代码文件详情")
    public ResponseWrapper<SourceCodeFileVO> getSourceCodeFile(
            @Parameter(description = "代码文件ID") @PathVariable Long id) {
        try {
            SourceCodeFileVO result = sourceCodeFileService.getSourceCodeFileVOById(id);
            if (result == null) {
                return ResponseWrapper.fail("代码文件不存在");
            }
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("获取代码文件失败", e);
            return ResponseWrapper.fail("获取代码文件失败: " + e.getMessage());
        }
    }

    @GetMapping("/{fileId}/version")
    @Operation(summary = "获取代码文件所有版本", description = "根据ID获取代码文件所有版本")
    public ResponseWrapper<List<SourceCodeFileVersionVO>> getSourceCodeFileVersions(
            @Parameter(description = "代码文件ID") @PathVariable Long fileId) {
        try {
            List<SourceCodeFileVersionVO> result = sourceCodeFileVersionService.getSourceCodeFileVersionVOById(fileId);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("获取代码文件所有版本失败", e);
            return ResponseWrapper.fail("获取代码文件所有版本失败: " + e.getMessage());
        }
    }

    @GetMapping("/{fileId}/version-result")
    @Operation(summary = "获取代码文件所有版本和审查结果", description = "根据ID获取代码文件所有版本和审查结果")
    public ResponseWrapper<List<SourceCodeFileVersionAndResultVO>> getSourceCodeFileAllVersionAndResult(
            @Parameter(description = "代码文件ID") @PathVariable Long fileId) {
        try {
            List<SourceCodeFileVersionAndResultVO> result = sourceCodeFileVersionService.getSourceCodeFileVersionAndResultVOById(fileId);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("获取代码文件所有版本和审查结果失败", e);
            return ResponseWrapper.fail("获取代码文件所有版本和审查结果失败: " + e.getMessage());
        }
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询代码文件详情", description = "分页查询代码文件详情，包括最新的审查结果")
    public ResponseWrapper<IPage<SourceCodeFileDetailVO>> getSourceCodeFilePage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Long pageNumber,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long pageSize,
            @Parameter(description = "单位Id") @RequestParam(required = false) Long depId,
            @Parameter(description = "用户Id") @RequestParam(required = false) Long userId,
            @Parameter(description = "文件名") @RequestParam(required = false) String fileName) {
        try {
            Page<SourceCodeFileDetailVO> page = new Page<>(pageNumber, pageSize);
            IPage<SourceCodeFileDetailVO> result = sourceCodeFileService.getSourceCodeFileDetailVOPage(page, depId, userId, fileName);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("分页查询代码文件失败", e);
            return ResponseWrapper.fail("分页查询代码文件失败: " + e.getMessage());
        }
    }

    @GetMapping("/recycle/page")
    @Operation(summary = "获取回收站代码文件分页", description = "获取回收站代码文件分页")
    public ResponseWrapper<IPage<SourceCodeFileVO>> getRecycledSourceCodeFilePage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "文件名") @RequestParam(required = false) String fileName) {
        try {
            Page<SourceCodeFile> page = new Page<>(pageNum, pageSize);
            IPage<SourceCodeFileVO> result = sourceCodeFileService.getRecycledSourceCodeFileVOPage(page, fileName);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("获取回收站代码文件分页失败", e);
            return ResponseWrapper.fail("获取回收站代码文件分页失败: " + e.getMessage());
        }
    }

    @PostMapping("/is-recycle")
    @Operation(summary = "变更代码文件回收站状态", description = "变更代码文件回收站状态")
    public ResponseWrapper<Boolean> changeSourceCodeFileRecycleStatus(@RequestBody ChangeFileRecycleStatusDTO changeFileRecycleStatusDTO) {
        try {
            Boolean result = sourceCodeFileService.changeSourceCodeFileRecycleStatus(changeFileRecycleStatusDTO);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("变更代码文件回收站状态失败", e);
            return ResponseWrapper.fail("变更代码文件回收站状态失败: " + e.getMessage());
        }
    }


    @DeleteMapping("/{id}")
    @Operation(summary = "删除代码文件", description = "根据ID删除代码文件")
    public ResponseWrapper<String> deleteCircuitFile(
            @Parameter(description = "代码文件ID") @PathVariable Long id) {
        try {
            boolean result = sourceCodeFileService.removeById(id);
            if (result) {
                return ResponseWrapper.success("删除成功");
            } else {
                return ResponseWrapper.fail("删除失败");
            }
        } catch (Exception e) {
            log.error("删除代码文件失败", e);
            return ResponseWrapper.fail("删除代码文件失败: " + e.getMessage());
        }
    }

    /**
     * 根据删除对象列表安全删除文件
     */
    @Operation(summary = "粉碎文件", description = "粉碎文件所有版本")
    @PatchMapping
    public ResponseWrapper<PulverizeFileResultBO> removeSourceCodeFileSafely(@RequestBody DeleteDTO removeObject) {
        ResponseWrapper<PulverizeFileResultBO> wrapper = new ResponseWrapper<>();
        try {
            PulverizeFileResultBO pulverizeFileResultBO = sourceCodeFileService.removeSourceCodeFilesWithTransaction(removeObject);
            wrapper.setContent(pulverizeFileResultBO);
            wrapper.setSucc(true);
            wrapper.setMsg("粉碎文件成功");
        } catch (Exception e) {
            wrapper.setSucc(false);
            wrapper.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Remove SourceCodeFile error:%s", e.getMessage()), e);
        }
        return wrapper;
    }
} 
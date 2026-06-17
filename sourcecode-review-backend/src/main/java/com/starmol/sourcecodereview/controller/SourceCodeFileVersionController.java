package com.starmol.sourcecodereview.controller;

import com.starmol.sourcecodereview.bean.vo.SourceCodeFileVersionVO;
import com.starmol.sourcecodereview.bean.vo.SourceCodeReviewResultVO;
import com.starmol.sourcecodereview.common.ResponseWrapper;
import com.starmol.sourcecodereview.constant.FileSecretLevelEnum;
import com.starmol.sourcecodereview.model.base.DeleteDTO;
import com.starmol.sourcecodereview.service.SourceCodeFileVersionService;
import com.starmol.sourcecodereview.service.SourceCodeReviewResultService;
import com.starmol.sourcecodereview.utils.ExceptionUtils;
import com.starmol.sourcecodereview.utils.StringUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * 代码文件版本控制器
 *
 * @author system
 * @date 2025-01-07
 */
@Slf4j
@RestController
@RequestMapping("/v1/source-code-file-version")
@Tag(name = "代码文件版本管理", description = "代码文件版本相关接口")
public class SourceCodeFileVersionController {
    @Resource
    private SourceCodeFileVersionService sourceCodeFileVersionService;

    @Autowired
    private SourceCodeReviewResultService sourceCodeReviewResultService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "创建代码文件版本(直接上传文件)", description = "创建新的代码文件版本(检查后有同名文件,带着原文件的fileId上传文件)")
    public ResponseWrapper<SourceCodeFileVersionVO> createSourceCodeFileVersion(@RequestParam("file") MultipartFile file,
                                                                                @RequestParam("fileId") Long fileId,
                                                                                @RequestParam("fileSecretLevelEnum") FileSecretLevelEnum fileSecretLevelEnum,
                                                                                @RequestParam(value = "compatibleModels", required = false) String compatibleModels,
                                                                                @RequestParam(value = "productModel", required = false) String productModel,
                                                                                @RequestParam(value = "productName", required = false) String productName,
                                                                                @RequestParam(value = "configName", required = false) String configName,
                                                                                @RequestParam(value = "codeFileVersion", required = false) String codeFileVersion
    ) {
        try {
            SourceCodeFileVersionVO result = sourceCodeFileVersionService.createSourceCodeFileVersion(file, fileId, fileSecretLevelEnum, compatibleModels, productModel, productName, configName, codeFileVersion);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("创建代码文件失败", e);
            return ResponseWrapper.fail("创建代码文件失败: " + e.getMessage());
        }
    }

    @GetMapping("/{versionId}/result")
    @Operation(summary = "获取代码文件版本的所有审查结果", description = "根据ID获取代码文件版本的所有审查结果")
    public ResponseWrapper<List<SourceCodeReviewResultVO>> getSourceCodeFileVersionResults(
            @Parameter(description = "代码文件ID") @PathVariable Long versionId,
            @RequestParam(required = false, defaultValue = "false") Boolean includeAllStatus
    ) {
        try {
            List<SourceCodeReviewResultVO> result = sourceCodeReviewResultService.getSourceCodeFileVersionResults(versionId, includeAllStatus);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("获取代码文件版本的所有审查结果失败", e);
            return ResponseWrapper.fail("获取代码文件版本的所有审查结果失败: " + e.getMessage());
        }
    }

    /**
     * 根据删除对象列表安全删除文件
     */
    @Operation(summary = "粉碎文件指定的版本", description = "粉碎文件指定的版本")
    @PatchMapping
    public ResponseWrapper<String> removeSourceCodeFileSafely(@RequestBody List<DeleteDTO> removeObjects) {
        ResponseWrapper<String> wrapper = new ResponseWrapper<>();
        String formatIds = StringUtil.cutString(removeObjects.stream().map(StringUtil::getLogDescription).collect(Collectors.joining(",")), 200);
        try {
            sourceCodeFileVersionService.removeSourceCodeFilesWithTransaction(removeObjects);
            wrapper.setContent(removeObjects.stream().map(m -> m.getId().toString()).collect(Collectors.joining(",")));
            wrapper.setSucc(true);
            wrapper.setMsg("粉碎文件成功");
        } catch (Exception e) {
            wrapper.setSucc(false);
            wrapper.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Remove SourceCodeFile error:%s", e.getMessage()), e);
        }
        return wrapper;
    }

    @GetMapping("/file-link/{fileId}")
    public  ResponseWrapper<Map<String, String>>  getExportLink(@PathVariable String fileId, @RequestParam(required = false) String fileName) throws IOException {
        try {
            Map<String, String> result =sourceCodeFileVersionService.getExportLink(fileId, fileName);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("获取代码文件连接地址", e);
            return ResponseWrapper.fail("获取代码文件连接地址失败: " + e.getMessage());
        }
    }
}

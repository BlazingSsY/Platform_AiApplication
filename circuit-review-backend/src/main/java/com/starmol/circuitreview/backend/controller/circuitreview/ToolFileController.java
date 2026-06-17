package com.starmol.circuitreview.backend.controller.circuitreview;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starmol.circuitreview.backend.bean.vo.ToolFileVO;
import com.starmol.circuitreview.backend.common.ResponseWrapper;
import com.starmol.circuitreview.backend.model.base.DeleteDTO;
import com.starmol.circuitreview.backend.service.circuitreview.ToolFileService;
import com.starmol.circuitreview.backend.utils.ExceptionUtils;
import com.starmol.circuitreview.backend.utils.StringUtil;

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

import java.util.List;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * 工具文件控制器
 *
 * @author system
 * @date 2025-01-07
 */
@Slf4j
@RestController
@RequestMapping("/v1/tool-file")
@Tag(name = "工具文件管理", description = "工具文件相关接口")
public class ToolFileController {

    @Autowired
    private ToolFileService toolFileService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "创建工具文件(直接上传文件)", description = "创建新的工具文件(直接上传文件)")
    public ResponseWrapper<ToolFileVO> createToolFile(@RequestParam("file") MultipartFile file, String toolName, String comments) {
        try {
            ToolFileVO result = toolFileService.createToolFile(file, toolName, comments);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("创建工具文件失败", e);
            return ResponseWrapper.fail("创建工具文件失败: " + e.getMessage());
        }
    }

    /**
     * 根据删除对象列表安全删除文件
     */
    @Operation(summary = "删除文件", description = "删除文件(安全删除)")
    @PatchMapping
    public ResponseWrapper<String> removeToolFileSafely(@RequestBody List<DeleteDTO> removeObjects) {
        ResponseWrapper<String> wrapper = new ResponseWrapper<>();
        try {
            toolFileService.removeToolFilesWithTransaction(removeObjects);
            wrapper.setContent(removeObjects.stream().map(m -> m.getId().toString()).collect(Collectors.joining(",")));
            wrapper.setSucc(true);
            wrapper.setMsg("删除文件成功");
        } catch (Exception e) {
            wrapper.setSucc(false);
            wrapper.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Remove ToolFile error:%s", e.getMessage()), e);
        }
        return wrapper;
    }


    @GetMapping("/page")
    @Operation(summary = "分页查询工具文件详情", description = "分页查询工具文件详情，包括最新的审查结果")
    public ResponseWrapper<IPage<ToolFileVO>> getToolFilePage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Long pageNumber,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long pageSize,
            @Parameter(description = "文件名") @RequestParam(required = false) String fileName) {
        try {
            IPage<ToolFileVO> result = toolFileService.getToolFileVOPage(pageNumber, pageSize, fileName);
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("分页查询工具文件失败", e);
            return ResponseWrapper.fail("分页查询工具文件失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取工具文件", description = "根据ID获取工具文件详情")
    public ResponseWrapper<ToolFileVO> getToolFile(
            @Parameter(description = "工具文件ID") @PathVariable Long id) {
        try {
            ToolFileVO result = toolFileService.getToolFileVOById(id);
            if (result == null) {
                return ResponseWrapper.fail("工具文件不存在");
            }
            return ResponseWrapper.success(result);
        } catch (Exception e) {
            log.error("获取工具文件失败", e);
            return ResponseWrapper.fail("获取工具文件失败: " + e.getMessage());
        }
    }

} 
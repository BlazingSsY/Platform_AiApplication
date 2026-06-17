package com.starmol.portal.backend.controller.application;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starmol.portal.backend.aop.Permit;
import com.starmol.portal.backend.bean.dto.ApplicationDTO;
import com.starmol.portal.backend.bean.dto.CustomMultipartFile;
import com.starmol.portal.backend.bean.dto.StatusChangeDTO;
import com.starmol.portal.backend.bean.vo.ApplicationVO;
import com.starmol.portal.backend.common.Permission;
import com.starmol.portal.backend.common.ResponseWrapper;
import com.starmol.portal.backend.model.base.DeleteDTO;
import com.starmol.portal.backend.service.application.ApplicationService;
import com.starmol.portal.backend.service.common.StorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "应用管理")
@RestController
@RequestMapping(value = "/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {
    private final ApplicationService applicationService;
    private final StorageService storageService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 创建应用
     */
    @Operation(summary = "创建应用", description = "创建应用")
    @PostMapping
    public ResponseWrapper<ApplicationVO> createApplication(@RequestBody ApplicationDTO applicationDTO) {
        ApplicationVO applicationVO = applicationService.createApplication(applicationDTO);
        return ResponseWrapper.success(applicationVO);
    }

    /**
     * 批量创建应用
     */
    @Operation(summary = "批量创建应用", description = "通过上传包含ApplicationDTO列表的JSON文件批量创建应用")
    @PostMapping("/batch")
    @SneakyThrows
    public ResponseWrapper<Integer> batchCreateApplications(@RequestParam("file") MultipartFile jsonFile) {
        // 解析JSON文件得到ApplicationDTO列表
        List<ApplicationDTO> applicationDTOs = objectMapper.readValue(jsonFile.getBytes(), 
                objectMapper.getTypeFactory().constructCollectionType(List.class, ApplicationDTO.class));

        int successCount = 0;
        // 遍历每个ApplicationDTO
        for (ApplicationDTO applicationDTO : applicationDTOs) {
            // 根据module和name生成图标文件名
            String iconName = applicationDTO.getModule() + "-" + applicationDTO.getName() + ".png";
            
            // 查找图标文件
            Path iconPath = Paths.get("data/icons", iconName);
            if (Files.exists(iconPath)) {
                // 如果图标文件存在，则读取并上传
                byte[] iconBytes = Files.readAllBytes(iconPath);
                MultipartFile iconMultipartFile = new CustomMultipartFile(
                        "icon", iconName, "image/png", iconBytes);
                
                // 上传图标文件并获取fileId
                String fileId = storageService.uploadFile(iconMultipartFile);
                
                // 设置ApplicationDTO的icon字段
                applicationDTO.setIcon(fileId);
            }
            
            // 调用ApplicationService.createApplication创建应用
            applicationService.createApplication(applicationDTO);
            successCount++;
        }
        
        return ResponseWrapper.success(successCount);
    }

    /**
     * 根据删除对象列表安全删除应用
     */
    @Operation(summary = "删除应用", description = "删除应用(安全删除)")
    @PatchMapping
    public ResponseWrapper<String> deleteApplicationSafely(@RequestBody List<DeleteDTO> deleteObjects) {
        String result = applicationService.deleteApplications(deleteObjects);
        return ResponseWrapper.success(result);
    }

    /**
     * 根据id修改应用
     */
    @Operation(summary = "修改应用", description = "根据id修改应用")
    @Parameters(
            {
                    @Parameter(in = ParameterIn.PATH, name = "id", schema = @Schema(type = "long"), description = "应用id")
            }
    )
    @PutMapping("/{id}")
    public ResponseWrapper<ApplicationVO> updateApplication(@PathVariable Long id, @RequestBody ApplicationDTO applicationDTO) {
        ApplicationVO applicationVO = applicationService.updateApplication(id, applicationDTO);
        return ResponseWrapper.success(applicationVO);
    }
    
    /**
     * 修改应用状态
     */
    @Operation(summary = "修改应用状态", description = "修改应用状态（上线/下线）")
    @PutMapping("/status")
    public ResponseWrapper<Boolean> changeApplicationStatus(@RequestBody StatusChangeDTO statusChangeDTO) {
        Boolean result = applicationService.changeStatus(statusChangeDTO.getId(), statusChangeDTO.getStatus());
        return ResponseWrapper.success(result);
    }

    /**
     * 分页查询应用
     */
    @Operation(summary = "分页查询应用", description = "分页查询应用")
    @Parameters(
            {
                    @Parameter(in = ParameterIn.QUERY, name = "pageNumber", schema = @Schema(type = "int"), description = "页码", example = "1"),
                    @Parameter(in = ParameterIn.QUERY, name = "pageSize", schema = @Schema(type = "int"), description = "每页显示条数", example = "10"),
                    @Parameter(in = ParameterIn.QUERY, name = "name", schema = @Schema(type = "string"), description = "应用名称")
            }
    )
    @GetMapping("/pages")
    public ResponseWrapper<IPage<ApplicationVO>> getApplicationListByPage(@RequestParam(defaultValue = "1") Long pageNumber,
                                                                        @RequestParam(defaultValue = "10") Long pageSize,
                                                                        @RequestParam(required = false) String name) {
        IPage<ApplicationVO> applicationVOPage = applicationService.getApplicationListByPage(pageNumber, pageSize, name);
        return ResponseWrapper.success(applicationVOPage);
    }

    /**
     * 获取应用列表
     */
    @Permit(value = Permission.ALL)
    @Operation(summary = "获取应用列表", description = "获取所有应用列表")
    @GetMapping
    public ResponseWrapper<Map<String, List<ApplicationVO>>> getApplicationList() {
        Map<String, List<ApplicationVO>> applicationVOs = applicationService.getApplicationList();
        return ResponseWrapper.success(applicationVOs);
    }

    /**
     * 根据id获取应用详情
     */
    @Permit(value = Permission.ALL)
    @Operation(summary = "查看应用详情", description = "根据应用id获取应用详情")
    @Parameters(
            {
                    @Parameter(in = ParameterIn.PATH, name = "id", schema = @Schema(type = "string"), description = "应用id")
            }
    )
    @GetMapping("/{id}")
    public ResponseWrapper<ApplicationVO> getApplicationDetail(@PathVariable String id) {
        ApplicationVO applicationVO = applicationService.getApplicationDetail(id);
        return ResponseWrapper.success(applicationVO);
    }
}
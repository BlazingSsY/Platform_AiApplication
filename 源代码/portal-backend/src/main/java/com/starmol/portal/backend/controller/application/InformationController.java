package com.starmol.portal.backend.controller.application;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starmol.portal.backend.aop.Permit;
import com.starmol.portal.backend.bean.dto.InfoAppendFileDTO;
import com.starmol.portal.backend.bean.dto.InformationDTO;
import com.starmol.portal.backend.bean.dto.StatusChangeDTO;
import com.starmol.portal.backend.bean.dto.InformationConfigDTO;
import com.starmol.portal.backend.bean.vo.InfoAppendFileVO;
import com.starmol.portal.backend.bean.vo.InformationVO;
import com.starmol.portal.backend.bean.vo.SimpleItemVO;
import com.starmol.portal.backend.bean.vo.InformationConfigVO;
import com.starmol.portal.backend.common.Permission;
import com.starmol.portal.backend.common.ResponseWrapper;
import com.starmol.portal.backend.config.InformationConfig;
import com.starmol.portal.backend.model.base.DeleteDTO;
import com.starmol.portal.backend.service.application.InfoAppendFileService;
import com.starmol.portal.backend.service.application.InformationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "资讯管理")
@RestController
@RequestMapping(value = "/v1/informations")
@RequiredArgsConstructor
public class InformationController {
    private final InformationService informationService;
    private final InfoAppendFileService infoAppendFileService;
    private final InformationConfig informationConfig;

    /**
     * 创建资讯
     */
    @Operation(summary = "创建资讯", description = "创建资讯")
    @PostMapping
    public ResponseWrapper<InformationVO> createInformation(@RequestBody InformationDTO informationDTO) {
        InformationVO informationVO = informationService.createInformation(informationDTO);
        return ResponseWrapper.success(informationVO);
    }

    /**
     * 根据删除对象列表安全删除资讯
     */
    @Operation(summary = "删除资讯", description = "删除资讯(安全删除)")
    @PatchMapping
    public ResponseWrapper<String> deleteInformationSafely(@RequestBody List<DeleteDTO> deleteObjects) {
        String result = informationService.deleteInformations(deleteObjects);
        return ResponseWrapper.success(result);
    }

    /**
     * 根据id修改资讯
     */
    @Operation(summary = "修改资讯", description = "根据id修改资讯")
    @Parameters(
            {
                    @Parameter(in = ParameterIn.PATH, name = "id", schema = @Schema(type = "long"), description = "资讯id")
            }
    )
    @PutMapping("/{id}")
    public ResponseWrapper<InformationVO> updateInformation(@PathVariable Long id, @RequestBody InformationDTO informationDTO) {
        InformationVO informationVO = informationService.updateInformation(id, informationDTO);
        return ResponseWrapper.success(informationVO);
    }
    
    /**
     * 修改资讯状态
     */
    @Operation(summary = "修改资讯状态", description = "修改资讯状态（发布/下线）")
    @PutMapping("/status")
    public ResponseWrapper<Boolean> changeInformationStatus(@RequestBody StatusChangeDTO statusChangeDTO) {
        Boolean result = informationService.changeStatus(statusChangeDTO.getId(), statusChangeDTO.getStatus());
        return ResponseWrapper.success(result);
    }

    /**
     * 分页查询资讯
     */
    @Operation(summary = "分页查询资讯", description = "分页查询资讯")
    @Parameters(
            {
                    @Parameter(in = ParameterIn.QUERY, name = "pageNumber", schema = @Schema(type = "int"), description = "页码", example = "1"),
                    @Parameter(in = ParameterIn.QUERY, name = "pageSize", schema = @Schema(type = "int"), description = "每页显示条数", example = "10"),
                    @Parameter(in = ParameterIn.QUERY, name = "title", schema = @Schema(type = "string"), description = "资讯标题")
            }
    )
    @GetMapping("/pages")
    public ResponseWrapper<IPage<InformationVO>> getInformationListByPage(@RequestParam(defaultValue = "1") Long pageNumber,
                                                                        @RequestParam(defaultValue = "10") Long pageSize,
                                                                        @RequestParam(required = false) String title) {
        IPage<InformationVO> informationVOPage = informationService.getInformationListByPage(pageNumber, pageSize, title);
        return ResponseWrapper.success(informationVOPage);
    }

    /**
     * 获取资讯列表
     */
    @Permit(value = Permission.ALL)
    @Operation(summary = "获取资讯列表", description = "获取所有资讯列表")
    @GetMapping
    public ResponseWrapper<List<InformationVO>> getInformationList() {
        List<InformationVO> informationVOs = informationService.getInformationList();
        return ResponseWrapper.success(informationVOs);
    }

    /**
     * 获取资讯列表
     */
    @Permit(value = Permission.ALL)
    @Operation(summary = "获取门户首页资讯滚动列表", description = "获取门户首页资讯滚动列表")
    @GetMapping("portal-home")
    public ResponseWrapper<List<InformationVO>> getInformationListForPortalHome() {
        List<InformationVO> informationVOs = informationService.getInformationListByConfig(
                informationConfig.getRangeType(),
                informationConfig.getStartDate(),
                informationConfig.getEndDate(),
                informationConfig.getCount()
        );
        return ResponseWrapper.success(informationVOs);
    }

    /**
     * 根据id获取资讯详情
     */
    @Permit(value = Permission.ALL)
    @Operation(summary = "查看资讯详情", description = "根据资讯id获取资讯详情")
    @Parameters(
            {
                    @Parameter(in = ParameterIn.PATH, name = "id", schema = @Schema(type = "string"), description = "资讯id")
            }
    )
    @GetMapping("/{id}")
    public ResponseWrapper<InformationVO> getInformationDetail(@PathVariable String id) {
        InformationVO informationVO = informationService.getInformationDetail(id);
        return ResponseWrapper.success(informationVO);
    }

    /**
     * 根据资讯id获取附件列表
     */
    @Permit(value = Permission.ALL)
    @Operation(summary = "获取资讯附件列表", description = "根据资讯id获取附件列表")
    @Parameters(
            {
                    @Parameter(in = ParameterIn.PATH, name = "infoId", schema = @Schema(type = "string"), description = "资讯id")
            }
    )
    @GetMapping("/{infoId}/attachments")
    public ResponseWrapper<List<InfoAppendFileVO>> getInformationAttachments(@PathVariable Long infoId) {
        List<InfoAppendFileVO> attachmentVOs = infoAppendFileService.getInformationAttachments(infoId);
        return ResponseWrapper.success(attachmentVOs);
    }

    /**
     * 添加资讯附件
     */
    @Operation(summary = "添加资讯附件", description = "添加资讯附件")
    @PostMapping("/{infoId}/attachments")
    public ResponseWrapper<InfoAppendFileVO> addInformationAttachment(@PathVariable Long infoId, @RequestBody InfoAppendFileDTO attachmentDTO) {
        InfoAppendFileVO attachmentVO = infoAppendFileService.addInformationAttachment(infoId, attachmentDTO);
        return ResponseWrapper.success(attachmentVO);
    }

    /**
     * 删除资讯附件
     */
    @Operation(summary = "删除资讯附件", description = "删除资讯附件")
    @Parameters(
            {
                    @Parameter(in = ParameterIn.PATH, name = "attachmentId", schema = @Schema(type = "string"), description = "附件id")
            }
    )
    @DeleteMapping("/attachments/{attachmentId}")
    public ResponseWrapper<String> deleteInformationAttachment(@PathVariable String attachmentId) {
        infoAppendFileService.deleteInformationAttachment(attachmentId);
        return ResponseWrapper.success(attachmentId);
    }
    
    /**
     * 获取资讯配置
     */
    @Permit(value = Permission.ALL)
    @Operation(summary = "获取资讯配置", description = "获取资讯刷新时间间隔和范围等配置")
    @GetMapping("/config")
    public ResponseWrapper<InformationConfigVO> getInformationConfig() {
        InformationConfigVO configVO = new InformationConfigVO();
        BeanUtils.copyProperties(informationConfig, configVO);
        return ResponseWrapper.success(configVO);
    }
    
    /**
     * 更新资讯配置
     */
    @Operation(summary = "更新资讯配置", description = "更新资讯刷新时间间隔和范围等配置")
    @PutMapping("/config")
    public ResponseWrapper<InformationConfigVO> updateInformationConfig(@RequestBody InformationConfigDTO configDTO) {
        // 更新配置
        if (configDTO.getRefreshInterval() != null) {
            // 确保刷新时间在有效范围内
            int interval = configDTO.getRefreshInterval();
            if (interval < 3) interval = 3;
            if (interval > 10) interval = 10;
            informationConfig.setRefreshInterval(interval);
        }
        
        if (configDTO.getRangeType() != null) {
            informationConfig.setRangeType(configDTO.getRangeType());
        }
        
        if (configDTO.getStartDate() != null) {
            informationConfig.setStartDate(configDTO.getStartDate());
        }
        
        if (configDTO.getEndDate() != null) {
            informationConfig.setEndDate(configDTO.getEndDate());
        }
        
        if (configDTO.getCount() != null) {
            informationConfig.setCount(configDTO.getCount());
        }
        
        // 保存到文件
        informationConfig.saveConfig();
        
        // 返回更新后的配置
        InformationConfigVO configVO = new InformationConfigVO();
        BeanUtils.copyProperties(informationConfig, configVO);
        return ResponseWrapper.success(configVO);
    }
}
package com.starmol.circuitreview.backend.controller.experienceshare;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starmol.circuitreview.backend.bean.dto.ExperienceShareDTO;
import com.starmol.circuitreview.backend.bean.vo.ExperienceShareVO;
import com.starmol.circuitreview.backend.common.ResponseWrapper;
import com.starmol.circuitreview.backend.model.base.DeleteDTO;
import com.starmol.circuitreview.backend.model.experienceshare.ExperienceShare;
import com.starmol.circuitreview.backend.service.common.LogRecordService;
import com.starmol.circuitreview.backend.service.experienceshare.ExperienceShareService;
import com.starmol.circuitreview.backend.utils.ExceptionUtils;
import com.starmol.circuitreview.backend.utils.StringUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * 设计经验分享管理控制器
 *
 * @author system
 * @version 1.0
 * @date 2026-05-27
 */
@Slf4j
@Tag(name = "设计经验分享管理")
@RestController
@RequestMapping(value = "/v1/experience-shares")
public class ExperienceShareController {

    private final ExperienceShareService experienceShareService;
    private final LogRecordService logRecordService;

    @Autowired
    public ExperienceShareController(ExperienceShareService experienceShareService, LogRecordService logRecordService) {
        this.experienceShareService = experienceShareService;
        this.logRecordService = logRecordService;
    }

    /**
     * 创建设计经验分享
     */
    @Operation(summary = "创建设计经验分享", description = "创建设计经验分享")
    @PostMapping
    public ResponseWrapper<ExperienceShare> createExperienceShare(@RequestBody ExperienceShareDTO dto) {
        ResponseWrapper<ExperienceShare> response = new ResponseWrapper<>();
        try {
            ExperienceShare result = experienceShareService.createExperienceShareAndUpdateAppendFiles(dto);
            response.setContent(result);
            response.setSucc(true);
            response.setMsg("创建设计经验分享成功");
            logRecordService.insert("设计经验分享管理", String.format("创建设计经验分享: %s 成功", StringUtil.getLogDescription(result.getId(), result.getTitle())));
        } catch (Exception e) {
            response.setSucc(false);
            response.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Create ExperienceShare error:%s", e.getMessage()), e);
            logRecordService.insert("设计经验分享管理", String.format("创建设计经验分享: %s 失败", StringUtil.getLogDescription(dto.getId(), dto.getTitle())));
        }
        return response;
    }

    /**
     * 根据删除对象列表安全删除设计经验分享
     */
    @Operation(summary = "删除设计经验分享", description = "删除设计经验分享(安全删除)")
    @PatchMapping
    public ResponseWrapper<String> deleteExperienceShareSafely(@RequestBody List<DeleteDTO> deleteObjects) {
        ResponseWrapper<String> response = new ResponseWrapper<>();
        String formatIds = StringUtil.cutString(deleteObjects.stream().map(StringUtil::getLogDescription).collect(Collectors.joining(",")), 200);
        try {
            experienceShareService.deleteExperienceSharesWithTransaction(deleteObjects);
            response.setContent(deleteObjects.stream().map(m -> m.getId().toString()).collect(Collectors.joining(",")));
            response.setSucc(true);
            response.setMsg("删除设计经验分享成功");
            logRecordService.insert("设计经验分享管理", String.format("删除设计经验分享: %s 成功", formatIds));
        } catch (Exception e) {
            response.setSucc(false);
            response.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Delete ExperienceShare error:%s", e.getMessage()), e);
            logRecordService.insert("设计经验分享管理", String.format("删除设计经验分享: %s 失败", formatIds));
        }
        return response;
    }

    /**
     * 根据id修改设计经验分享
     */
    @Operation(summary = "修改设计经验分享", description = "根据id修改设计经验分享")
    @Parameters({
            @Parameter(in = ParameterIn.PATH, name = "id", schema = @Schema(type = "long"), description = "设计经验分享ID")
    })
    @PutMapping("/{id}")
    public ResponseWrapper<ExperienceShare> updateExperienceShare(@PathVariable Long id, @RequestBody ExperienceShareDTO dto) {
        ResponseWrapper<ExperienceShare> response = new ResponseWrapper<>();
        try {
            dto.setId(id);
            ExperienceShare result = experienceShareService.updateExperienceShareAndUpdateAppendFiles(dto);
            response.setContent(result);
            response.setSucc(true);
            response.setMsg("修改设计经验分享成功");
            logRecordService.insert("设计经验分享管理", String.format("修改设计经验分享: %s 成功", StringUtil.getLogDescription(result.getId(), result.getTitle())));
        } catch (Exception e) {
            response.setSucc(false);
            response.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Update ExperienceShare error:%s", e.getMessage()), e);
            logRecordService.insert("设计经验分享管理", String.format("修改设计经验分享: %s 失败", StringUtil.getLogDescription(dto.getId(), dto.getTitle())));
        }
        return response;
    }

    /**
     * 分页查询设计经验分享
     */
    @Operation(summary = "分页查询设计经验分享", description = "分页查询设计经验分享")
    @GetMapping("/page")
    public ResponseWrapper<IPage<ExperienceShareVO>> getExperienceShareListByPage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Long pageNumber,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long pageSize,
            @Parameter(description = "标题") @RequestParam(required = false) String title,
            @Parameter(description = "贡献人") @RequestParam(required = false) String contributor,
            @Parameter(description = "单位Id") @RequestParam(required = false) Long departmentId) {
        ResponseWrapper<IPage<ExperienceShareVO>> response = new ResponseWrapper<>();
        try {
            IPage<ExperienceShareVO> page = experienceShareService.listExperienceShareVOByPage(new Page<>(pageNumber, pageSize), title, contributor, departmentId);
            log.info("Get experience share list by page success");
            response.setContent(page);
            response.setSucc(true);
            response.setMsg("分页查询设计经验分享成功");
        } catch (Exception e) {
            response.setSucc(false);
            response.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Get experience share list by page error:%s", e.getMessage()), e);
        }
        return response;
    }

    /**
     * 根据id获取设计经验分享详情
     */
    @Operation(summary = "查看设计经验分享详情", description = "根据id获取设计经验分享详情")
    @Parameters({
            @Parameter(in = ParameterIn.PATH, name = "id", schema = @Schema(type = "long"), description = "设计经验分享ID")
    })
    @GetMapping("/{id}")
    public ResponseWrapper<ExperienceShareVO> getExperienceShareDetail(@PathVariable Long id) {
        ResponseWrapper<ExperienceShareVO> response = new ResponseWrapper<>();
        try {
            ExperienceShareVO vo = experienceShareService.getExperienceShareVOById(id);
            response.setContent(vo);
            response.setSucc(true);
            response.setMsg("查看设计经验分享成功");
        } catch (Exception e) {
            log.error(String.format("Get experience share detail error: %s", e.getMessage()), e);
            response.setSucc(false);
            response.setMsg(ExceptionUtils.getLocalMessage(e));
        }
        return response;
    }

    /**
     * 点赞/取消点赞
     */
    @Operation(summary = "点赞/取消点赞", description = "对设计经验分享进行点赞或取消点赞操作")
    @Parameters({
            @Parameter(in = ParameterIn.PATH, name = "id", schema = @Schema(type = "long"), description = "设计经验分享ID")
    })
    @PostMapping("/{id}/like")
    public ResponseWrapper<Integer> toggleLike(@PathVariable Long id) {
        ResponseWrapper<Integer> response = new ResponseWrapper<>();
        try {
            Integer likeCount = experienceShareService.toggleLike(id);
            response.setContent(likeCount);
            response.setSucc(true);
            response.setMsg("操作成功");
            logRecordService.insert("设计经验分享管理", String.format("点赞操作: 经验分享ID=%s, 当前点赞数=%d", id, likeCount));
        } catch (Exception e) {
            response.setSucc(false);
            response.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Toggle like error: %s", e.getMessage()), e);
        }
        return response;
    }
}

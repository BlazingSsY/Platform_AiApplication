package com.starmol.circuitreview.backend.controller.experienceshare;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starmol.circuitreview.backend.bean.dto.ExperienceShareReplyDTO;
import com.starmol.circuitreview.backend.bean.vo.ExperienceShareReplyVO;
import com.starmol.circuitreview.backend.common.ResponseWrapper;
import com.starmol.circuitreview.backend.model.base.DeleteDTO;
import com.starmol.circuitreview.backend.model.experienceshare.ExperienceShareReply;
import com.starmol.circuitreview.backend.service.common.LogRecordService;
import com.starmol.circuitreview.backend.service.experienceshare.ExperienceShareReplyService;
import com.starmol.circuitreview.backend.utils.ExceptionUtils;
import com.starmol.circuitreview.backend.utils.StringUtil;

import lombok.RequiredArgsConstructor;
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
 * 设计经验分享回复管理控制器
 *
 * @author system
 * @version 1.0
 * @date 2026-06-04
 */
@Slf4j
@Tag(name = "设计经验分享回复管理")
@RestController
@RequestMapping(value = "/v1/experience-share-replies")
@RequiredArgsConstructor
public class ExperienceShareReplyController {

    private final ExperienceShareReplyService replyService;
    private final LogRecordService logRecordService;

    /**
     * 创建回复
     */
    @Operation(summary = "创建回复", description = "创建回复")
    @PostMapping
    public ResponseWrapper<ExperienceShareReply> createReply(@RequestBody ExperienceShareReplyDTO dto) {
        ResponseWrapper<ExperienceShareReply> response = new ResponseWrapper<>();
        try {
            ExperienceShareReply result = replyService.createReplyAndUpdateAppendFiles(dto);
            response.setContent(result);
            response.setSucc(true);
            response.setMsg("创建回复成功");
            logRecordService.insert("设计经验分享回复管理", String.format("创建回复: %s 成功", StringUtil.getLogDescription(result.getId(), null)));
        } catch (Exception e) {
            response.setSucc(false);
            response.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Create ExperienceShareReply error:%s", e.getMessage()), e);
            logRecordService.insert("设计经验分享回复管理", String.format("创建回复: %s 失败", StringUtil.getLogDescription(dto.getId(), null)));
        }
        return response;
    }

    /**
     * 根据删除对象列表安全删除回复
     */
    @Operation(summary = "删除回复", description = "删除回复(安全删除)")
    @PatchMapping
    public ResponseWrapper<String> deleteReplySafely(@RequestBody List<DeleteDTO> deleteObjects) {
        ResponseWrapper<String> response = new ResponseWrapper<>();
        String formatIds = StringUtil.cutString(deleteObjects.stream().map(StringUtil::getLogDescription).collect(Collectors.joining(",")), 200);
        try {
            replyService.deleteRepliesWithTransaction(deleteObjects);
            response.setContent(deleteObjects.stream().map(m -> m.getId().toString()).collect(Collectors.joining(",")));
            response.setSucc(true);
            response.setMsg("删除回复成功");
            logRecordService.insert("设计经验分享回复管理", String.format("删除回复: %s 成功", formatIds));
        } catch (Exception e) {
            response.setSucc(false);
            response.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Delete ExperienceShareReply error:%s", e.getMessage()), e);
            logRecordService.insert("设计经验分享回复管理", String.format("删除回复: %s 失败", formatIds));
        }
        return response;
    }

    /**
     * 根据id修改回复
     */
    @Operation(summary = "修改回复", description = "根据id修改回复")
    @Parameters({
            @Parameter(in = ParameterIn.PATH, name = "id", schema = @Schema(type = "long"), description = "回复ID")
    })
    @PutMapping("/{id}")
    public ResponseWrapper<ExperienceShareReply> updateReply(@PathVariable Long id, @RequestBody ExperienceShareReplyDTO dto) {
        ResponseWrapper<ExperienceShareReply> response = new ResponseWrapper<>();
        try {
            dto.setId(id);
            ExperienceShareReply result = replyService.updateReplyAndUpdateAppendFiles(dto);
            response.setContent(result);
            response.setSucc(true);
            response.setMsg("修改回复成功");
            logRecordService.insert("设计经验分享回复管理", String.format("修改回复: %s 成功", StringUtil.getLogDescription(result.getId(), null)));
        } catch (Exception e) {
            response.setSucc(false);
            response.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Update ExperienceShareReply error:%s", e.getMessage()), e);
            logRecordService.insert("设计经验分享回复管理", String.format("修改回复: %s 失败", StringUtil.getLogDescription(dto.getId(), null)));
        }
        return response;
    }

    /**
     * 分页查询回复
     */
    @Operation(summary = "分页查询回复", description = "分页查询回复")
    @GetMapping("/page")
    public ResponseWrapper<IPage<ExperienceShareReplyVO>> getReplyListByPage(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Long pageNumber,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long pageSize,
            @Parameter(description = "设计经验分享ID") @RequestParam(required = false) Long fId,
            @Parameter(description = "回复内容") @RequestParam(required = false) String replyContent) {
        ResponseWrapper<IPage<ExperienceShareReplyVO>> response = new ResponseWrapper<>();
        try {
            IPage<ExperienceShareReplyVO> page = replyService.listReplyVOByPage(new Page<>(pageNumber, pageSize), fId, replyContent);
            log.info("Get experience share reply list by page success");
            response.setContent(page);
            response.setSucc(true);
            response.setMsg("分页查询回复成功");
        } catch (Exception e) {
            response.setSucc(false);
            response.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Get experience share reply list by page error:%s", e.getMessage()), e);
        }
        return response;
    }

    /**
     * 根据id获取回复详情
     */
    @Operation(summary = "查看回复详情", description = "根据回复id获取回复详情")
    @Parameters({
            @Parameter(in = ParameterIn.PATH, name = "id", schema = @Schema(type = "long"), description = "回复ID")
    })
    @GetMapping("/{id}")
    public ResponseWrapper<ExperienceShareReplyVO> getReplyDetail(@PathVariable Long id) {
        ResponseWrapper<ExperienceShareReplyVO> response = new ResponseWrapper<>();
        try {
            ExperienceShareReplyVO vo = replyService.getReplyVOById(id);
            response.setContent(vo);
            response.setSucc(true);
            response.setMsg("查看回复成功");
        } catch (Exception e) {
            log.error(String.format("Get experience share reply detail error: %s", e.getMessage()), e);
            response.setSucc(false);
            response.setMsg(ExceptionUtils.getLocalMessage(e));
        }
        return response;
    }
}

package com.starmol.circuitreview.backend.controller.suggestion;


import com.starmol.circuitreview.backend.bean.vo.SuggestionStatusVO;
import com.starmol.circuitreview.backend.common.ResponseWrapper;
import com.starmol.circuitreview.backend.model.base.DeleteDTO;
import com.starmol.circuitreview.backend.service.common.LogRecordService;
import com.starmol.circuitreview.backend.service.suggestion.SuggestionStatusService;
import com.starmol.circuitreview.backend.utils.ExceptionUtils;
import com.starmol.circuitreview.backend.utils.StringUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Yuexiaopneg
 * @version 1.0
 * @date 2019/11/20 反馈意见状态管理控制器
 */
@Slf4j
@Tag(name = "反馈意见状态管理")
@RestController
@RequestMapping(value = "/v1/suggestion-status")
public class SuggestionStatusController {
    private final SuggestionStatusService suggestionStatusService;
    private final LogRecordService logRecordService;

    @Autowired
    public SuggestionStatusController(SuggestionStatusService suggestionStatusService, LogRecordService logRecordService) {
        this.suggestionStatusService = suggestionStatusService;
        this.logRecordService = logRecordService;
    }


    /**
     * 根据删除对象列表安全删除反馈意见状态，全部删除成功表示当前操作成功，否则回滚操作并返回操作失败
     */
    @Operation(summary = "删除反馈意见状态", description =  "删除反馈意见状态(安全删除)")
    @PatchMapping
    public ResponseWrapper<String> deleteSuggestionStatusSafely(@RequestBody List<DeleteDTO> deleteObjects) {
        ResponseWrapper<String> response = new ResponseWrapper<>();
        String formatIds = StringUtil.cutString(deleteObjects.stream().map(dto->StringUtil.getLogDescription(dto)).collect(Collectors.joining(",")), 200);
        try {
            suggestionStatusService.deleteSuggestionStatusWithTransaction(deleteObjects);
            response.setContent(deleteObjects.stream().map(m -> m.getId().toString()).collect(Collectors.joining(",")));
            response.setSucc(true);
            response.setMsg("删除反馈意见状态成功");
            logRecordService.insert("反馈意见状态管理", String.format("删除反馈意见状态: %s 成功", formatIds));
        } catch (Exception e) {
            response.setSucc(false);
            response.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Delete SuggestionStatus error:%s", e.getMessage()), e);
            logRecordService.insert("反馈意见状态管理", String.format("删除反馈意见状态: %s 失败", formatIds));
        }
        return response;
    }


    /**
     * 查询反馈意见状态
     */
    @Operation(summary = "查询反馈意见状态", description =  "查询反馈意见状态")
    @GetMapping
    public ResponseWrapper<List<SuggestionStatusVO>> getSuggestionStatusListByPage(@Parameter(description = "反馈意见ID") @RequestParam Long suggestionId) {
        ResponseWrapper<List<SuggestionStatusVO>> response = new ResponseWrapper<>();
        try {
            List<SuggestionStatusVO> suggestionStatusList;
            suggestionStatusList =  suggestionStatusService.listSuggestionStatus(suggestionId);
            log.info("Get suggestionStatus list by page success");
            response.setContent(suggestionStatusList);
            response.setSucc(true);
            response.setMsg("分页查询反馈意见状态成功");
        } catch (Exception e) {
            response.setSucc(false);
            response.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Get suggestionStatus list by page error:%s", e.getMessage()), e);
        }
        return response;
    }
}

package com.starmol.circuitreview.backend.controller.suggestion;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starmol.circuitreview.backend.bean.dto.ChangeSuggestionStatusDTO;
import com.starmol.circuitreview.backend.bean.dto.SuggestionDTO;
import com.starmol.circuitreview.backend.bean.vo.SuggestionVO;
import com.starmol.circuitreview.backend.common.ResponseWrapper;
import com.starmol.circuitreview.backend.constant.SuggestionStatusEnum;
import com.starmol.circuitreview.backend.model.suggestion.Suggestion;
import com.starmol.circuitreview.backend.model.base.DeleteDTO;
import com.starmol.circuitreview.backend.service.suggestion.SuggestionService;
import com.starmol.circuitreview.backend.service.common.LogRecordService;
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
 * @author Yuexiaopneg
 * @version 1.0
 * @date 2019/11/20 反馈意见管理控制器
 */
@Slf4j
@Tag(name = "反馈意见管理")
@RestController
@RequestMapping(value = "/v1/suggestions")
public class SuggestionController {
    private final SuggestionService suggestionService;
    private final LogRecordService logRecordService;

    @Autowired
    public SuggestionController(SuggestionService suggestionService, LogRecordService logRecordService) {
        this.suggestionService = suggestionService;
        this.logRecordService = logRecordService;
    }

    /**
     * 创建反馈意见
     */
    @Operation(summary = "创建反馈意见", description = "创建反馈意见")
    @PostMapping
    public ResponseWrapper<Suggestion> createSuggestion(@RequestBody SuggestionDTO suggestionDTO) {
        ResponseWrapper<Suggestion> response = new ResponseWrapper<>();
        try {
            Suggestion returnSuggestion = suggestionService.createSuggestionAndUpdateAppendFiles(suggestionDTO);
            response.setContent(returnSuggestion);
            response.setSucc(true);
            response.setMsg("创建反馈意见成功");
            logRecordService.insert("反馈意见管理", String.format("创建反馈意见: %s 成功", StringUtil.getLogDescription(returnSuggestion.getId(),returnSuggestion.getTitle())));
        } catch (Exception e) {
            response.setSucc(false);
            response.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Create Suggestion error:%s", e.getMessage()), e);
            logRecordService.insert("反馈意见管理", String.format("创建反馈意见: %s 失败", StringUtil.getLogDescription(suggestionDTO.getId(),suggestionDTO.getTitle())));
        }
        return response;
    }

    /**
     * 根据删除对象列表安全删除反馈意见，全部删除成功表示当前操作成功，否则回滚操作并返回操作失败
     */
    @Operation(summary = "删除反馈意见", description =  "删除反馈意见(安全删除)")
    @PatchMapping
    public ResponseWrapper<String> deleteSuggestionSafely(@RequestBody List<DeleteDTO> deleteObjects) {
        ResponseWrapper<String> response = new ResponseWrapper<>();
        String formatIds = StringUtil.cutString(deleteObjects.stream().map(dto->StringUtil.getLogDescription(dto)).collect(Collectors.joining(",")), 200);
        try {
            suggestionService.deleteSuggestionsWithTransaction(deleteObjects);
            response.setContent(deleteObjects.stream().map(m -> m.getId().toString()).collect(Collectors.joining(",")));
            response.setSucc(true);
            response.setMsg("删除反馈意见成功");
            logRecordService.insert("反馈意见管理", String.format("删除反馈意见: %s 成功", formatIds));
        } catch (Exception e) {
            response.setSucc(false);
            response.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Delete Suggestion error:%s", e.getMessage()), e);
            logRecordService.insert("反馈意见管理", String.format("删除反馈意见: %s 失败", formatIds));
        }
        return response;
    }


    /**
     * 根据id修改反馈意见
     */
    @Operation(summary = "修改反馈意见", description =  "根据id修改反馈意见")
    @Parameters(
            {
                    @Parameter(in = ParameterIn.PATH, name = "id", schema = @Schema(type = "long"), description = "反馈意见d")
            }
    )
    @PutMapping("/{id}")
    public ResponseWrapper<Suggestion> updateSuggestion(@PathVariable Long id, @RequestBody SuggestionDTO suggestionDTO) {
        ResponseWrapper<Suggestion> response = new ResponseWrapper<>();
        try {
            suggestionDTO.setId(id);
            Suggestion returnSuggestion = suggestionService.updateSuggestionAndUpdateAppendFiles(suggestionDTO);
            response.setContent(returnSuggestion);
            response.setSucc(true);
            response.setMsg("修改反馈意见成功");
            logRecordService.insert("反馈意见管理", String.format("修改反馈意见: %s 成功", StringUtil.getLogDescription(returnSuggestion.getId(), returnSuggestion.getTitle())));
        } catch (Exception e) {
            response.setSucc(false);
            response.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Update suggestion error:%s", e.getMessage()), e);
            logRecordService.insert("反馈意见管理", String.format("修改反馈意见: %s 失败", StringUtil.getLogDescription(suggestionDTO.getId(), suggestionDTO.getTitle())));
        }
        return response;
    }

    @Operation(summary = "修改反馈意见状态", description = "根据id修改反馈意见状态")
    @Parameters(
            {
                    @Parameter(in = ParameterIn.PATH, name = "id", schema = @Schema(type = "long"), description = "反馈意见Id")
            }
    )
    @PutMapping("/status/{id}")
    public ResponseWrapper<ChangeSuggestionStatusDTO> updateSuggestionStatus(@PathVariable Long id, @RequestBody ChangeSuggestionStatusDTO changeSuggestionStatusDTO) {
        ResponseWrapper<ChangeSuggestionStatusDTO> response = new ResponseWrapper<>();
        try {

            suggestionService.changeSuggestionStatusById(id, changeSuggestionStatusDTO);
            response.setContent(changeSuggestionStatusDTO);
            response.setSucc(true);
            response.setMsg("修改反馈意见状态成功");
            logRecordService.insert("反馈意见管理", String.format("修改反馈意见状态: %s 成功", StringUtil.getLogDescription(id, changeSuggestionStatusDTO.getTitle())));
        } catch (Exception e) {
            response.setSucc(false);
            response.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Update suggestion status error:%s", e.getMessage()), e);
            logRecordService.insert("反馈意见管理", String.format("修改反馈意见状态: %s 失败", StringUtil.getLogDescription(changeSuggestionStatusDTO.getId(), changeSuggestionStatusDTO.getTitle())));
        }
        return response;
    }


    /**
     * 分页查询反馈意见
     */
    @Operation(summary = "分页查询反馈意见", description =  "分页查询反馈意见")
    @GetMapping("/page")
    public ResponseWrapper<IPage<SuggestionVO>> getSuggestionListByPage(@Parameter(description = "页码") @RequestParam(defaultValue = "1") Long pageNumber,
                                                                        @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long pageSize,
                                                                        @Parameter(description = "机构ids") @RequestParam(required = false) List<Long> departmentIds,
                                                                        @Parameter(description = "用户ids") @RequestParam(required = false) List<Long> userIds,
                                                                        @Parameter(description = "状态") @RequestParam(required = false) SuggestionStatusEnum status,
                                                                        @Parameter(description = "标题") @RequestParam(required = false) String title) {
        ResponseWrapper<IPage<SuggestionVO>> response = new ResponseWrapper<>();
        try {
            IPage<SuggestionVO> suggestionsByPage;
            suggestionsByPage =  suggestionService.listSuggestionVOByPage(new Page<>(pageNumber, pageSize), departmentIds,  userIds,  status, title);
            log.info("Get suggestion list by page success");
            response.setContent(suggestionsByPage);
            response.setSucc(true);
            response.setMsg("分页查询反馈意见成功");
        } catch (Exception e) {
            response.setSucc(false);
            response.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Get suggestion list by page error:%s", e.getMessage()), e);
        }
        return response;
    }

    /**
     * 根据id获取反馈意见详情
     */
    @Operation(summary = "查看反馈意见详情", description =  "根据反馈意见id获取反馈意见详情")
    @Parameters(
            {
                    @Parameter(in = ParameterIn.PATH, name = "id", schema = @Schema(type = "long"), description = "反馈意见id")
            }
    )
    @GetMapping("/{id}")
    public ResponseWrapper<SuggestionVO> getSuggestionDetail(@PathVariable Long id) {
        ResponseWrapper<SuggestionVO> response = new ResponseWrapper<>();
        try {
            SuggestionVO suggestion = suggestionService.getSuggestionVOById(id);
            response.setContent(suggestion);
            response.setSucc(true);
            response.setMsg("查看反馈意见成功");
        } catch (Exception e) {
            log.error(String.format("Get suggestion detail error: %s", e.getMessage()), e);
            response.setSucc(false);
            response.setMsg(ExceptionUtils.getLocalMessage(e));
        }
        return response;
    }
}

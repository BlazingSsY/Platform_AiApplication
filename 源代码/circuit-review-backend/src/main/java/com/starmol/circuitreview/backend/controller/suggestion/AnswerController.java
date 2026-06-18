package com.starmol.circuitreview.backend.controller.suggestion;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starmol.circuitreview.backend.bean.dto.AnswerDTO;
import com.starmol.circuitreview.backend.bean.vo.AnswerVO;
import com.starmol.circuitreview.backend.common.Permission;
import com.starmol.circuitreview.backend.common.ResponseWrapper;
import com.starmol.circuitreview.backend.model.base.DeleteDTO;
import com.starmol.circuitreview.backend.model.suggestion.Answer;
import com.starmol.circuitreview.backend.service.common.LogRecordService;
import com.starmol.circuitreview.backend.service.suggestion.AnswerService;
import com.starmol.circuitreview.backend.utils.ExceptionUtils;
import com.starmol.circuitreview.backend.utils.StringUtil;
import com.starmol.circuitreview.backend.aop.Permit;

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
 * @date 2019/11/20 回复管理控制器
 */
@Slf4j
@Tag(name = "回复管理")
@RestController
@RequestMapping(value = "/v1/answers")
public class AnswerController {
    private final AnswerService answerService;
    private final LogRecordService logRecordService;

    @Autowired
    public AnswerController(AnswerService answerService, LogRecordService logRecordService) {
        this.answerService = answerService;
        this.logRecordService = logRecordService;
    }

    /**
     * 创建回复
     */
    @Operation(summary = "创建回复", description = "创建回复")
    @PostMapping
    public ResponseWrapper<Answer> createAnswer(@RequestBody AnswerDTO answerDTO) {
        ResponseWrapper<Answer> response = new ResponseWrapper<>();
        try {
            Answer returnAnswer = answerService.createAnswerAndUpdateAppendFiles(answerDTO);
            response.setContent(returnAnswer);
            response.setSucc(true);
            response.setMsg("创建回复成功");
            logRecordService.insert("回复管理", String.format("创建回复: %s 成功", StringUtil.getLogDescription(returnAnswer.getId(),null)));
        } catch (Exception e) {
            response.setSucc(false);
            response.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Create Answer error:%s", e.getMessage()), e);
            logRecordService.insert("回复管理", String.format("创建回复: %s 失败", StringUtil.getLogDescription(answerDTO.getId(),null)));
        }
        return response;
    }

    /**
     * 根据删除对象列表安全删除回复，全部删除成功表示当前操作成功，否则回滚操作并返回操作失败
     */
    @Operation(summary = "删除回复", description =  "删除回复(安全删除)")
    @PatchMapping
    public ResponseWrapper<String> deleteAnswerSafely(@RequestBody List<DeleteDTO> deleteObjects) {
        ResponseWrapper<String> response = new ResponseWrapper<>();
        String formatIds = StringUtil.cutString(deleteObjects.stream().map(dto->StringUtil.getLogDescription(dto)).collect(Collectors.joining(",")), 200);
        try {
            answerService.deleteAnswersWithTransaction(deleteObjects);
            response.setContent(deleteObjects.stream().map(m -> m.getId().toString()).collect(Collectors.joining(",")));
            response.setSucc(true);
            response.setMsg("删除回复成功");
            logRecordService.insert("回复管理", String.format("删除回复: %s 成功", formatIds));
        } catch (Exception e) {
            response.setSucc(false);
            response.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Delete Answer error:%s", e.getMessage()), e);
            logRecordService.insert("回复管理", String.format("删除回复: %s 失败", formatIds));
        }
        return response;
    }


    /**
     * 根据id修改回复
     */
    @Operation(summary = "修改回复", description =  "根据id修改回复")
    @Parameters(
            {
                    @Parameter(in = ParameterIn.PATH, name = "id", schema = @Schema(type = "long"), description = "回复d")
            }
    )
    @PutMapping("/{id}")
    public ResponseWrapper<Answer> updateAnswer(@PathVariable Long id, @RequestBody AnswerDTO answerDTO) {
        ResponseWrapper<Answer> response = new ResponseWrapper<>();
        try {
            answerDTO.setId(id);
            Answer returnAnswer = answerService.updateAnswerAndUpdateAppendFiles(answerDTO);
            response.setContent(returnAnswer);
            response.setSucc(true);
            response.setMsg("修改回复成功");
            logRecordService.insert("回复管理", String.format("修改回复: %s 成功", StringUtil.getLogDescription(returnAnswer.getId(), null)));
        } catch (Exception e) {
            response.setSucc(false);
            response.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Update answer error:%s", e.getMessage()), e);
            logRecordService.insert("回复管理", String.format("修改回复: %s 失败", StringUtil.getLogDescription(answerDTO.getId(), null)));
        }
        return response;
    }



    /**
     * 分页查询回复
     */
    @Operation(summary = "分页查询回复", description =  "分页查询回复")
    @GetMapping("/page")
    public ResponseWrapper<IPage<AnswerVO>> getAnswerListByPage(@Parameter(description = "页码") @RequestParam(defaultValue = "1") Long pageNumber,
                                                                        @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long pageSize,
                                                                        @Parameter(description = "反馈意见ID") @RequestParam(required = false) Long fId,
                                                                        @Parameter(description = "标题") @RequestParam(required = false) String answerContent) {
        ResponseWrapper<IPage<AnswerVO>> response = new ResponseWrapper<>();
        try {
            IPage<AnswerVO> answersByPage;
            answersByPage =  answerService.listAnswerVOByPage(new Page<>(pageNumber, pageSize), fId, answerContent);
            log.info("Get answer list by page success");
            response.setContent(answersByPage);
            response.setSucc(true);
            response.setMsg("分页查询回复成功");
        } catch (Exception e) {
            response.setSucc(false);
            response.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Get answer list by page error:%s", e.getMessage()), e);
        }
        return response;
    }

    /**
     * 根据id获取回复详情
     */
    @Operation(summary = "查看回复详情", description =  "根据回复id获取回复详情")
    @Parameters(
            {
                    @Parameter(in = ParameterIn.PATH, name = "id", schema = @Schema(type = "long"), description = "回复id")
            }
    )
    @GetMapping("/{id}")
    public ResponseWrapper<AnswerVO> getAnswerDetail(@PathVariable Long id) {
        ResponseWrapper<AnswerVO> response = new ResponseWrapper<>();
        try {
            AnswerVO answer = answerService.getAnswerVOById(id);
            response.setContent(answer);
            response.setSucc(true);
            response.setMsg("查看回复成功");
        } catch (Exception e) {
            log.error(String.format("Get answer detail error: %s", e.getMessage()), e);
            response.setSucc(false);
            response.setMsg(ExceptionUtils.getLocalMessage(e));
        }
        return response;
    }
}

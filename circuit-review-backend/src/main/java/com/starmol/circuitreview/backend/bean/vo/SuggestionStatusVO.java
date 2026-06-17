/*
 * STARMOL Confidential
 * OCO Source Materials
 * STARMOL
 * © Copyright STARMOL Advanced Technology Ltd. 2022-2023
 * The source code for this program is not published or otherwise divested of its trade secrets, irrespective of STARMOL has been deposited with the P.R.China Copyright Office
 */
package com.starmol.circuitreview.backend.bean.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.circuitreview.backend.constant.SuggestionStatusEnum;
import com.starmol.circuitreview.backend.model.base.BaseBean;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static com.baomidou.mybatisplus.annotation.FieldFill.INSERT;

/**
 * @author Yuexiaopeng
 * @description AnswerVO
 * @since 2021/5/11 17:38
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "回复分页查询VO")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuggestionStatusVO extends BaseBean implements Serializable {

    private static final long serialVersionUID = 5460085666807921556L;

    @Schema(description = "主记录ID")
    private Long suggestionId;

    @Schema(description = "反馈状态(0:new;1:open;2:reopen;3:closed)")
    private SuggestionStatusEnum status;

    @Schema(description = "备注")
    private String comments;

    @Schema(description = "创建人")
    private Long createUser;

    @Schema(description = "创建人名称")
    private String createUserName;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createDate;

    @Schema(description = "最后更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateDate;

}

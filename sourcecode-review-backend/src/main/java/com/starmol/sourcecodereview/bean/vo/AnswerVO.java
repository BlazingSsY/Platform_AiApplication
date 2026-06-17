/*
 * STARMOL Confidential
 * OCO Source Materials
 * STARMOL
 * © Copyright STARMOL Advanced Technology Ltd. 2022-2023
 * The source code for this program is not published or otherwise divested of its trade secrets, irrespective of STARMOL has been deposited with the P.R.China Copyright Office
 */
package com.starmol.sourcecodereview.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.sourcecodereview.model.base.BaseBean;
import com.starmol.sourcecodereview.model.suggestion.Answer;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Yuexiaopeng
 * @description AnswerVO
 * @since 2021/5/11 17:38
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "回复分页查询VO")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnswerVO extends BaseBean implements Serializable {

    private static final long serialVersionUID = 5460085666807921556L;

    @Schema(description = "主记录ID")
    private Long fId;

    @Schema(description = "回复内容")
    private String answer;

    @Schema(description = "备注")
    private String comments;

    @Schema(description = "引用回复ID")
    private Long refId;

    @Schema(description = "引用回复")
    private Answer refAnswer;

    @Schema(description = "创建人")
    private Long createUser;

    @Schema(description = "创建人名称")
    private String createUserName;

    @Schema(description = "最后更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateDate;

    @Schema(description = "附件文件列表")
    private List<AppendFileVO> appendFileList;

}

/*
 * STARMOL Confidential
 * OCO Source Materials
 * STARMOL
 * © Copyright STARMOL Advanced Technology Ltd. 2022-2023
 * The source code for this program is not published or otherwise divested of its trade secrets, irrespective of STARMOL has been deposited with the P.R.China Copyright Office
 */
package com.starmol.logicreview.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.logicreview.constant.SuggestionStatusEnum;
import com.starmol.logicreview.model.base.BaseBean;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Yuexiaopeng
 * @description SuggestionVO
 * @since 2021/5/11 17:38
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "反馈意见分页查询VO")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SuggestionVO extends BaseBean implements Serializable {

    private static final long serialVersionUID = 5460085666807901556L;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "反馈意见内容")
    private String suggestion;

    @Schema(description = "反馈状态(1:打开;2:重新打开;3:关闭)")
    private SuggestionStatusEnum status;

    @Schema(description = "意见描述")
    private String description;

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

    @Schema(description = "文件ID")
    private Long fileId;

    @Schema(description = "文件版本ID")
    private Long fileVersionId;

    @Schema(description = "审查结果ID")
    private Long resultId;

    @Schema(description = "附件文件列表")
    private List<AppendFileVO> appendFileList;

}

package com.starmol.sourcecodereview.bean.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.sourcecodereview.constant.SuggestionStatusEnum;
import com.starmol.sourcecodereview.model.base.BaseBean;

import java.io.Serializable;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "反馈意见模型DTO")
@Accessors(chain = true)
public class SuggestionDTO extends BaseBean implements Serializable {
    private static final long serialVersionUID = 8475418862356391102L;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "反馈意见内容")
    private String suggestion;

    @Schema(description = "反馈意见状态")
    private SuggestionStatusEnum status;

    @Schema(description = "意见描述")
    private String description;

    @Schema(description = "备注")
    private String comments;

    @Schema(description = "文件ID")
    private Long fileId;

    @Schema(description = "文件版本ID")
    private Long fileVersionId;

    @Schema(description = "审查结果ID")
    private Long resultId;

    @Schema(description = "附件文件列表")
    private List<AppendFileDTO> appendFileList;
}
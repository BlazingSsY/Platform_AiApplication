package com.starmol.logicreview.model.suggestion;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.logicreview.constant.SuggestionStatusEnum;
import com.starmol.logicreview.model.base.IdBaseModel;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "logic_yjfk_suggestion", autoResultMap = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "  主表模型")
@ToString(callSuper = true)
@Accessors(chain = true)
public class Suggestion extends IdBaseModel implements Serializable {

    @Schema(description = "标题")
    private String title;

    @Schema(description = "反馈意见内容")
    private String suggestion;

    @Schema(description = "反馈状态(1:open;2:reopen;3:closed)")
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
}

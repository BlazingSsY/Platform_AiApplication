package com.starmol.logicreview.model.suggestion;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.logicreview.constant.SuggestionStatusEnum;
import com.starmol.logicreview.model.base.IdBaseModel;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "反馈意见状态日志模型")
@TableName(value = "logic_yjfk_suggestion_status")
@Accessors(chain = true)
public class SuggestionStatus extends IdBaseModel implements Serializable {
    private static final long serialVersionUID = 8495418862356311001L;

    @Schema(description = "主记录ID")
    private Long suggestionId;

    @Schema(description = "反馈状态(0:new(open);1:open;2:reopen;3:closed)")
    private SuggestionStatusEnum status;

    @Schema(description = "备注")
    private String comments;

}
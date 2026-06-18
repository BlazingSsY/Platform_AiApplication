package com.starmol.circuitreview.backend.model.suggestion;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.circuitreview.backend.model.base.IdBaseModel;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "回复模型")
@TableName(value = "yjfk_answer")
@Accessors(chain = true)
public class Answer extends IdBaseModel implements Serializable {
    private static final long serialVersionUID = 8495418862356311001L;

    @Schema(description = "主记录ID")
    private Long fId;

    @Schema(description = "回复内容")
    private String answer;

    @Schema(description = "引用回复ID")
    private Long refId;

    @Schema(description = "备注")
    private String comments;

}
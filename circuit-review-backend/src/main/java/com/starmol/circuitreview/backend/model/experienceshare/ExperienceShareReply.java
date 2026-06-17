package com.starmol.circuitreview.backend.model.experienceshare;

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
@Schema(description = "设计经验分享回复模型")
@TableName(value = "sjjyfx_experience_share_reply")
@Accessors(chain = true)
public class ExperienceShareReply extends IdBaseModel implements Serializable {
    private static final long serialVersionUID = 8975418862356312001L;

    @Schema(description = "主记录ID")
    private Long fId;

    @Schema(description = "回复内容")
    private String reply;

    @Schema(description = "备注")
    private String comments;
}

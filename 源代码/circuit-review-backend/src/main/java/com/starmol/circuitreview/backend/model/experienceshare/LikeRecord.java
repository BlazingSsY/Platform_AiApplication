package com.starmol.circuitreview.backend.model.experienceshare;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.circuitreview.backend.model.base.IdBaseModel;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sjjyfx_like_record", autoResultMap = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "设计经验分享点赞记录模型")
@ToString(callSuper = true)
@Accessors(chain = true)
public class LikeRecord extends IdBaseModel implements Serializable {

    private static final long serialVersionUID = 8975418862356312001L;

    @Schema(description = "经验分享ID")
    private Long experienceId;

    @Schema(description = "点赞用户ID")
    private Long userId;

    @Schema(description = "是否点赞(1:已点赞;0:已取消)")
    private Integer isLiked;
}

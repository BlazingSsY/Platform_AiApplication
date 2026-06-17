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
@TableName(value = "sjjyfx_experience_share", autoResultMap = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "设计经验分享主表模型")
@ToString(callSuper = true)
@Accessors(chain = true)
public class ExperienceShare extends IdBaseModel implements Serializable {

    private static final long serialVersionUID = 8975418862356311001L;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "经验分享内容")
    private String content;

    @Schema(description = "贡献人")
    private String contributor;

    @Schema(description = "单位")
    private String organization;

    @Schema(description = "联系方式")
    private String contact;

    @Schema(description = "点赞数")
    private Integer likeCount;
}

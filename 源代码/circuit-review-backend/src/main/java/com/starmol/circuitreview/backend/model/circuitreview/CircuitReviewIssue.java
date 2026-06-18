package com.starmol.circuitreview.backend.model.circuitreview;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.circuitreview.backend.model.base.IdBaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "dlsc_review_issue", autoResultMap = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "电路审查问题模型")
@ToString(callSuper = true)
@Accessors(chain = true)
public class CircuitReviewIssue extends IdBaseModel implements Serializable {
    
    @Schema(description = "外键，关联到文件表")
    private Long fileId;

    @Schema(description = "外键，关联到文件版本表")
    private Long fileVersionId;

    @Schema(description = "外键，关联到结果表")
    private Long resultId;

    @Schema(description = "外键，关联到结果详情表")
    private Long resultDetailId;

    @Schema(description = "外键，关联到规则表")
    private Long ruleId;

    @Schema(description = "器件型号 (可为空)")
    private String deviceType;

    @Schema(description = "位号引脚 (单个位号引脚，可为空)")
    private String tagPin;

    @Schema(description = "审查意见")
    private String reviewSuggestion;

    @Schema(description = "规则编号")
    private String ruleCode;

    @Schema(description = "审查时间")
    private LocalDateTime reviewTime;

    @Schema(description = "备注")
    private String comments;
}

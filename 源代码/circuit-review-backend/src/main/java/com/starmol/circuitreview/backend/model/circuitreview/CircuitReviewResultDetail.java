package com.starmol.circuitreview.backend.model.circuitreview;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.circuitreview.backend.constant.CircuitReviewResultDetailAuditTypeEnum;
import com.starmol.circuitreview.backend.constant.RuleTypeEnum;
import com.starmol.circuitreview.backend.model.base.IdBaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "dlsc_review_result_detail", autoResultMap = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "电路审查结果详情模型")
@ToString(callSuper = true)
@Accessors(chain = true)
public class CircuitReviewResultDetail extends IdBaseModel implements Serializable {

    @Schema(description = "外键,关联到审查结果表")
    private Long resultId;

    @Schema(description = "外键,关联到规则表")
    private Long ruleId;

    @Schema(description = "规则编号")
    private String ruleCode;

    @Schema(description = "器件型号")
    private String deviceType;

    @Schema(description = "位号引脚")
    private String tagPin;

    @Schema(description = "审查意见")
    private String reviewSuggestion;
    
    @Schema(description = "是否通过(0:未通过;1:通过)")
    private Integer isPassed;

    @Schema(description = "审核类型")
    private CircuitReviewResultDetailAuditTypeEnum auditType;

    @Schema(description = "批准的审核类型")
    private CircuitReviewResultDetailAuditTypeEnum approvedAuditType;

    @Schema(description = "问题反馈")
    private String issueFeedback;
    
    @Schema(description = "备注")
    private String comments;
}

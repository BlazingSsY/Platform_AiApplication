package com.starmol.circuitreview.backend.bean.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.circuitreview.backend.constant.CircuitReviewResultDetailAuditStatusEnum;
import com.starmol.circuitreview.backend.constant.CircuitReviewResultDetailAuditTypeEnum;
import com.starmol.circuitreview.backend.constant.RuleTypeEnum;
import com.starmol.circuitreview.backend.model.base.BaseBean;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "电路审查结果详情VO")
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CircuitReviewResultDetailVO extends BaseBean implements Serializable {

    private static final long serialVersionUID = 8895418862356391002L;

    @Schema(description = "外键,关联到审查结果表")
    private Long resultId;

    @Schema(description = "外键,关联到规则表")
    private Long ruleId;

    @Schema(description = "规则类型(1:通用规则;2:元器件手册规则;3:技术规则)")
    private RuleTypeEnum ruleType;

    @Schema(description = "规则编号")
    private String ruleCode;

    @Schema(description = "规则名称")
    private String ruleName;

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

    @Schema(description = "外键,关联到电路审查结果详情审核表")
    private Long resultDetailAuditId;

    @Schema(description = "拒绝原因")
    private String rejectReason;

    @Schema(description = "拒绝的问题反馈")
    private String rejectIssueFeedback;

    @Schema(description = "拒绝的审核类型")
    private CircuitReviewResultDetailAuditTypeEnum rejectAuditType;

    @Schema(description = "问题审核状态(1:审核中;2:审核通过;3:审核拒绝)")
    private CircuitReviewResultDetailAuditStatusEnum auditStatus;

    @Schema(description = "提交审核的时间")
    private LocalDateTime auditSubmitTime;

    @Schema(description = "审核完成的时间")
    private LocalDateTime auditCloseTime;

    @Schema(description = "外键,关联到用户表,复核人")
    private Long auditUserId;

    @Schema(description = "外键,关联到用户表,复核人")
    private String auditUserName;

    @Schema(description = "备注")
    private String comments;
}
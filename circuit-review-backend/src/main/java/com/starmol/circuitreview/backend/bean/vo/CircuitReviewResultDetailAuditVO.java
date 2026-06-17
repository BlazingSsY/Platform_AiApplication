package com.starmol.circuitreview.backend.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.circuitreview.backend.constant.CircuitReviewResultDetailAuditStatusEnum;
import com.starmol.circuitreview.backend.constant.CircuitReviewResultDetailAuditTypeEnum;
import com.starmol.circuitreview.backend.constant.RuleTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "电路审查结果详情审核VO")
@JsonIgnoreProperties(ignoreUnknown = true)
@Accessors(chain = true)
public class CircuitReviewResultDetailAuditVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "外键,关联到文件表")
    private Long fileId;

    @Schema(description = "外键,关联到文件版本表")
    private Long fileVersionId;

    @Schema(description = "外键,关联到审查结果表")
    private Long resultId;

    @Schema(description = "外键,关联到审查结果详情表")
    private Long resultDetailId;

    @Schema(description = "问题审核状态(1:审核中;2:审核通过;3:审核拒绝)")
    private CircuitReviewResultDetailAuditStatusEnum status;

    @Schema(description = "提交审核的时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime auditSubmitTime;

    @Schema(description = "审核完成的时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime auditCloseTime;

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

    @Schema(description = "拒绝原因")
    private String rejectReason;

    @Schema(description = "复核人")
    private String auditUserName;

}
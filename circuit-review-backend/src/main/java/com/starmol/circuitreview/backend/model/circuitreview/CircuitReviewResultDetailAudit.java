package com.starmol.circuitreview.backend.model.circuitreview;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.circuitreview.backend.constant.CircuitReviewResultDetailAuditStatusEnum;
import com.starmol.circuitreview.backend.constant.CircuitReviewResultDetailAuditTypeEnum;
import com.starmol.circuitreview.backend.model.base.IdBaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "dlsc_review_result_detail_audit", autoResultMap = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "电路审查结果详情复核模型")
@ToString(callSuper = true)
@Accessors(chain = true)
public class CircuitReviewResultDetailAudit extends IdBaseModel implements Serializable {

    @Schema(description = "外键,关联到文件表")
    private Long fileId;

    @Schema(description = "外键,关联到文件版本表")
    private Long fileVersionId;

    @Schema(description = "外键,关联到审查结果表")
    private Long resultId;

    @Schema(description = "外键,关联到审查结果详情表")
    private Long resultDetailId;

    @Schema(description = "外键,关联到电路审查结果复核表")
    private Long resultAuditId;

    @Schema(description = "继承的结果详情复核记录Id")
    private Long inheritedDetailAuditId;

    @Schema(description = "复核类型")
    private CircuitReviewResultDetailAuditTypeEnum auditType;

    @Schema(description = "问题反馈")
    private String issueFeedback;

    @Schema(description = "拒绝原因")
    private String rejectReason;

    @Schema(description = "问题复核状态(1:复核中;2:复核通过;3:复核拒绝)")
    private CircuitReviewResultDetailAuditStatusEnum status;

    @Schema(description = "提交复核的时间")
    private LocalDateTime auditSubmitTime;

    @Schema(description = "复核完成的时间")
    private LocalDateTime auditCloseTime;
}

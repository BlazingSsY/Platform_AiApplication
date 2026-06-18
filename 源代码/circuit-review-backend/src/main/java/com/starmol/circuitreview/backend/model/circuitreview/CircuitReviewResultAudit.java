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
@TableName(value = "dlsc_review_result_audit", autoResultMap = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "电路审查结果复核模型")
@ToString(callSuper = true)
@Accessors(chain = true)
public class CircuitReviewResultAudit extends IdBaseModel implements Serializable {

    @Schema(description = "外键,关联到文件表")
    private Long fileId;

    @Schema(description = "外键,关联到文件版本表")
    private Long fileVersionId;

    @Schema(description = "外键,关联到审查结果表")
    private Long resultId;

    @Schema(description = "问题复核状态(0:待复核;1:已复核)")
    private Integer isAuditFinished;

    @Schema(description = "管理员问题复核状态(0:待复核;1:已复核)")
    private Integer isAdminAuditFinished;

    @Schema(description = "专家问题复核状态(0:待复核;1:已复核)")
    private Integer isExpertAuditFinished;

    @Schema(description = "开始复核的时间")
    private LocalDateTime auditTime;
}

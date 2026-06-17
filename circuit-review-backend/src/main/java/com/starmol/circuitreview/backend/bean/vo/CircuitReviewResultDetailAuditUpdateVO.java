package com.starmol.circuitreview.backend.bean.vo;

import com.starmol.circuitreview.backend.constant.AuditActionTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Schema(description = "电路审查结果详情审核更新请求")
public class CircuitReviewResultDetailAuditUpdateVO {

    @Schema(description = "审核动作类型")
    private AuditActionTypeEnum auditActionType;

    @Schema(description = "拒绝原因", required = false)
    private String rejectReason;
}

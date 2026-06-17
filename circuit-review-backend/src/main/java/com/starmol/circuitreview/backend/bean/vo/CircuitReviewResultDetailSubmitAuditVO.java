package com.starmol.circuitreview.backend.bean.vo;

import com.starmol.circuitreview.backend.constant.CircuitReviewResultDetailAuditTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Schema(description = "电路审查结果详情提交审核请求")
public class CircuitReviewResultDetailSubmitAuditVO {

    @Schema(description = "审核类型")
    private CircuitReviewResultDetailAuditTypeEnum auditType;

    @Schema(description = "问题反馈")
    private String issueFeedback;
}
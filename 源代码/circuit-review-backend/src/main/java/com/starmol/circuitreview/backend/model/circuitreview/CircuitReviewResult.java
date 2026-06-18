package com.starmol.circuitreview.backend.model.circuitreview;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.circuitreview.backend.constant.CircuitReviewStatusEnum;
import com.starmol.circuitreview.backend.model.base.IdBaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "dlsc_review_result", autoResultMap = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "电路审查结果模型")
@ToString(callSuper = true)
@Accessors(chain = true)
public class CircuitReviewResult extends IdBaseModel implements Serializable {
    
    @Schema(description = "外键,关联到文件表")
    private Long fileId;

    @Schema(description = "外键,关联到文件版本表")
    private Long fileVersionId;
    
    @Schema(description = "检查点数量")
    private Integer checkPoints;
    
    @Schema(description = "通过的检查点数量")
    private Integer passCheckPoints;

    @Schema(description = "关闭的问题数量")
    private Integer closedFailCheckPoints;

    @Schema(description = "所有问题数量")
    private Integer totalFailCheckPoints;
    
    @Schema(description = "通过率")
    private BigDecimal passRate;

    @Schema(description = "问题闭环状态(0:未闭环;1:已闭环)")
    private Integer isClosedLoop;

    @Schema(description = "问题审核状态(0:未审核;1:审核中)")
    private Integer isInAudit;
    
    @Schema(description = "开始审查的时间")
    private LocalDateTime reviewTime;
    
    @Schema(description = "状态(1:正在审查;2:审查完成)")
    private CircuitReviewStatusEnum status;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "备注")
    private String comments;
}

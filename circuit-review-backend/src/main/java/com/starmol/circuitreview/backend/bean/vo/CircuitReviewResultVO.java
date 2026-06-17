package com.starmol.circuitreview.backend.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.circuitreview.backend.constant.CircuitReviewStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "电路审查结果VO")
@JsonIgnoreProperties(ignoreUnknown = true)
@Accessors(chain = true)
public class CircuitReviewResultVO implements Serializable {
    
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "外键,关联到文件表")
    private Long fileId;

    @Schema(description = "外键,关联到文件版本表")
    private Long fileVersionId;

    @Schema(description = "外键,关联到电路审查结果审核表")
    private Long resultAuditId;

    @Schema(description = "检查点数量")
    private Integer checkPoints;

    @Schema(description = "通过的检查点数量")
    private Integer passCheckPoints;

    @Schema(description = "不通过的检查点数量")
    private Integer failCheckPoints;

    @Schema(description = "通过率")
    private BigDecimal passRate;

    @Schema(description = "问题闭环状态(0:未闭环;1:已闭环)")
    private Integer isClosedLoop;

    @Schema(description = "问题审核状态(0:未审核;1:审核中)")
    private Integer isInAudit;

    @Schema(description = "开始审查的时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime reviewTime;

    @Schema(description = "状态(0:未审查;1:正在审查;2:审查完成)")
    private CircuitReviewStatusEnum status;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "备注")
    private String comments;
}
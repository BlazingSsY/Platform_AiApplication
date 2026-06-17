package com.starmol.circuitreview.backend.bean.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.circuitreview.backend.constant.FileSecretLevelEnum;
import com.starmol.circuitreview.backend.model.base.BaseBean;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "电路图统计导出数据详情DTO")
@Accessors(chain = true)
public class CircuitReviewStatisticsExportDataDTO {

    @Schema(description = "机构id")
    private Long departmentId;

    @Schema(description = "机构名称")
    private String departmentName;

    @Schema(description = "文件数量")
    private Integer fileCounts;

    @Schema(description = "问题已关闭文件数量")
    private Integer closedLoopFileCounts;

    @Schema(description = "总审查点数量")
    private Integer totalCheckPoints;

    @Schema(description = "总通过的审查点数量")
    private Integer totalPassCheckPoints;

    @Schema(description = "总不通过的审查点数量")
    private Integer totalFailCheckPoints;

    @Schema(description = "总关闭审查点数量")
    private Integer totalClosedFailCheckPoints = 0;

    @Schema(description = "超半月未关闭原理图数据")
    private Integer exceedHalfMonthNotClosedFiles = 0;

}

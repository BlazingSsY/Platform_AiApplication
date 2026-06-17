package com.starmol.sourcecodereview.bean.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "代码审查统计数据项VO")
@Accessors(chain = true)
public class SourceCodeReviewStatisticsDataItemVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8475418762156391115L;

    @Schema(description = "部门ID")
    private Long departmentId;

    @Schema(description = "部门名称")
    private String departmentName;

    @Schema(description = "文件数量")
    private Long fileCount;

    @Schema(description = "版本数量")
    private Long versionCount;

    @Schema(description = "审查结果数量")
    private Long reviewResultCount;

    @Schema(description = "通过审查数量")
    private Long passedReviewCount;

    @Schema(description = "未通过审查数量")
    private Long failedReviewCount;

    @Schema(description = "待审查数量")
    private Long pendingReviewCount;

    @Schema(description = "通过率")
    private Double passRate;
} 
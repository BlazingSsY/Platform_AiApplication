package com.starmol.sourcecodereview.bean.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "代码审查统计导出数据详情DTO")
@Accessors(chain = true)
public class SourceCodeReviewStatisticsExportDataDTO {

    @Schema(description = "机构id")
    private Long departmentId;

    @Schema(description = "机构名称")
    private String departmentName;

    @Schema(description = "原代码文件数量")
    private Integer sourceFileCount;

    @Schema(description = "问题已关闭代码文件数量")
    private Integer closedLoopCount;

    @Schema(description = "检查文件数量")
    private Integer fileCount;

    @Schema(description = "通过检查文件数量")
    private Integer passFileCount;
}

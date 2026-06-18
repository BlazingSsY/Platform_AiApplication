package com.starmol.logicreview.bean.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "代码审查结果复审DTO")
@Accessors(chain = true)
public class SourceCodeReviewRecheckDTO {

    @Schema(description = "审查结果ID")
    private Long resultId;

    @Schema(description = "问题Id")
    private List<String> questionIds;

    @Schema(description = "版本")
    private String version;

    @Schema(description = "审查结论")
    private String recheckConclusion;

    @Schema(description = "问题描述")
    private String questionDesc;
}

package com.starmol.logicreview.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.logicreview.constant.ReviewStatusEnum;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "代码审查进度VO")
@Accessors(chain = true)
public class SourceCodeReviewProcessVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8475418712156331105L;

    @Schema(description = "总文件数")
    private Integer totalFilesize;

    @Schema(description = "已完成审查文件数")
    private Integer finishFilesize;

    @Schema(description = "正在审查文件")
    private String fileName;

    @Schema(description = "文件适用规则列表")
    List<SourceCodeReviewProcessRuleVO> fileRule;
} 
package com.starmol.sourcecodereview.bean.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.sourcecodereview.constant.FileRuleStatusEnum;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "代码审查进度VO")
@Accessors(chain = true)
public class SourceCodeReviewProcessRuleVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8415418712156331105L;

    @Schema(description = "规则id")
    private String ruleId;

    @Schema(description = "规则描述")
    private String rule;

    @Schema(description = "规则来源")
    private String ruleSource;

    @Schema(description = "规则类型")
    private String ruleType;

    @Schema(description = "规则状态(1:表示没有违反次规则的;2:表示有违反规则的;3:表示正在审校中)")
    private Integer status;

} 
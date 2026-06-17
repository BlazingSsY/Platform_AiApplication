package com.starmol.logicreview.bean.bo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.logicreview.model.base.BaseBean;

import java.io.Serial;
import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "代码审查结果详情VO")
@Accessors(chain = true)
public class SourceCodeReviewResultDetailRuleBO extends BaseBean implements Serializable {
    @Serial
    private static final long serialVersionUID = 8475418762156391113L;

    @Schema(description = "外键,关联到规则表")
    private Long ruleId;

    @Schema(description = "规则类型")
    private String ruleType;

    @Schema(description = "规则名称")
    private String ruleName;

    @Schema(description = "规则编号")
    private String ruleCode;

    @Schema(description = "源代码文件名称")
    private String sourceFileName;
} 
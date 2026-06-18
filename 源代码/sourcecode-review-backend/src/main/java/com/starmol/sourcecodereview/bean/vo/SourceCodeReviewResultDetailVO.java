package com.starmol.sourcecodereview.bean.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.sourcecodereview.model.base.BaseBean;

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
public class SourceCodeReviewResultDetailVO extends BaseBean implements Serializable {

    @Serial
    private static final long serialVersionUID = 8475418762156391113L;

    @Schema(description = "外键,关联到审查结果表")
    private Long resultId;

    @Schema(description = "外键,关联到规则表")
    private Long ruleId;

    @Schema(description = "规则类型(1:通用规则;2:元器件手册规则;3:技术规则)")
    private String ruleType;

    @Schema(description = "规则名称")
    private String ruleName;

    @Schema(description = "规则编号")
    private String ruleCode;

    @Schema(description = "规则来源")
    private String ruleSource;

    @Schema(description = "机理说明")
    private String explain;

    @Schema(description = "源代码文件名称")
    private String sourceFileName;

    @Schema(description = "代码行号")
    private String lineNumber;

    @Schema(description = "错误代码")
    private String errorCode;

    @Schema(description = "错误原因")
    private String errorReason;

    @Schema(description = "审查意见")
    private String reviewSuggestion;

    @Schema(description = "是否通过(0:未通过;1:通过)")
    private Integer isPassed;

    @Schema(description = "备注")
    private String comments;

    @Schema(description = "问题Id")
    private String questionId;

    @Schema(description = "审查结论")
    private String recheckConclusion;

    @Schema(description = "问题描述")
    private String questionDesc;

    @Schema(description = "复核状态(1:未复核;2:复核中;3:复核完成)")
    private Integer recheckStatus;

    @Schema(description = "复核结果状态(1:通过;2:未通过)")
    private Integer recheckResultStatus;

    @Schema(description = "拒绝理由")
    private String rejectReason;

    @Schema(description = "复核处理用户ID")
    private String recheckUserId;

} 
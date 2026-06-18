package com.starmol.logicreview.model.codereview;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.logicreview.model.base.IdBaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "ljsc_review_result_detail", autoResultMap = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "代码审查结果详情模型")
@ToString(callSuper = true)
@Accessors(chain = true)
public class SourceCodeReviewResultDetail extends IdBaseModel implements Serializable {

    @Schema(description = "外键,关联到审查结果表")
    private Long resultId;

    @Schema(description = "外键,关联到规则表")
    private Long ruleId;

    @Schema(description = "源代码文件名")
    private String sourceFileName;

    @Schema(description = "源代语言(JAVA/C_PLUS/C)")
    private String language;

    @Schema(description = "错误代码")
    private String errorCode;

    @Schema(description = "代码行号")
    private String lineNumber;

    @Schema(description = "错误原因")
    private String errorReason;

    @Schema(description = "审查意见")
    private String reviewSuggestion;

    @Schema(description = "是否通过(0:未通过;1:通过)")
    private Integer isPassed;
    
    @Schema(description = "备注")
    private String comments;

    @Schema(description = "规则编号")
    private String ruleCode;

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

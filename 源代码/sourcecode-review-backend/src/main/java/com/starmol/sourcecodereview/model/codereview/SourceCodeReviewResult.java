package com.starmol.sourcecodereview.model.codereview;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.sourcecodereview.constant.ReviewStatusEnum;
import com.starmol.sourcecodereview.model.base.IdBaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "dmsc_review_result", autoResultMap = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "代码审查结果模型")
@ToString(callSuper = true)
@Accessors(chain = true)
public class SourceCodeReviewResult extends IdBaseModel implements Serializable {
    
    @Schema(description = "外键,关联到文件表")
    private Long fileId;

    @Schema(description = "外键,关联到文件版本表")
    private Long fileVersionId;
    
    @Schema(description = "检查点数量")
    private Integer checkPoints;
    
    @Schema(description = "通过的检查点数量")
    private Integer passCheckPoints;
    
    @Schema(description = "通过率")
    private BigDecimal passRate;

    @Schema(description = "问题闭环状态(0:未闭环;1:已闭环)")
    private Integer isClosedLoop;
    
    @Schema(description = "开始审查的时间")
    private LocalDateTime reviewTime;
    
    @Schema(description = "状态(1:正在审查;2:审查完成;3:审查失败;4:审查错误)")
    private ReviewStatusEnum status;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "备注")
    private String comments;

    @Schema(description = "耗时(秒)")
    private Integer duration;

    @Schema(description =  "文件数")
    private Integer filesSize;

    @Schema(description =  "文件行数")
    private Integer filesLine;

    @Schema(description =  "使用规则数")
    private Integer useRuleSize;

    @Schema(description =  "问题数量")
    private Integer questions;
}

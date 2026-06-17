package com.starmol.logicreview.bean.vo;

import com.starmol.logicreview.constant.CodeReviewRuleSummaryEnum;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SourceCodeReviewWaitTaskVO implements Serializable {

    @Schema(description = "需要等待任务数")
    private Integer  taskNum;
} 
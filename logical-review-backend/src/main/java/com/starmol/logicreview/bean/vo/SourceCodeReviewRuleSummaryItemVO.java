package com.starmol.logicreview.bean.vo;

import com.starmol.logicreview.constant.CodeReviewRuleSummaryEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class SourceCodeReviewRuleSummaryItemVO implements Serializable {

    private SourceCodeReviewRuleVO rule;

    private CodeReviewRuleSummaryEnum summary;
} 
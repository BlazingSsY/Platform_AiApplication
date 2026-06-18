package com.starmol.sourcecodereview.bean.vo;

import com.starmol.sourcecodereview.constant.CodeReviewRuleSummaryEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class SourceCodeReviewRuleSummaryItemVO implements Serializable {

    private SourceCodeReviewRuleVO rule;

    private CodeReviewRuleSummaryEnum summary;
} 
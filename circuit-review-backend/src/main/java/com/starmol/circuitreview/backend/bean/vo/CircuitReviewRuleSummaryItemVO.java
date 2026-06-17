package com.starmol.circuitreview.backend.bean.vo;

import com.starmol.circuitreview.backend.constant.CircuitReviewRuleSummaryEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
public class CircuitReviewRuleSummaryItemVO implements Serializable {

    private CircuitReviewRuleVO rule;

    private CircuitReviewRuleSummaryEnum summary;
}

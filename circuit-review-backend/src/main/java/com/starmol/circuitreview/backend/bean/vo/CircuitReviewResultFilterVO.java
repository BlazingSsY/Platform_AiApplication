package com.starmol.circuitreview.backend.bean.vo;

import lombok.Data;

import java.util.List;

@Data
public class CircuitReviewResultFilterVO {
    private List<Integer> ruleTypeFilters;
    private List<String> deviceTypeFilters;
    private List<String> reviewRuleFilters;
    private List<Integer> isPassedFilters;
}

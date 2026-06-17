package com.starmol.logicreview.bean.vo;

import lombok.Data;

import java.util.List;

@Data
public class SourceCodeReviewResultFilterVO {
    private List<String> sourceFileNameFilters;
    private List<String> reviewRuleFilters;
} 
package com.starmol.sourcecodereview.bean.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class SourceCodeReviewStatisticsDataVO implements Serializable {

    private List<SourceCodeFileDetailVO> fileDetailVOList = new ArrayList<>();
    private Integer fileCount = 0;
    private Integer totalReviewPointCount = 0;
    private Integer totalPassReviewPointCount = 0;
    private BigDecimal totalPassRate = BigDecimal.ZERO;
    private Map<String, Map<String, Map<String, Object>>> chartDataMap;
} 
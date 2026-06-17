package com.starmol.circuitreview.backend.bean.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.*;

@Data
public class CircuitReviewStatisticsDataVO {
    private List<CircuitFileDetailVO> fileDetailVOList = new ArrayList<>();
    private Integer fileCount = 0;
    private Integer totalReviewPointCount = 0;
    private Integer totalPassReviewPointCount = 0;
    private Integer totalFailReviewPointCount = 0;
    private BigDecimal totalPassRate = BigDecimal.ZERO;
    private Map<String, Map<String, Map<String, Object>>> chartDataMap;
    private Long resultId;
}

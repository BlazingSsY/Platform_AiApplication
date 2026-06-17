package com.starmol.circuitreview.backend.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class CircuitReviewHomeStatisticsDataVO {
    private List<CircuitReviewHomeStatisticsDataItemVO> mostRecentlyReviewedFiles = new ArrayList<>();
    private List<CircuitReviewHomeStatisticsDataItemVO> highestPassRateFiles = new ArrayList<>();
    private Map<String, Integer> totalFilesCountByDepartment = new HashMap<>();
    private Map<String, Integer> reviewedFilesCountByDepartment = new HashMap<>();
    private Map<String, BigDecimal> userCountByDepartment = new HashMap<>();
    private Map<String, Integer> reviewIssueCountByDepartment = new HashMap<>();
}

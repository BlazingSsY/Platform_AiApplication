package com.starmol.sourcecodereview.bean.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class SourceCodeReviewHomeStatisticsDataVO implements Serializable {

    private List<SourceCodeReviewHomeStatisticsDataItemVO> mostRecentlyReviewedFiles = new ArrayList<>();
    private List<SourceCodeReviewHomeStatisticsDataItemVO> highestPassRateFiles = new ArrayList<>();
    private Map<String, Integer> totalFilesCountByDepartment = new HashMap<>();
    private Map<String, Integer> reviewedFilesCountByDepartment = new HashMap<>();
    private Map<String, Integer> userCountByDepartment = new HashMap<>();
    private Map<String, Integer> reviewIssueCountByDepartment = new HashMap<>();
} 
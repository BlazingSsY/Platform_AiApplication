package com.starmol.logicreview.service;

import com.starmol.logicreview.bean.dto.SourceCodeReviewStatisticsExportDataDTO;
import com.starmol.logicreview.bean.vo.SourceCodeFileDetailVO;
import com.starmol.logicreview.bean.vo.SourceCodeReviewHomeStatisticsDataVO;
import com.starmol.logicreview.bean.vo.SourceCodeReviewStatisticsDataVO;
import com.starmol.logicreview.bean.vo.SourceCodeReviewStatisticsRequestVO;
import com.starmol.logicreview.bean.vo.StatisticsExportRequestVO;

import java.util.List;

/**
 * 代码审查统计服务接口
 *
 * @author system
 * @date 2025-01-07
 */
public interface StatisticsService {

    /**
     * 获取代码审查主页统计数据
     *
     * @return 主页统计数据
     */
    SourceCodeReviewHomeStatisticsDataVO getHomeStatistics();

    /**
     * 获取代码审查统计数据
     *
     * @param request 统计请求
     * @return 统计数据
     */
    SourceCodeReviewStatisticsDataVO getSourceCodeReviewStatistics(SourceCodeReviewStatisticsRequestVO request);


    List<SourceCodeReviewStatisticsExportDataDTO> getStatisticsForExport(StatisticsExportRequestVO requestVO);

    List<SourceCodeFileDetailVO> getStatisticsFileDetailsByLatestVersion(SourceCodeReviewStatisticsRequestVO requestVO);
} 
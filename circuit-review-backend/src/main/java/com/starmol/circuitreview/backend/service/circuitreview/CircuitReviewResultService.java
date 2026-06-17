package com.starmol.circuitreview.backend.service.circuitreview;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starmol.circuitreview.backend.bean.vo.CircuitReviewRequestVO;
import com.starmol.circuitreview.backend.bean.vo.CircuitReviewResultFilterVO;
import com.starmol.circuitreview.backend.bean.vo.CircuitReviewResultVO;
import com.starmol.circuitreview.backend.bean.vo.CircuitReviewRuleSummaryItemVO;
import com.starmol.circuitreview.backend.model.circuitreview.CircuitReviewResult;
import com.starmol.circuitreview.backend.service.base.BaseService;

import java.util.List;

/**
 * 电路审查结果服务接口
 *
 * @author system
 * @date 2025-01-07
 */
public interface CircuitReviewResultService extends BaseService<CircuitReviewResult> {
    
    /**
     * 创建电路审查结果并返回VO
     */
    CircuitReviewResultVO createCircuitReviewResult(CircuitReviewResult circuitReviewResult);

    void removeResultByFileId(Long fileId);
    /**
     * 更新电路审查结果并返回VO
     */
    CircuitReviewResultVO updateCircuitReviewResult(Long id, CircuitReviewResult circuitReviewResult);
    
    /**
     * 根据ID获取电路审查结果VO
     */
    CircuitReviewResultVO getCircuitReviewResultVOById(Long id);
    
    /**
     * 分页查询电路审查结果VO
     */
    IPage<CircuitReviewResultVO> getCircuitReviewResultVOPage(Page<CircuitReviewResult> page, Long fileId);

    Long submitCircuitReview(CircuitReviewRequestVO circuitReviewRequestVO);

    List<CircuitReviewRuleSummaryItemVO> getCircuitReviewRuleSummary(Long id);

    CircuitReviewResultFilterVO getCircuitReviewResultFilters(Long id, Integer ruleType, String reviewRule, String deviceType, Integer isPassed);

    List<CircuitReviewResult> getResultByFileVersionIdList(List<Long> fileVersionId);

    List<CircuitReviewResult> getResultByFileIdList(List<Long> fileIdList);

    List<CircuitReviewResultVO> getCircuitFileVersionResults(Long versionId);

    void removeCircuitFileReviewResult(List<Long> removeTargets);

    boolean deleteCircuitReviewResult(Long id);
}
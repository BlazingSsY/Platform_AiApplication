package com.starmol.logicreview.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starmol.logicreview.bean.bo.SourceCodeReviewResultDetailRuleBO;
import com.starmol.logicreview.bean.vo.SourceCodeReviewResultDetailVO;
import com.starmol.logicreview.model.codereview.SourceCodeReviewResultDetail;
import com.starmol.logicreview.service.base.BaseService;

import java.util.List;

/**
 * 代码审查结果详情服务接口
 *
 * @author system
 * @date 2025-01-07
 */
public interface SourceCodeReviewResultDetailService extends BaseService<SourceCodeReviewResultDetail> {

    /**
     * 创建代码审查结果详情
     *
     * @param sourceCodeReviewResultDetail 代码审查结果详情
     * @return 代码审查结果详情VO
     */
    SourceCodeReviewResultDetailVO createSourceCodeReviewResultDetail(SourceCodeReviewResultDetail sourceCodeReviewResultDetail);

    void removeDetailByResultIdList(List<Long> resultIdList);
    
    /**
     * 更新代码审查结果详情
     *
     * @param id 代码审查结果详情ID
     * @param sourceCodeReviewResultDetail 代码审查结果详情
     * @return 代码审查结果详情VO
     */
    SourceCodeReviewResultDetailVO updateSourceCodeReviewResultDetail(Long id, SourceCodeReviewResultDetail sourceCodeReviewResultDetail);

    /**
     * 根据ID获取代码审查结果详情VO
     *
     * @param id 代码审查结果详情ID
     * @return 代码审查结果详情VO
     */
    SourceCodeReviewResultDetailVO getSourceCodeReviewResultDetailVOById(Long id);

    /**
     * 分页查询代码审查结果详情VO
     */
    IPage<SourceCodeReviewResultDetailVO> getSourceCodeReviewResultDetailVOPage(IPage page, Long resultId, String ruleName, String ruleType, String sourceFileName, String lineNumber, String errorCode, String errorReason, String reviewSuggestion, Integer isPassed);

    /**
     * 查询代码审查结果详情VO列表
     */
    List<SourceCodeReviewResultDetailVO> getSourceCodeReviewResultDetailVOList(Long resultId, String ruleName, String ruleType, String sourceFileName, String lineNumber, String errorCode, String errorReason, String reviewSuggestion, Integer isPassed);

    List<SourceCodeReviewResultDetailVO> getAllDetailsByResultId(Long resultId);

    List<SourceCodeReviewResultDetailRuleBO> getAllDetailsAndRuleByResultId(Long resultId);

    List<String> getSourceCodeReviewResultFilters(Long id, Integer isPassed);
    List<String> getCircuitReviewResultRuleFilters(Long id, Integer isPassed);
}
package com.starmol.logicreview.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starmol.logicreview.bean.bo.CodeReviewResultBO;
import com.starmol.logicreview.bean.dto.SourceCodeDTO;
import com.starmol.logicreview.bean.vo.CodeReviewResultVO;
import com.starmol.logicreview.bean.vo.SourceCodeContentVO;
import com.starmol.logicreview.bean.vo.SourceCodeReviewProcessVO;
import com.starmol.logicreview.bean.vo.SourceCodeReviewRequestVO;
import com.starmol.logicreview.bean.vo.SourceCodeReviewResultDetailVO;
import com.starmol.logicreview.bean.vo.SourceCodeReviewResultFilterVO;
import com.starmol.logicreview.bean.vo.SourceCodeReviewResultVO;
import com.starmol.logicreview.bean.vo.SourceCodeReviewRuleSummaryItemVO;
import com.starmol.logicreview.bean.vo.SourceCodeReviewStopVO;
import com.starmol.logicreview.bean.vo.SourceCodeReviewWaitTaskVO;
import com.starmol.logicreview.model.codereview.SourceCodeReviewResult;
import com.starmol.logicreview.service.base.BaseService;

import java.util.List;

/**
 * 代码审查结果服务接口
 *
 * @author system
 * @date 2025-01-07
 */
public interface SourceCodeReviewResultService extends BaseService<SourceCodeReviewResult> {

    /**
     * 创建代码审查结果
     *
     * @param sourceCodeReviewResult 代码审查结果
     * @return 代码审查结果VO
     */
    SourceCodeReviewResultVO createSourceCodeReviewResult(SourceCodeReviewResult sourceCodeReviewResult);

    void removeResultByFileId(Long fileId);

    void removeResultByFileIdWithoutCheckPermission(Long fileId);
    /**
     * 更新代码审查结果
     *
     * @param id 代码审查结果ID
     * @param sourceCodeReviewResult 代码审查结果
     * @return 代码审查结果VO
     */
    SourceCodeReviewResultVO updateSourceCodeReviewResult(Long id, SourceCodeReviewResult sourceCodeReviewResult);


    List<SourceCodeReviewResultDetailVO> getSourceCodeReviewResultDetailVOAllListByResult(Long id);

    CodeReviewResultVO getSourceCodeReviewResultByResultAndVersion(Long id, String version);

    /**
     * 根据ID获取代码审查结果VO
     *
     * @param id 代码审查结果ID
     * @return 代码审查结果VO
     */
    SourceCodeReviewResultVO getSourceCodeReviewResultVOById(Long id);

    SourceCodeReviewProcessVO getSourceCodeReviewProcessResultVOById(Long id);

    IPage<SourceCodeReviewResultVO> getSourceCodeReviewResultVOPage(Page<SourceCodeReviewResult> page, Long fileId);

    Long submitSourceCodeReview(SourceCodeReviewRequestVO sourceCodeReviewRequestVO);

    void stopSourceCodeReview(SourceCodeReviewStopVO sourceCodeReviewStopVO);

    List<SourceCodeReviewRuleSummaryItemVO> getSourceCodeReviewRuleSummary(Long id);

    SourceCodeReviewWaitTaskVO getWaitTask(Long id);

    SourceCodeReviewResultFilterVO getSourceCodeReviewResultFilters(Long id, Integer isPassed);

    List<SourceCodeReviewResult> getResultByFileIdList(List<Long> fileVersionId);

    List<SourceCodeReviewResultVO> getSourceCodeFileVersionResults(Long versionId, Boolean includeAllStatus);

    void getAllCodeReviewResultAndUpdate();

    boolean deleteSourceCodeReviewResult(Long id);

    SourceCodeContentVO getSourceCodeContent(Long resultId, String fileName, String offset, String version);

    List<String> getVersionByResultId(Long id);
}
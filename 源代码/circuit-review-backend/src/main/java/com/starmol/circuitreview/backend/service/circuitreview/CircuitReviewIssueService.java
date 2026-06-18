package com.starmol.circuitreview.backend.service.circuitreview;

import com.starmol.circuitreview.backend.model.circuitreview.CircuitReviewIssue;
import com.starmol.circuitreview.backend.service.base.BaseService;

import java.util.List;

/**
 * 电路审查问题服务接口
 *
 * @author system
 * @date 2026-04-02
 */
public interface CircuitReviewIssueService extends BaseService<CircuitReviewIssue> {
    /**
     * 批量保存电路审查问题（自动去重）
     * 去重规则：同一个电路图文件中，器件型号、位号管脚、审查意见和规则编号这四个属性相同的问题视为同一个问题
     *
     * @param issues 待保存的问题列表
     */
    void saveIssuesBatch(List<CircuitReviewIssue> issues);

    Long getFileReviewIssueCount(Long fileId);
}

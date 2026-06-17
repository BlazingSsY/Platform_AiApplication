package com.starmol.circuitreview.backend.service.circuitreview.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starmol.circuitreview.backend.model.circuitreview.CircuitReviewIssue;
import com.starmol.circuitreview.backend.repository.circuitreview.CircuitReviewIssueMapper;
import com.starmol.circuitreview.backend.service.base.impl.BaseServiceImpl;
import com.starmol.circuitreview.backend.service.circuitreview.CircuitReviewIssueService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 电路审查问题服务实现类
 *
 * @author system
 * @since 2026-04-02
 */
@Service
@Slf4j
public class CircuitReviewIssueServiceImpl extends BaseServiceImpl<CircuitReviewIssueMapper, CircuitReviewIssue> implements CircuitReviewIssueService {
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveIssuesBatch(List<CircuitReviewIssue> issues) {
        if (CollectionUtils.isEmpty(issues)) {
            return;
        }

        Long fileId = issues.get(0).getFileId();

        List<CircuitReviewIssue> existingIssues = this.list(
                Wrappers.<CircuitReviewIssue>lambdaQuery()
                        .eq(CircuitReviewIssue::getFileId, fileId)
        );

        List<String> existingKeys = existingIssues.stream()
                .map(this::generateUniqueKey)
                .toList();

        List<CircuitReviewIssue> newIssues = issues.stream()
                .filter(issue -> !existingKeys.contains(generateUniqueKey(issue)))
                .toList();

        if (CollectionUtils.isNotEmpty(newIssues)) {
            log.info("文件 ID: {}, 本次提交 {} 个问题，其中新增 {} 个问题",
                    fileId, issues.size(), newIssues.size());
            this.saveBatch(newIssues);
        } else {
            log.info("文件 ID: {}, 本次提交 {} 个问题，无新增问题", fileId, issues.size());
        }
    }

    @Override
    public Long getFileReviewIssueCount(Long fileId) {
        return this.count(Wrappers.<CircuitReviewIssue>lambdaQuery().eq(CircuitReviewIssue::getFileId, fileId));
    }

    private String generateUniqueKey(CircuitReviewIssue issue) {
        String deviceType = issue.getDeviceType() != null ? issue.getDeviceType() : "";
        String tagPin = issue.getTagPin() != null ? issue.getTagPin() : "";
        String reviewSuggestion = issue.getReviewSuggestion() != null ? issue.getReviewSuggestion() : "";
        String ruleCode = issue.getRuleCode() != null ? issue.getRuleCode() : "";

        return String.format("%s|%s|%s|%s|%s",
                issue.getFileId(), deviceType, tagPin, reviewSuggestion, ruleCode);
    }
}

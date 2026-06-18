package com.starmol.sourcecodereview.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starmol.sourcecodereview.bean.dto.CodeReviewRecheckResultSubmitDTO;
import com.starmol.sourcecodereview.bean.dto.SourceCodeReviewRecheckDTO;
import com.starmol.sourcecodereview.bean.vo.CodeRecheckDetailVO;
import com.starmol.sourcecodereview.bean.vo.CodeReviewResultVO;
import com.starmol.sourcecodereview.bean.vo.SourceCodeFileAuditVO;
import com.starmol.sourcecodereview.model.codereview.SourceCodeReviewResult;

/**
 * 代码审查结果服务接口
 *
 * @author system
 * @date 2025-01-07
 */
public interface SourceCodeReviewRecheckService {

    /**
     * 创建代码审查结果复审请求
     *
     * @param sourceCodeReviewRecheckDTO 代码审查结果复审请求
     * @return 代码审查结果VO
     */
    void createSourceCodeReviewRecheck(SourceCodeReviewRecheckDTO sourceCodeReviewRecheckDTO);

    IPage<SourceCodeFileAuditVO> getCodeRecheckList(Long pageNumber, Long pageSize, String fileName, Long depId, Long userId, Integer status);

    CodeRecheckDetailVO getSourceCodeReviewRecheckDetailByResultAndVersion(Long fileVersionId, String version);

    void submitReviewRecheckResults(CodeReviewRecheckResultSubmitDTO codeReviewRecheckResultSubmitDTO);
}
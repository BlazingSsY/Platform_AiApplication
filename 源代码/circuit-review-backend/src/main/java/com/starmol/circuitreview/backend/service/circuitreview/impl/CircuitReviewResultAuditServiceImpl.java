package com.starmol.circuitreview.backend.service.circuitreview.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starmol.circuitreview.backend.bean.vo.CircuitFileAuditVO;
import com.starmol.circuitreview.backend.bean.vo.CircuitReviewResultAuditVO;
import com.starmol.circuitreview.backend.constant.CircuitFileAuditSortFieldEnum;
import com.starmol.circuitreview.backend.constant.CircuitReviewResultDetailAuditTypeEnum;
import com.starmol.circuitreview.backend.constant.SortDirectionEnum;
import com.starmol.circuitreview.backend.model.circuitreview.CircuitFileVersion;
import com.starmol.circuitreview.backend.model.circuitreview.CircuitReviewResultAudit;
import com.starmol.circuitreview.backend.repository.circuitreview.CircuitReviewResultAuditMapper;
import com.starmol.circuitreview.backend.service.base.impl.BaseCascadeServiceImpl;
import com.starmol.circuitreview.backend.service.circuitreview.CircuitFileVersionService;
import com.starmol.circuitreview.backend.service.circuitreview.CircuitReviewResultAuditService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 电路审查结果审核服务实现类
 */
@Service
@RequiredArgsConstructor
public class CircuitReviewResultAuditServiceImpl extends BaseCascadeServiceImpl<CircuitReviewResultAuditMapper, CircuitReviewResultAudit> implements CircuitReviewResultAuditService {

    private final ObjectMapper objectMapper;

    private final CircuitFileVersionService circuitFileVersionService;

    @Override
    public IPage<CircuitFileAuditVO> getCircuitReviewResultAuditVOPageForAdmin(Page<CircuitFileAuditVO> page, String fileName, Long depId, Long userId, Integer status, CircuitFileAuditSortFieldEnum sortField, SortDirectionEnum sortDirection) {
        return getCircuitReviewResultAuditVOPage(page, fileName, depId, userId, status, sortField, sortDirection, true);
    }

    @Override
    public IPage<CircuitFileAuditVO> getCircuitReviewResultAuditVOPageForExpert(Page<CircuitFileAuditVO> page, String fileName, Long depId, Long userId, Integer status, CircuitFileAuditSortFieldEnum sortField, SortDirectionEnum sortDirection) {
        return getCircuitReviewResultAuditVOPage(page, fileName, depId, userId, status, sortField, sortDirection, false);
    }

    private IPage<CircuitFileAuditVO> getCircuitReviewResultAuditVOPage(Page<CircuitFileAuditVO> page, String fileName, Long depId, Long userId, Integer status, CircuitFileAuditSortFieldEnum sortField, SortDirectionEnum sortDirection, boolean isAdmin) {
        List<CircuitReviewResultDetailAuditTypeEnum> auditTypeEnumList;
        if(isAdmin) {
            auditTypeEnumList = List.of(CircuitReviewResultDetailAuditTypeEnum.REVIEW_INCORRECT);
        }
        else {
            auditTypeEnumList = List.of(CircuitReviewResultDetailAuditTypeEnum.RULE_NOT_APPLICABLE, CircuitReviewResultDetailAuditTypeEnum.ISSUE_EXCEPTION, CircuitReviewResultDetailAuditTypeEnum.REVIEW_NO_IMPACT);
        }
        // 首先获取文件基础信息（按fileId去重）
        IPage<CircuitFileAuditVO> filePage = this.getBaseMapper().getCircuitReviewResultAuditVOPage(page, fileName, depId, userId, status, auditTypeEnumList, sortField, sortDirection, isAdmin);
        
        // 对每个文件获取其所有审核结果并填充到resultAuditVOList
        filePage.getRecords().forEach(fileAuditVO -> {
            // 获取该文件的所有审核结果，按isAuditFinished升序（未完成的在前），然后按auditTime降序（时间近的在前）
            LambdaQueryWrapper<CircuitReviewResultAudit> queryWrapper = Wrappers.<CircuitReviewResultAudit>lambdaQuery()
                    .eq(CircuitReviewResultAudit::getFileId, fileAuditVO.getFileId())
                    .orderByDesc(CircuitReviewResultAudit::getAuditTime);
            
            // 查询并转换为VO列表
            var resultAudits = this.list(queryWrapper);
            var resultAuditVOList = resultAudits.stream()
                    .map(audit -> objectMapper.convertValue(audit, CircuitReviewResultAuditVO.class))
                    .toList();

            resultAuditVOList.forEach(circuitReviewResultAuditVO -> {
                List<CircuitFileVersion> fileVersionList = circuitFileVersionService.list(Wrappers.<CircuitFileVersion>lambdaQuery().eq(CircuitFileVersion::getId, circuitReviewResultAuditVO.getFileVersionId()));
                if(CollectionUtils.isNotEmpty(fileVersionList)) {
                    circuitReviewResultAuditVO.setFileVersionName(fileVersionList.get(0).getFileName());
                }
            });

            fileAuditVO.setResultAuditVOList(resultAuditVOList);
        });
        
        return filePage;
    }

    @Override
    public CircuitReviewResultAudit getByResultId(Long resultId) {
        return this.getOne(Wrappers.<CircuitReviewResultAudit>lambdaQuery().eq(CircuitReviewResultAudit::getResultId, resultId));
    }
}
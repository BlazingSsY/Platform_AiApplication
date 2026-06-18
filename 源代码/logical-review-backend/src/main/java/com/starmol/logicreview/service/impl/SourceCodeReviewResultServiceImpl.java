package com.starmol.logicreview.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starmol.logicreview.bean.bo.CodeReviewAllRuleBO;
import com.starmol.logicreview.bean.bo.CodeReviewCodeResultBO;
import com.starmol.logicreview.bean.bo.CodeReviewFileResultBO;
import com.starmol.logicreview.bean.bo.CodeReviewResultBO;
import com.starmol.logicreview.bean.bo.CodeReviewRuleBO;
import com.starmol.logicreview.bean.bo.ResponseDataBO;
import com.starmol.logicreview.bean.bo.ResultVersionListBO;
import com.starmol.logicreview.bean.bo.ReviewSummaryBO;
import com.starmol.logicreview.bean.bo.ReviewSummaryResultBO;
import com.starmol.logicreview.bean.bo.StopReviewResultBO;
import com.starmol.logicreview.bean.dto.FilterAllRulesDTO;
import com.starmol.logicreview.bean.dto.RuleFilterDTO;
import com.starmol.logicreview.bean.dto.UserDTO;
import com.starmol.logicreview.bean.vo.CodeReviewResultVO;
import com.starmol.logicreview.bean.vo.SourceCodeContentVO;
import com.starmol.logicreview.bean.vo.SourceCodeFileVersionVO;
import com.starmol.logicreview.bean.vo.SourceCodeReviewProcessVO;
import com.starmol.logicreview.bean.vo.SourceCodeReviewRequestVO;
import com.starmol.logicreview.bean.vo.SourceCodeReviewResultDetailVO;
import com.starmol.logicreview.bean.vo.SourceCodeReviewResultFilterVO;
import com.starmol.logicreview.bean.vo.SourceCodeReviewResultVO;
import com.starmol.logicreview.bean.vo.SourceCodeReviewRuleSummaryItemVO;
import com.starmol.logicreview.bean.vo.SourceCodeReviewRuleVO;
import com.starmol.logicreview.bean.vo.SourceCodeReviewStopVO;
import com.starmol.logicreview.bean.vo.SourceCodeReviewWaitTaskVO;
import com.starmol.logicreview.common.ResponseWrapper;
import com.starmol.logicreview.common.UserMetaData;
import com.starmol.logicreview.constant.CodeReviewRuleSummaryEnum;
import com.starmol.logicreview.constant.Constant;
import com.starmol.logicreview.constant.ReviewStatusEnum;
import com.starmol.logicreview.constant.SysRoleTypeEnum;
import com.starmol.logicreview.exception.KnowException;
import com.starmol.logicreview.model.codereview.SourceCodeFileVersion;
import com.starmol.logicreview.model.codereview.SourceCodeReviewResult;
import com.starmol.logicreview.model.codereview.SourceCodeReviewResultDetail;
import com.starmol.logicreview.repository.codereview.SourceCodeReviewResultMapper;
import com.starmol.logicreview.service.SourceCodeFileVersionService;
import com.starmol.logicreview.service.SourceCodeReviewResultDetailService;
import com.starmol.logicreview.service.SourceCodeReviewResultService;
import com.starmol.logicreview.service.SourceCodeReviewRuleService;
import com.starmol.logicreview.service.base.impl.BaseServiceImpl;
import com.starmol.logicreview.service.common.StorageService;
import com.starmol.logicreview.service.feign.CodeReviewClient;
import com.starmol.logicreview.service.feign.UserServiceClient;
import com.starmol.logicreview.utils.HttpRequestUtil;
import com.starmol.logicreview.utils.IdWorker;
import com.starmol.logicreview.utils.SpringContextUtils;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * 代码审查结果服务实现类
 *
 * @author system
 * @since 2025-01-07
 */

@Service
@RequiredArgsConstructor
@Slf4j
@EnableAsync
public class SourceCodeReviewResultServiceImpl extends BaseServiceImpl<SourceCodeReviewResultMapper, SourceCodeReviewResult> implements SourceCodeReviewResultService {

    private final String DEFAULT_USER_NAME = "pollingdefaultuser";
    private final ObjectMapper objectMapper;
    private final CodeReviewClient codeReviewClient;
    private final SourceCodeReviewRuleService sourceCodeReviewRuleService;
    private final SourceCodeReviewResultDetailService sourceCodeReviewResultDetailService;
    private final StorageService storageService;
    private final UserServiceClient userServiceClient;


    @Override
    public SourceCodeReviewResultVO createSourceCodeReviewResult(SourceCodeReviewResult sourceCodeReviewResult) {
        SourceCodeReviewResult savedSourceCodeReviewResult = this.saveAndReturnObject(sourceCodeReviewResult);
        return convertToVO(savedSourceCodeReviewResult);
    }

    @Override
    public void removeResultByFileId(Long fileId) {
        // 获取当前用户信息
        UserMetaData currentUser = HttpRequestUtil.getUser();
        if (currentUser == null) {
            throw new KnowException("获取当前用户信息失败");
        }
        // 获取用户角色
        SysRoleTypeEnum userRole = currentUser.getSysRoleType();
        if ((userRole == null) || (!SysRoleTypeEnum.SUPER_SUPERVISOR.equals(userRole) && !SysRoleTypeEnum.ADMIN.equals(userRole))) {
            throw new KnowException("当前用户无权限删除审查记录");
        }
        log.info("删除逻辑审查结果，文件ID: {}", fileId);

        List<SourceCodeReviewResult> sourceCodeReviewResults = this.list(
                Wrappers.<SourceCodeReviewResult>lambdaQuery()
                        .eq(SourceCodeReviewResult::getFileId, fileId)
        );
        List<Long> idList = sourceCodeReviewResults.stream().map(SourceCodeReviewResult::getId).toList();
        if (CollectionUtils.isNotEmpty(idList)) {
            this.remove(Wrappers.<SourceCodeReviewResult>lambdaQuery().in(SourceCodeReviewResult::getId, idList));
            sourceCodeReviewResultDetailService.removeDetailByResultIdList(idList);
        } else {
            log.info("无逻辑审查结果，文件ID: {}", fileId);
        }
    }

    @Override
    public void removeResultByFileIdWithoutCheckPermission(Long fileId) {
        List<SourceCodeReviewResult> sourceCodeReviewResults = this.list(
                Wrappers.<SourceCodeReviewResult>lambdaQuery()
                        .eq(SourceCodeReviewResult::getFileId, fileId)
        );
        List<Long> idList = sourceCodeReviewResults.stream().map(SourceCodeReviewResult::getId).toList();
        if (CollectionUtils.isNotEmpty(idList)) {
            this.remove(Wrappers.<SourceCodeReviewResult>lambdaQuery().in(SourceCodeReviewResult::getId, idList));
            sourceCodeReviewResultDetailService.removeDetailByResultIdList(idList);
        } else {
            log.info("无逻辑审查结果，文件ID: {}", fileId);
        }
    }


    @Override
    public SourceCodeReviewResultVO updateSourceCodeReviewResult(Long id, SourceCodeReviewResult sourceCodeReviewResult) {
        sourceCodeReviewResult.setId(id);
        SourceCodeReviewResult updatedSourceCodeReviewResult = this.updateByIDAndReturnObject(sourceCodeReviewResult);
        return convertToVO(updatedSourceCodeReviewResult);
    }

    @SneakyThrows
    @Override
    public List<SourceCodeReviewResultDetailVO> getSourceCodeReviewResultDetailVOAllListByResult(Long id) {
        //按照用户给定的新方案,这里需要根据审查结果ID,通过用户提供的API一次性获取所有审查结果详情, 按照前端展示要求组装后返回给前端
        SourceCodeReviewResultService sourceCodeReviewResultService = SpringContextUtils.getInstanceByType(SourceCodeReviewResultService.class);
        SourceCodeReviewResult sourceCodeReviewResult = sourceCodeReviewResultService.getById(id);
        SourceCodeFileVersionService sourceCodeFileVersionService = SpringContextUtils.getInstanceByType(SourceCodeFileVersionService.class);
        SourceCodeFileVersionVO sourceCodeFileVersionVO = sourceCodeFileVersionService.getSourceCodeFileVersionVOByVersionId(sourceCodeReviewResult.getFileVersionId());
        //调用第三方接口获取审查结果
        if (Objects.nonNull(sourceCodeFileVersionVO) && StringUtils.isNotBlank(sourceCodeFileVersionVO.getMinioId())) {
            Map<String, Object> resultMap = getStringObjectMap(sourceCodeFileVersionVO.getMinioId(),HttpRequestUtil.getDebugId(), HttpRequestUtil.getLoginName(), null);
            if (resultMap.get("code").equals(200)) {
                log.info("前端获取状态时,获取审查结果成功, ID: {}", sourceCodeFileVersionVO.getMinioId());
                //将resultMap中的Map结构的data属性转换成CodeReviewResultBO
                CodeReviewResultBO codeReviewResultBO = objectMapper.convertValue(resultMap.get("data"), CodeReviewResultBO.class);
                //判断codeReviewResultBO中是否有值
                if (Objects.nonNull(codeReviewResultBO) && Objects.nonNull(codeReviewResultBO.getStatus())) {
                    //只有状态为1或者2时, 才进行下一步处理
                    if (codeReviewResultBO.getStatus().equals(ReviewStatusEnum.FINISHED.getValue()) || codeReviewResultBO.getStatus().equals(ReviewStatusEnum.IN_PROGRESS.getValue())) {
                        //将结果转换为 SourceCodeReviewResultDetail列表
                        List<SourceCodeReviewResultDetail> details = new ArrayList<>();
                        for (CodeReviewFileResultBO codeReviewFileResultBO : codeReviewResultBO.getFilesResult()) {
                            SourceCodeReviewResultDetail detail = new SourceCodeReviewResultDetail();
                            detail.setResultId(sourceCodeReviewResult.getId());
                            detail.setRuleCode(codeReviewFileResultBO.getRuleId());
                            detail.setSourceFileName(codeReviewFileResultBO.getFileName());
                            detail.setLanguage(codeReviewFileResultBO.getLanguage());
                            detail.setIsPassed(codeReviewFileResultBO.getStatus().equals(1) ? 1 : 0);
                            if (CollectionUtils.isNotEmpty(codeReviewFileResultBO.getCodes())){
                                //遍历codeReviewFileResultBO.getCodes()将数据保存到details中
                                for (CodeReviewCodeResultBO code : codeReviewFileResultBO.getCodes()) {
                                    SourceCodeReviewResultDetail errorDetail = new SourceCodeReviewResultDetail();
                                    errorDetail.setResultId(detail.getResultId());
                                    errorDetail.setRuleCode(detail.getRuleCode());
                                    errorDetail.setSourceFileName(detail.getSourceFileName());
                                    errorDetail.setLanguage(detail.getLanguage());
                                    errorDetail.setIsPassed(detail.getIsPassed());
                                    errorDetail.setErrorCode(code.getCode());
                                    errorDetail.setLineNumber(code.getLineNumber());
                                    errorDetail.setErrorReason(code.getErrorReason());
                                    errorDetail.setReviewSuggestion(code.getModifySuggest());
                                    errorDetail.setQuestionId(code.getQuestionId());
                                    errorDetail.setQuestionDesc(code.getQuestionDesc());
                                    errorDetail.setRecheckConclusion(code.getRecheckConclusion());
                                    errorDetail.setRecheckStatus(code.getRecheckStatus());
                                    errorDetail.setRecheckResultStatus(code.getRecheckResultStatus());
                                    errorDetail.setRejectReason(code.getRejectReason());
                                    errorDetail.setRecheckUserId(code.getRecheckUserId());
                                    detail.setCreateUser(sourceCodeReviewResult.getCreateUser());
                                    detail.setId(IdWorker.getId());
                                    detail.setCreateDate(LocalDateTime.now());
                                    details.add(errorDetail);
                                }
                            }
                            else {
                                //代码文件审核通过只有一条记录
                                detail.setCreateUser(sourceCodeReviewResult.getCreateUser());
                                detail.setId(IdWorker.getId());
                                detail.setCreateDate(LocalDateTime.now());
                                details.add(detail);
                            }
                        }
                        //调用用户接口,获取所有审查规则(根据用户提供的信息,大概200多个)
                        FilterAllRulesDTO filterAllRulesDTO = new FilterAllRulesDTO();
                        filterAllRulesDTO.setFilter(new RuleFilterDTO().setLanguage(Collections.emptyList()).setSelectStatus(Collections.emptyList()).setRuleType(Collections.emptyList()).setRuleSource(Collections.emptyList()).setDesc(""));
                        CodeReviewAllRuleBO codeReviewRuleServiceAllRules =sourceCodeReviewRuleService.getAllRules(filterAllRulesDTO);
                        List<CodeReviewRuleBO>  codeReviewRuleBOList = codeReviewRuleServiceAllRules.getRules();
                        //获取规则以 规则id 为key的 MAP
                        Map<String, CodeReviewRuleBO> codeReviewRuleBOMap = codeReviewRuleBOList.stream().collect(Collectors.toMap(CodeReviewRuleBO::getId, Function.identity(), (oldValue, newValue) -> newValue));
                        List<SourceCodeReviewResultDetailVO> sourceCodeReviewResultDetailVOList = details.stream().map(detail -> {
                            SourceCodeReviewResultDetailVO sourceCodeReviewResultDetailVO = objectMapper.convertValue(detail, SourceCodeReviewResultDetailVO.class);
                            if (Objects.nonNull(detail.getRuleCode()) && codeReviewRuleBOMap.containsKey(detail.getRuleCode())) {
                                sourceCodeReviewResultDetailVO.setRuleName(codeReviewRuleBOMap.get(detail.getRuleCode()).getDesc());
                                sourceCodeReviewResultDetailVO.setRuleType(codeReviewRuleBOMap.get(detail.getRuleCode()).getRuleType());
                                sourceCodeReviewResultDetailVO.setRuleSource(codeReviewRuleBOMap.get(detail.getRuleCode()).getRuleSource());
                                sourceCodeReviewResultDetailVO.setExplain(codeReviewRuleBOMap.get(detail.getRuleCode()).getExplain());
                            }
                            return sourceCodeReviewResultDetailVO;
                        }).collect(Collectors.toList());
                        return sourceCodeReviewResultDetailVOList;
                    }
                }
            } else {
                log.error("获取审查结果详情失败,调用接口失败, Review ID: {}", sourceCodeFileVersionVO.getMinioId());
                throw new KnowException("逻辑审查错误,调用接口失败");
            }
        }
        return Collections.emptyList();
    }



    //2026-02-03: 按照新的需要这个接口,可以接收版本号参数,
    @SneakyThrows
    @Override
    public CodeReviewResultVO getSourceCodeReviewResultByResultAndVersion(Long id, String version) {
        //按照用户给定的新方案,这里需要根据审查结果ID,通过用户提供的API一次性获取所有审查结果详情, 按照前端展示要求组装后返回给前端
        SourceCodeReviewResultService sourceCodeReviewResultService = SpringContextUtils.getInstanceByType(SourceCodeReviewResultService.class);
        SourceCodeReviewResult sourceCodeReviewResult = sourceCodeReviewResultService.getById(id);
        SourceCodeFileVersionService sourceCodeFileVersionService = SpringContextUtils.getInstanceByType(SourceCodeFileVersionService.class);
        SourceCodeFileVersionVO sourceCodeFileVersionVO = sourceCodeFileVersionService.getSourceCodeFileVersionVOByVersionId(sourceCodeReviewResult.getFileVersionId());
        CodeReviewResultVO codeReviewResultVO = new CodeReviewResultVO();
        //调用第三方接口获取审查结果
        if (Objects.nonNull(sourceCodeFileVersionVO) && StringUtils.isNotBlank(sourceCodeFileVersionVO.getMinioId())) {
            Map<String, Object> resultMap = getStringObjectMap(sourceCodeFileVersionVO.getMinioId(),HttpRequestUtil.getDebugId(), HttpRequestUtil.getLoginName(), version);
            if (resultMap.get("code").equals(200)) {
                log.info("前端获取状态时,根据版本获取审查结果成功, ID: {}", sourceCodeFileVersionVO.getMinioId());
                //将resultMap中的Map结构的data属性转换成CodeReviewResultBO
                CodeReviewResultBO codeReviewResultBO = objectMapper.convertValue(resultMap.get("data"), CodeReviewResultBO.class);
                codeReviewResultVO = objectMapper.convertValue(codeReviewResultBO, CodeReviewResultVO.class);//转换为CodeReviewResultVO
                //判断codeReviewResultBO中是否有值
                if (Objects.nonNull(codeReviewResultBO) && Objects.nonNull(codeReviewResultBO.getStatus())) {
                    //只有状态为1或者2时, 才进行下一步处理
                    if (codeReviewResultBO.getStatus().equals(ReviewStatusEnum.FINISHED.getValue()) || codeReviewResultBO.getStatus().equals(ReviewStatusEnum.IN_PROGRESS.getValue())) {
                        //将结果转换为 SourceCodeReviewResultDetail列表
                        List<SourceCodeReviewResultDetail> details = new ArrayList<>();
                        for (CodeReviewFileResultBO codeReviewFileResultBO : codeReviewResultBO.getFilesResult()) {
                            SourceCodeReviewResultDetail detail = new SourceCodeReviewResultDetail();
                            detail.setResultId(sourceCodeReviewResult.getId());
                            detail.setRuleCode(codeReviewFileResultBO.getRuleId());
                            detail.setSourceFileName(codeReviewFileResultBO.getFileName());
                            detail.setLanguage(codeReviewFileResultBO.getLanguage());
                            detail.setIsPassed(codeReviewFileResultBO.getStatus().equals(1) ? 1 : 0);
                            if (CollectionUtils.isNotEmpty(codeReviewFileResultBO.getCodes())){
                                //遍历codeReviewFileResultBO.getCodes()将数据保存到details中
                                for (CodeReviewCodeResultBO code : codeReviewFileResultBO.getCodes()) {
                                    SourceCodeReviewResultDetail errorDetail = new SourceCodeReviewResultDetail();
                                    errorDetail.setResultId(detail.getResultId());
                                    errorDetail.setRuleCode(detail.getRuleCode());
                                    errorDetail.setSourceFileName(detail.getSourceFileName());
                                    errorDetail.setLanguage(detail.getLanguage());
                                    errorDetail.setIsPassed(detail.getIsPassed());
                                    errorDetail.setErrorCode(code.getCode());
                                    errorDetail.setLineNumber(code.getLineNumber());
                                    errorDetail.setErrorReason(code.getErrorReason());
                                    errorDetail.setReviewSuggestion(code.getModifySuggest());
                                    errorDetail.setQuestionId(code.getQuestionId());
                                    errorDetail.setQuestionDesc(code.getQuestionDesc());
                                    errorDetail.setRecheckConclusion(code.getRecheckConclusion());
                                    errorDetail.setRecheckStatus(code.getRecheckStatus());
                                    errorDetail.setRecheckResultStatus(code.getRecheckResultStatus());
                                    errorDetail.setRejectReason(code.getRejectReason());
                                    errorDetail.setRecheckUserId(code.getRecheckUserId());
                                    detail.setCreateUser(sourceCodeReviewResult.getCreateUser());
                                    detail.setId(IdWorker.getId());
                                    detail.setCreateDate(LocalDateTime.now());
                                    details.add(errorDetail);
                                }
                            }
                            else {
                                //代码文件审核通过只有一条记录
                                detail.setCreateUser(sourceCodeReviewResult.getCreateUser());
                                detail.setId(IdWorker.getId());
                                detail.setCreateDate(LocalDateTime.now());
                                details.add(detail);
                            }
                        }
                        //调用用户接口,获取所有审查规则(根据用户提供的信息,大概200多个)
                        FilterAllRulesDTO filterAllRulesDTO = new FilterAllRulesDTO();
                        filterAllRulesDTO.setFilter(new RuleFilterDTO().setLanguage(Collections.emptyList()).setSelectStatus(Collections.emptyList()).setRuleType(Collections.emptyList()).setRuleSource(Collections.emptyList()).setDesc(""));
                        CodeReviewAllRuleBO codeReviewRuleServiceAllRules =sourceCodeReviewRuleService.getAllRules(filterAllRulesDTO);
                        List<CodeReviewRuleBO>  codeReviewRuleBOList = codeReviewRuleServiceAllRules.getRules();
                        //获取规则以 规则id 为key的 MAP
                        Map<String, CodeReviewRuleBO> codeReviewRuleBOMap = codeReviewRuleBOList.stream().collect(Collectors.toMap(CodeReviewRuleBO::getId, Function.identity(), (oldValue, newValue) -> newValue));
                        List<SourceCodeReviewResultDetailVO> sourceCodeReviewResultDetailVOList = details.stream().map(detail -> {
                            SourceCodeReviewResultDetailVO sourceCodeReviewResultDetailVO = objectMapper.convertValue(detail, SourceCodeReviewResultDetailVO.class);
                            if (Objects.nonNull(detail.getRuleCode()) && codeReviewRuleBOMap.containsKey(detail.getRuleCode())) {
                                sourceCodeReviewResultDetailVO.setRuleName(codeReviewRuleBOMap.get(detail.getRuleCode()).getDesc());
                                sourceCodeReviewResultDetailVO.setRuleType(codeReviewRuleBOMap.get(detail.getRuleCode()).getRuleType());
                                sourceCodeReviewResultDetailVO.setRuleSource(codeReviewRuleBOMap.get(detail.getRuleCode()).getRuleSource());
                                sourceCodeReviewResultDetailVO.setExplain(codeReviewRuleBOMap.get(detail.getRuleCode()).getExplain());
                            }
                            return sourceCodeReviewResultDetailVO;
                        }).collect(Collectors.toList());
                        codeReviewResultVO.setDetailVOList(sourceCodeReviewResultDetailVOList);

                        //2026.03.20根据用户要求,在查看审查详情后更新审查结果
                        if (codeReviewResultBO.getStatus().equals(ReviewStatusEnum.FINISHED.getValue())){
                            sourceCodeReviewResult.setFilesSize(codeReviewResultVO.getFilesSize());

                            sourceCodeReviewResult.setCheckPoints(codeReviewResultVO.getFilesSize());
                            sourceCodeReviewResult.setPassCheckPoints(codeReviewResultVO.getPassFileNum());
                            if (codeReviewResultVO.getFilesSize() > 0) {
                                sourceCodeReviewResult.setPassRate(new BigDecimal(codeReviewResultVO.getPassFileNum()).divide(new BigDecimal(codeReviewResultVO.getFilesSize()), 5, RoundingMode.HALF_UP));
                            }
                            else {
                                sourceCodeReviewResult.setPassRate(BigDecimal.ZERO);
                            }
                            sourceCodeReviewResult.setFilesLine(codeReviewResultVO.getFilesLine());
                            sourceCodeReviewResult.setUseRuleSize(codeReviewResultVO.getUseRuleSize());
                            sourceCodeReviewResult.setQuestions(codeReviewResultVO.getQuestions());
                            sourceCodeReviewResult.setDuration(codeReviewResultVO.getDuration());
                            this.updateById(sourceCodeReviewResult);
                        }

                        return codeReviewResultVO;
                    }
                }
            } else {
                log.error("根据版本获取审查结果详情失败,调用接口失败, Review ID: {}", sourceCodeFileVersionVO.getMinioId());
                throw new KnowException("逻辑审查错误,调用取审查结果详情接口失败");
            }
        }
        return codeReviewResultVO;
    }


    @SneakyThrows
    @Override
    public SourceCodeReviewResultVO getSourceCodeReviewResultVOById(Long id) {
        SourceCodeReviewResult sourceCodeReviewResult = this.getById(id);
        //根据用户要求,如果‘文件数    行数    审查规则数    问题数量’为空,则调用第三方接口获取一次获取最新数据
        if (Objects.nonNull(sourceCodeReviewResult) && ReviewStatusEnum.FINISHED.getValue().equals(sourceCodeReviewResult.getStatus()) && (sourceCodeReviewResult.getFilesSize() == null && sourceCodeReviewResult.getFilesLine() == null && sourceCodeReviewResult.getUseRuleSize() == null && sourceCodeReviewResult.getQuestions() == null)) {
            SourceCodeFileVersionService sourceCodeFileVersionService = SpringContextUtils.getInstanceByType(SourceCodeFileVersionService.class);
            SourceCodeFileVersionVO sourceCodeFileVersionVO = sourceCodeFileVersionService.getSourceCodeFileVersionVOByVersionId(sourceCodeReviewResult.getFileVersionId());
            //调用第三方接口获取审查结果
            if (Objects.nonNull(sourceCodeFileVersionVO) && StringUtils.isNotBlank(sourceCodeFileVersionVO.getMinioId())) {
                ReviewSummaryResultBO codeReviewSummaryResultBO = getReviewSummary(sourceCodeFileVersionVO.getMinioId());
                if (Objects.nonNull(codeReviewSummaryResultBO)) {
                    sourceCodeReviewResult.setFilesSize(codeReviewSummaryResultBO.getFilesSize());
                    sourceCodeReviewResult.setFilesLine(codeReviewSummaryResultBO.getFilesLine());
                    sourceCodeReviewResult.setUseRuleSize(codeReviewSummaryResultBO.getUseRuleSize());
                    sourceCodeReviewResult.setQuestions(codeReviewSummaryResultBO.getQuestions());
                    sourceCodeReviewResult= this.updateByIDAndReturnObject(sourceCodeReviewResult);
                }
            }
        }
        return convertToVO(sourceCodeReviewResult);
    }

    @SneakyThrows
    @Override
    public SourceCodeReviewProcessVO getSourceCodeReviewProcessResultVOById(Long id) {
        // 获取reviewId
        SourceCodeReviewResult sourceCodeReviewResult = this.getById(id);
        SourceCodeFileVersionService sourceCodeFileVersionService = SpringContextUtils.getInstanceByType(SourceCodeFileVersionService.class);
        SourceCodeFileVersionVO sourceCodeFileVersionVO = sourceCodeFileVersionService.getSourceCodeFileVersionVOByVersionId(sourceCodeReviewResult.getFileVersionId());
        return getReviewProcess(sourceCodeFileVersionVO.getMinioId());
    }

    @SneakyThrows
    private SourceCodeReviewProcessVO getReviewProcess(String reviewId) {
        try {
            // 调用第三方接口获取审查进度
            log.info("获取审查进度 ID:{}", reviewId);
            ResponseDataBO<Object>  responseWrapper = codeReviewClient.getReviewProcess(reviewId,HttpRequestUtil.getDebugId(),HttpRequestUtil.getLoginName());
            if (responseWrapper.getCode().equals(200)) { // 第三方接口调用成功
                //将结果转换为BO
                SourceCodeReviewProcessVO sourceCodeReviewProcessVO = objectMapper.convertValue(responseWrapper.getData(), SourceCodeReviewProcessVO.class);
                return sourceCodeReviewProcessVO;
            }
            else { // 第三方接口调用失败, 停止代码审查失败
                log.error("获取审查进度,调用接口失败: {}", responseWrapper.getMessage());
                throw new KnowException(responseWrapper.getMessage());
            }
        } catch (Exception e) {
            log.error("获取审查进度出错", e);
            throw new KnowException(e.getMessage(), e);
        }
    }




    @Override
    public IPage<SourceCodeReviewResultVO> getSourceCodeReviewResultVOPage(Page<SourceCodeReviewResult> page, Long fileId) {
        IPage<SourceCodeReviewResult> sourceCodeReviewResultPage = this.page(page,
                Wrappers.<SourceCodeReviewResult>lambdaQuery()
                        .eq(fileId != null, SourceCodeReviewResult::getFileId, fileId)
        );
        return sourceCodeReviewResultPage.convert(this::convertToVO);
    }

    private Long submitSourceCodeReview(SourceCodeReviewRequestVO sourceCodeReviewRequestVO, Long userId) {
        log.info("提交逻辑审查，文件ID: {}", sourceCodeReviewRequestVO.getFileVersionId());
        // 获取文件
        SourceCodeFileVersionService sourceCodeFileVersionService = SpringContextUtils.getInstanceByType(SourceCodeFileVersionService.class);
        SourceCodeFileVersionVO sourceCodeFileVersionVO = sourceCodeFileVersionService.getSourceCodeFileVersionVOByVersionId(sourceCodeReviewRequestVO.getFileVersionId());
        if (Objects.nonNull(sourceCodeFileVersionVO)) {
            SourceCodeReviewResult sourceCodeReviewResult = new SourceCodeReviewResult()
                    .setFileId(sourceCodeFileVersionVO.getFileId())
                    .setFileVersionId(sourceCodeFileVersionVO.getId())
                    .setReviewTime(LocalDateTime.now())
                    .setStatus(ReviewStatusEnum.IN_PROGRESS);
            sourceCodeReviewResult.setId(IdWorker.getId());
            sourceCodeReviewResult.setCreateUser(userId);
            sourceCodeReviewResult.setCreateDate(LocalDateTime.now());
            this.save(sourceCodeReviewResult);
            SourceCodeReviewResult saved = this.getById(sourceCodeReviewResult.getId());

            try {
                startReview(saved.getId(), sourceCodeFileVersionVO.getMinioId(), userId);
                return saved.getId();
            } catch (Exception e) {
                log.error("文件上传到第三方服务失败", e);
                // 上传失败，更新状态并抛出异常
                SourceCodeReviewResult update = new SourceCodeReviewResult();
                update.setId(saved.getId());
                update.setStatus(ReviewStatusEnum.FAILED);
                update.setErrorMessage(e.getMessage());
                update.setUpdateUser(userId);
                update.setUpdateDate(LocalDateTime.now());
                this.updateById(update);
                throw new KnowException("文件上传到第三方服务失败: " + e.getMessage());
            }
        } else {
            log.error("逻辑文件版本不存在: {}", sourceCodeReviewRequestVO.getFileVersionId());
            throw new KnowException("逻辑文件版本不存在");
        }
    }

    @Override
    public Long submitSourceCodeReview(SourceCodeReviewRequestVO sourceCodeReviewRequestVO) {
        return submitSourceCodeReview(sourceCodeReviewRequestVO, HttpRequestUtil.getUserId());
    }

    @Override
    public void stopSourceCodeReview(SourceCodeReviewStopVO sourceCodeReviewStopVO){
        log.info("停止逻辑审查，结果ID: {}", sourceCodeReviewStopVO.getResultId());
        // 获取审查结果
        SourceCodeReviewResult sourceCodeReviewResult = this.getById(sourceCodeReviewStopVO.getResultId());
        SourceCodeFileVersionService sourceCodeFileVersionService = SpringContextUtils.getInstanceByType(SourceCodeFileVersionService.class);
        SourceCodeFileVersionVO sourceCodeFileVersionVO = sourceCodeFileVersionService.getSourceCodeFileVersionVOByVersionId(sourceCodeReviewResult.getFileVersionId());
        stopReview(sourceCodeReviewResult.getId(), sourceCodeFileVersionVO.getMinioId(), HttpRequestUtil.getUserId());
    }

    @Override
    public  SourceCodeReviewWaitTaskVO getWaitTask(Long id){
        log.info("获取需要等待的任务数，结果ID: {}", id);
        SourceCodeReviewResult sourceCodeReviewResult = this.getById(id);
        SourceCodeFileVersionService sourceCodeFileVersionService = SpringContextUtils.getInstanceByType(SourceCodeFileVersionService.class);
        SourceCodeFileVersionVO sourceCodeFileVersionVO = sourceCodeFileVersionService.getSourceCodeFileVersionVOByVersionId(sourceCodeReviewResult.getFileVersionId());
        try {
            //调用三方接口
            ResponseDataBO<Object>  responseWrapper = codeReviewClient.getWaitTask(sourceCodeFileVersionVO.getMinioId(),HttpRequestUtil.getDebugId(),HttpRequestUtil.getLoginName());
            if (responseWrapper.getCode().equals(200)) { // 第三方接口调用失败, 更新状态审查结果为失败
                //将结果转换为BO
                SourceCodeReviewWaitTaskVO sourceCodeReviewWaitTaskVO = objectMapper.convertValue(responseWrapper.getData(), SourceCodeReviewWaitTaskVO.class);
                return sourceCodeReviewWaitTaskVO;
            }
            else { // 第三方接口调用失败, 停止代码审查失败
                log.error("获取需要等待的任务数,调用接口失败: {}", responseWrapper.getMessage());
                throw new KnowException(responseWrapper.getMessage());
            }
        } catch (Exception e) {
            log.error("获取需要等待的任务数失败", e);
            throw new KnowException(e.getMessage(), e);
        }
    }




    @Override
    public List<SourceCodeReviewRuleSummaryItemVO> getSourceCodeReviewRuleSummary(Long id) {
        SourceCodeReviewResult sourceCodeReviewResult = this.getById(id);
        if (sourceCodeReviewResult != null) {
            List<SourceCodeReviewRuleSummaryItemVO> summaryItemVOList = new ArrayList<>();
            List<SourceCodeReviewResultDetailVO> detailVOList = sourceCodeReviewResultDetailService.getAllDetailsByResultId(id);
            List<SourceCodeReviewRuleVO> sourceCodeReviewRuleVOList = sourceCodeReviewRuleService.getSourceCodeReviewRuleVOList();
            sourceCodeReviewRuleVOList.forEach(sourceCodeReviewRuleVO -> {
                SourceCodeReviewRuleSummaryItemVO itemVO = new SourceCodeReviewRuleSummaryItemVO();
                itemVO.setRule(sourceCodeReviewRuleVO);
                //获取审查详情中使用了“当前规则”的记录列表
                List<SourceCodeReviewResultDetailVO> currentRuledetailVOList = detailVOList.stream().filter(detailVO -> detailVO.getRuleId().equals(sourceCodeReviewRuleVO.getId())).toList();
                Optional<SourceCodeReviewResultDetailVO> findDetailVOOptional = currentRuledetailVOList.stream().filter(detailVO -> detailVO.getIsPassed().equals(0)).findFirst();
                if (CollectionUtils.isNotEmpty(currentRuledetailVOList)) { //只有通过或不通过的规则才添加进列表,返回给前端,不适用的规则(审查详情中不包含)不返回给前端
                    if (findDetailVOOptional.isPresent()) { //只要存在未通过规则，则该规则的汇总为未通过
                        itemVO.setSummary(CodeReviewRuleSummaryEnum.NOT_PASSED);
                    } else {
                        itemVO.setSummary(CodeReviewRuleSummaryEnum.PASSED);
                    }
                    summaryItemVOList.add(itemVO);
                }
            });
            return summaryItemVOList;
        } else {
            throw new KnowException("不存在该逻辑审查记录");
        }
    }




    @Override
    public SourceCodeReviewResultFilterVO getSourceCodeReviewResultFilters(Long id, Integer isPassed) {
        SourceCodeReviewResultFilterVO filterVO = new SourceCodeReviewResultFilterVO();
        List<String> sourceFileNamefilters = sourceCodeReviewResultDetailService.getSourceCodeReviewResultFilters(id, isPassed);
        List<String> ruleFilters = sourceCodeReviewResultDetailService.getCircuitReviewResultRuleFilters(id, isPassed);
        //将filters中的null值替换为空格
        sourceFileNamefilters = sourceFileNamefilters.stream().map(filter -> StringUtils.isBlank(filter) ? " " : filter).distinct().collect(Collectors.toList());
        filterVO.setSourceFileNameFilters(sourceFileNamefilters);
        filterVO.setReviewRuleFilters(ruleFilters);
        return filterVO;
    }

    @Override
    public List<SourceCodeReviewResult> getResultByFileIdList(List<Long> fileVersionId) {
        if (CollectionUtils.isNotEmpty(fileVersionId)) {
            return this.lambdaQuery().in(SourceCodeReviewResult::getFileVersionId, fileVersionId).eq(SourceCodeReviewResult::getStatus, ReviewStatusEnum.FINISHED).orderByDesc(SourceCodeReviewResult::getReviewTime).list();
        } else {
            return List.of();
        }
    }

    @Override
    public List<SourceCodeReviewResultVO> getSourceCodeFileVersionResults(Long versionId, Boolean includeAllStatus) {
        List<SourceCodeReviewResult> reviewResults = this.lambdaQuery().eq(SourceCodeReviewResult::getFileVersionId, versionId).eq(!includeAllStatus, SourceCodeReviewResult::getStatus, ReviewStatusEnum.FINISHED).orderByDesc(SourceCodeReviewResult::getReviewTime).list();
        return reviewResults.stream().map(this::convertToVO).toList();
    }

    @Transactional
    public void startReview(Long resultId, String reviewId, Long userId) {
        try {
            // 1. 调用第三方接口
            log.info("提交逻辑审查 ID:{}", reviewId);
            ResponseDataBO<Object>  responseWrapper = codeReviewClient.review(reviewId,HttpRequestUtil.getDebugId(),HttpRequestUtil.getLoginName());
            if (!responseWrapper.getCode().equals(200)) { // 第三方接口调用失败, 更新状态审查结果为失败
                SourceCodeReviewResult update = new SourceCodeReviewResult();
                update.setId(resultId);
                update.setStatus(ReviewStatusEnum.FAILED);
                update.setErrorMessage(responseWrapper.getMessage());
                update.setUpdateUser(userId);
                update.setUpdateDate(LocalDateTime.now());
                this.updateById(update);
                log.error("提交逻辑审查失败,调用接口失败: {}", responseWrapper.getMessage());
            }
        } catch (Exception e) {
            SourceCodeReviewResult update = new SourceCodeReviewResult();
            update.setId(resultId);
            update.setStatus(ReviewStatusEnum.FAILED);
            update.setErrorMessage(e.getMessage());
            update.setUpdateUser(userId);
            update.setUpdateDate(LocalDateTime.now());
            this.updateById(update);
            log.error("提交逻辑审查失败", e);
            throw new KnowException(e.getMessage(), e);
        }
    }


    @SneakyThrows
    public void stopReview(Long resultId, String reviewId, Long userId) {
        try {
            // 调用第三方接口停止代码
            log.info("停止代码审查 ID:{}", reviewId);
            ResponseDataBO<Object>  responseWrapper = codeReviewClient.stopReview(reviewId,HttpRequestUtil.getDebugId(),HttpRequestUtil.getLoginName());
            if (responseWrapper.getCode().equals(200)) { // 第三方接口调用成功, 更新状态审查结果为失败
                //将结果转换为BO
                StopReviewResultBO stopReviewResultBO = objectMapper.convertValue(responseWrapper.getData(), StopReviewResultBO.class);
                if (stopReviewResultBO.getStopStatus().equals(1)) { // 停止成功
                    SourceCodeReviewResult update = new SourceCodeReviewResult();
                    update.setId(resultId);
                    update.setStatus(ReviewStatusEnum.FAILED);
                    update.setErrorMessage(responseWrapper.getMessage());
                    update.setUpdateUser(userId);
                    update.setUpdateDate(LocalDateTime.now());
                    this.updateById(update);
                    log.info("停止代码审查成功");
                } else { // 停止失败
                    throw new KnowException("停止代码审查,调用接口失败!");
                }
            }
            else { // 第三方接口调用失败, 停止代码审查失败
                log.error("停止代码审查,调用接口失败: {}", responseWrapper.getMessage());
                throw new KnowException(responseWrapper.getMessage());
            }

        } catch (Exception e) {
            log.error("停止代码审查出错", e);
            throw new KnowException(e.getMessage(), e);
        }
    }



    @Override
    @SneakyThrows
    public void getAllCodeReviewResultAndUpdate() {
        //获取所有状态为ReviewStatusEnum.IN_PROGRESS和ReviewStatusEnum.ERROR的审查结果
        List<SourceCodeReviewResult> inProgressResults = this.lambdaQuery().eq(SourceCodeReviewResult::getStatus, ReviewStatusEnum.IN_PROGRESS).or().eq(SourceCodeReviewResult::getStatus, ReviewStatusEnum.ERROR).list();
        log.info("有{}个任务需要获取状态", inProgressResults.size());
        for (SourceCodeReviewResult inProgressResult : inProgressResults) {
            try {
                //获取源代码文件版本id, 获取源代码文件版本
                SourceCodeFileVersionService sourceCodeFileVersionService = SpringContextUtils.getInstanceByType(SourceCodeFileVersionService.class);
                Long fileVersionId = inProgressResult.getFileVersionId();
                SourceCodeFileVersion sourceCodeFileVersion = sourceCodeFileVersionService.getById(fileVersionId);
                String reviewId = Optional.ofNullable(sourceCodeFileVersion).map(SourceCodeFileVersion::getMinioId).orElseThrow(() -> new KnowException("源代码文件版本不存在"));
                String loginName = DEFAULT_USER_NAME;
                if (Objects.nonNull(sourceCodeFileVersion) && Objects.nonNull(sourceCodeFileVersion.getCreateUser())){
                    try {//确保这里异常不影响正常流程执行
                        ResponseWrapper<UserDTO> userDTOResponseWrapper = userServiceClient.getUserById(sourceCodeFileVersion.getCreateUser());
                        if(!userDTOResponseWrapper.isSucc()) {
                            log.error("获取当前用户信息失败: {} 错误信息: {}", sourceCodeFileVersion.getCreateUser(),  userDTOResponseWrapper.getMsg());
                        }
                        loginName = userDTOResponseWrapper.getContent().getLoginName();
                    }
                    catch (Exception e) {
                        log.error("获取当前用户信息出错: {} 错误信息: {}", sourceCodeFileVersion.getCreateUser(),  e.getMessage());
                    }
                }
                log.info("定时任务,开始获取审查结果, ID: {}", reviewId);
                Map<String, Object> resultMap = getStringObjectMap(reviewId, Constant.POLLING_FOR_JOB_PROGRESS, loginName, null);
                if (resultMap.containsKey("code") && resultMap.get("code").equals(200)) {
                    log.info("定时任务,获取审查结果成功, ID: {}", reviewId);
                    //将resultMap中的Map结构的data属性转换成CodeReviewResultBO
                    CodeReviewResultBO codeReviewResultBO = objectMapper.convertValue(resultMap.get("data"), CodeReviewResultBO.class);
                    //判断codeReviewResultBO中是否有status为2 (表示审校中完成)
                    if (Objects.nonNull(codeReviewResultBO) && Objects.nonNull(codeReviewResultBO.getStatus())) {
                        //当前审查结果已经完成,更新状态为完成
                        log.info("Review ID为: {} 的代码审查结果 status: {} ", reviewId, codeReviewResultBO.getStatus());
                        List<CodeReviewFileResultBO> unProcessedFiles = filterProcessedFiles(inProgressResult, codeReviewResultBO.getFilesResult());
                        //当前时间和审查开始时间间隔大于1小时,认为审查失败
                        if ((codeReviewResultBO.getStatus().equals(ReviewStatusEnum.FAILED.getValue())) || (codeReviewResultBO.getStatus().equals(ReviewStatusEnum.ERROR.getValue()) && Duration.between(inProgressResult.getReviewTime(), LocalDateTime.now()).toHours() > 1)) {
                            //审查失败处理
                            SourceCodeReviewResult update = new SourceCodeReviewResult();
                            update.setId(inProgressResult.getId());
                            update.setStatus(ReviewStatusEnum.FAILED);
                            update.setErrorMessage(null);
                            update.setUpdateDate(LocalDateTime.now());
                            this.updateById(update);
                        } else {
                            processResult(inProgressResult, unProcessedFiles, codeReviewResultBO.getStatus().equals(2), codeReviewResultBO.getDuration());
                            //如果当前审查完成,再调用getReviewSummary获取汇总结果
                            if (codeReviewResultBO.getStatus().equals(ReviewStatusEnum.FINISHED.getValue())) {
                                SourceCodeReviewResult update = new SourceCodeReviewResult();
                                update.setId(inProgressResult.getId());
                                //2026.3.21把之前详情处理的移到这里
                                update.setStatus(ReviewStatusEnum.FINISHED);
                                update.setCheckPoints(codeReviewResultBO.getFilesSize());
                                update.setPassCheckPoints(codeReviewResultBO.getPassFileNum());

                                if (codeReviewResultBO.getFilesSize() > 0) {
                                    update.setPassRate(new BigDecimal(codeReviewResultBO.getPassFileNum()).divide(new BigDecimal(codeReviewResultBO.getFilesSize()), 5, RoundingMode.HALF_UP));
                                }
                                else {
                                    update.setPassRate(BigDecimal.ZERO);
                                }

                                update.setErrorMessage(null);
                                update.setIsClosedLoop(codeReviewResultBO.getFilesSize().equals(codeReviewResultBO.getPassFileNum())?1:0);

                                update.setUseRuleSize(codeReviewResultBO.getUseRuleSize());
                                update.setQuestions(codeReviewResultBO.getQuestions());
                                update.setFilesSize(codeReviewResultBO.getFilesSize());
                                update.setFilesLine(codeReviewResultBO.getFilesLine());
                                this.updateByIDAndReturnObject(update);

                                /**ReviewSummaryResultBO codeReviewSummaryResultBO = getReviewSummary(reviewId);
                                if (Objects.nonNull(codeReviewSummaryResultBO)) {//获取汇总结果成功,更新到数据库
                                    SourceCodeReviewResult update = new SourceCodeReviewResult();
                                    update.setId(inProgressResult.getId());
                                    update.setCheckPoints(codeReviewSummaryResultBO.getFilesSize());
                                    update.setUseRuleSize(codeReviewSummaryResultBO.getUseRuleSize());
                                    update.setQuestions(codeReviewSummaryResultBO.getQuestions());
                                    update.setFilesSize(codeReviewSummaryResultBO.getFilesSize());
                                    update.setFilesLine(codeReviewSummaryResultBO.getFilesLine());
                                    this.updateByIDAndReturnObject(update);
                                }*/
                            }
                        }
                    } else {
                        log.info("ID为: {} 的代码审查结果不完整, ID: {}", reviewId);
                    }
                } else {
                    log.info("定时任务,获取审查结果失败, ID: {}", reviewId);
                    SourceCodeReviewResult update = new SourceCodeReviewResult();
                    update.setId(inProgressResult.getId());
                    if (Duration.between(inProgressResult.getReviewTime(), LocalDateTime.now()).toHours() > 1) {
                        update.setStatus(ReviewStatusEnum.FAILED);
                    } else {
                        update.setStatus(ReviewStatusEnum.ERROR);
                    }
                    update.setErrorMessage(resultMap.get("message").toString());
                    update.setUpdateDate(LocalDateTime.now());
                    this.updateById(update);
                    log.error("代码审查错误, 调用接口失败 Review ID: {}", reviewId);
                }
            } catch (Exception e) {
                log.error("定时任务,获取审查结果失败", e);
            }
        }
    }

    private Map<String, Object> getStringObjectMap(String reviewId, String debugId, String loginName, String version) throws JsonProcessingException {
        //调用第三方接口获取审查结果
        Map<String, Object> resultMap = new HashMap<>();
        try {
            String jsonString = codeReviewClient.getResult(reviewId, debugId, loginName, version);
            log.info("获取代码审查结果成功, ID: {}, 结果:{}", reviewId, jsonString);
            resultMap = objectMapper.readValue(jsonString, Map.class);
        } catch (FeignException e) {
            log.error("获取代码审查错误,FeignException: {}", e.getMessage(), e);
            if (e.responseBody().isPresent()) {
                String body = StandardCharsets.UTF_8.decode(e.responseBody().get()).toString();
                resultMap = objectMapper.readValue(body, Map.class);
            }
            else {
                resultMap.put("code", 500);
                resultMap.put("message", e.getMessage());
            }
        } catch (Exception e) {
            log.error("获取代码审查错误,Exception: {}", e.getMessage(), e);
            resultMap.put("code", 500);
            resultMap.put("message", e.getMessage());
        }
        return resultMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteSourceCodeReviewResult(Long id) {
        try {
            this.removeById(id);
            this.sourceCodeReviewResultDetailService.removeDetailByResultIdList(List.of(id));
            return true;
        } catch (Exception e) {
            log.error("删除代码审查结果失败: {}", id, e);
            return false;
        }
    }

    @SneakyThrows
    private List<CodeReviewFileResultBO>  filterProcessedFiles(SourceCodeReviewResult sourceCodeReviewResult, List<CodeReviewFileResultBO> codeReviewFileResultBOList) {
        List<CodeReviewFileResultBO> unProcessedFiles = new ArrayList<>();
        //TODO 调试完成后删除 List<SourceCodeReviewResultDetailRuleBO> existDetails =sourceCodeReviewResultDetailService.getAllDetailsAndRuleByResultId(sourceCodeReviewResult.getId());
        //现在代码审查规则不再缓存, 审查结果详情保存了规则编号(用户的编号),所以直接从审查结果详情获取即可
        List<SourceCodeReviewResultDetail> existDetails = sourceCodeReviewResultDetailService.list(Wrappers.<SourceCodeReviewResultDetail>lambdaQuery().eq(SourceCodeReviewResultDetail::getResultId, sourceCodeReviewResult.getId()));
        for (CodeReviewFileResultBO codeReviewFileResultBO : codeReviewFileResultBOList) {
            if (!codeReviewFileResultBO.getStatus().equals(3)) { // 状态为3(正在审核)的忽略
                //existDetails中有存在sourceFileName和codeReviewFileResultBO的sourceFileName相同的记录忽略当前codeReviewFileResultBO否则加如到unProcessedFiles
                if (!existDetails.stream().anyMatch(detail ->  Objects.nonNull(detail.getRuleCode()) && detail.getRuleCode().equals(codeReviewFileResultBO.getRuleId()) && detail.getSourceFileName().equals(codeReviewFileResultBO.getFileName()))) {
                    unProcessedFiles.add(codeReviewFileResultBO);
                }
            }
        }
        return unProcessedFiles;
    }


    @SneakyThrows
    private synchronized void processResult(SourceCodeReviewResult sourceCodeReviewResult,  List<CodeReviewFileResultBO> codeReviewFileResultBOList, boolean isFinished, Integer duration) {
        List<SourceCodeReviewResultDetail> details = new ArrayList<>();
        //TODO 现在代码审查规则不再缓存,调试完成后删除List<SourceCodeReviewResultDetailRuleBO> existDetails =sourceCodeReviewResultDetailService.getAllDetailsAndRuleByResultId(sourceCodeReviewResult.getId());
        //现在代码审查规则不再缓存, 审查结果详情保存了规则编号(用户的编号),所以直接从审查结果详情获取即可
        List<SourceCodeReviewResultDetail> existDetails = sourceCodeReviewResultDetailService.list(Wrappers.<SourceCodeReviewResultDetail>lambdaQuery().eq(SourceCodeReviewResultDetail::getResultId, sourceCodeReviewResult.getId()));

        //Boolean isPassed = true;
        //Integer passCheckPoints = 0;
        //遍历codeReviewFileResultBOList,将数据保存到details
        for (CodeReviewFileResultBO codeReviewFileResultBO : codeReviewFileResultBOList) {
            if (!existDetails.stream().anyMatch(detail ->  Objects.nonNull(detail.getRuleCode()) && detail.getRuleCode().equals(codeReviewFileResultBO.getRuleId()) && detail.getSourceFileName().equals(codeReviewFileResultBO.getFileName()))) {
                SourceCodeReviewResultDetail detail = new SourceCodeReviewResultDetail();
                detail.setResultId(sourceCodeReviewResult.getId());
                detail.setRuleCode(codeReviewFileResultBO.getRuleId());
                detail.setSourceFileName(codeReviewFileResultBO.getFileName());
                detail.setLanguage(codeReviewFileResultBO.getLanguage());
                detail.setIsPassed(codeReviewFileResultBO.getStatus().equals(1) ? 1 : 0);

                if (CollectionUtils.isNotEmpty(codeReviewFileResultBO.getCodes())){
                    //isPassed = false;
                    //遍历codeReviewFileResultBO.getCodes()将数据保存到details中
                    for (CodeReviewCodeResultBO code : codeReviewFileResultBO.getCodes()) {
                        SourceCodeReviewResultDetail errorDetail = new SourceCodeReviewResultDetail();
                        errorDetail.setResultId(detail.getResultId());
                        errorDetail.setRuleCode(detail.getRuleCode());
                        errorDetail.setSourceFileName(detail.getSourceFileName());
                        errorDetail.setLanguage(detail.getLanguage());
                        errorDetail.setIsPassed(detail.getIsPassed());
                        errorDetail.setErrorCode(code.getCode());
                        errorDetail.setLineNumber(code.getLineNumber());
                        errorDetail.setErrorReason(code.getErrorReason());
                        errorDetail.setReviewSuggestion(code.getModifySuggest());
                        errorDetail.setQuestionId(code.getQuestionId());
                        errorDetail.setQuestionDesc(code.getQuestionDesc());
                        errorDetail.setRecheckConclusion(code.getRecheckConclusion());
                        errorDetail.setRecheckStatus(code.getRecheckStatus());
                        errorDetail.setRecheckResultStatus(code.getRecheckResultStatus());
                        errorDetail.setRejectReason(code.getRejectReason());
                        errorDetail.setRecheckUserId(code.getRecheckUserId());
                        detail.setCreateUser(sourceCodeReviewResult.getCreateUser());
                        detail.setId(IdWorker.getId());
                        detail.setCreateDate(LocalDateTime.now());
                        details.add(errorDetail);
                    }
                }
                else {
                    //代码文件审核通过只有一条记录
                    detail.setCreateUser(sourceCodeReviewResult.getCreateUser());
                    detail.setId(IdWorker.getId());
                    detail.setCreateDate(LocalDateTime.now());
                    details.add(detail);
                    //passCheckPoints ++;
                }
            }
        }
        sourceCodeReviewResultDetailService.saveBatch(details);
        /** 2026.3.21 改为审查完成后,获取审查结果,计算通过率,所以把代码注释掉
        //根据审查详情中的记录,更新主审查结果,注:根据用户要求,这里不计算审查点,而计算审查文件数,现在数据库中的审查点保存了审查文件
        List<SourceCodeReviewResultDetail> checkedfileNameList= sourceCodeReviewResultDetailService.list(Wrappers.<SourceCodeReviewResultDetail>lambdaQuery().eq(SourceCodeReviewResultDetail::getResultId, sourceCodeReviewResult.getId()).select(SourceCodeReviewResultDetail::getSourceFileName, SourceCodeReviewResultDetail::getRuleId, SourceCodeReviewResultDetail::getIsPassed));
        Long checkFiles = checkedfileNameList.stream().map(SourceCodeReviewResultDetail::getSourceFileName).distinct().count();
        Long unPassCheckFiles = checkedfileNameList.stream().filter(detail -> detail.getIsPassed() == 0).map(SourceCodeReviewResultDetail::getSourceFileName).distinct().count();

        //Long checkRules = checkedfileNameList.stream().map(SourceCodeReviewResultDetail::getRuleId).distinct().count();
        //Long unPassCheckRules = checkedfileNameList.stream().filter(detail -> detail.getIsPassed() == 0).map(SourceCodeReviewResultDetail::getRuleId).distinct().count();

        if (isFinished) { //审查完成后如果unPassCheckPoints为0，则认为是闭环
            sourceCodeReviewResult.setStatus(ReviewStatusEnum.FINISHED);
            sourceCodeReviewResult.setErrorMessage(null);
            sourceCodeReviewResult.setDuration(duration);
            sourceCodeReviewResult.setIsClosedLoop(unPassCheckFiles.equals(0L)?1:0);
        }else {
            sourceCodeReviewResult.setIsClosedLoop(0);
        }
        sourceCodeReviewResult.setCheckPoints(checkFiles.intValue());
        sourceCodeReviewResult.setPassCheckPoints(checkFiles.intValue() - unPassCheckFiles.intValue());
        //按照用户要求改为:  通过率 = 通过文件数/总数文件数
        if (checkFiles.intValue() > 0) {
            sourceCodeReviewResult.setPassRate(new BigDecimal(checkFiles.intValue() - unPassCheckFiles.intValue()).divide(new BigDecimal(checkFiles.intValue()), 5, RoundingMode.HALF_UP));
        }
        else {
            sourceCodeReviewResult.setPassRate(BigDecimal.ZERO);
        }
        sourceCodeReviewResult.setVersion(this.getById(sourceCodeReviewResult.getId()).getVersion());;
        this.updateByIDAndReturnObject(sourceCodeReviewResult);*/
    }


    /**
     * 调用三方结果,获取审查结果汇总信息
     *
     * @param reviewId
     * @return
     */
    private ReviewSummaryResultBO getReviewSummary(String reviewId) {
        try {
            //调用第三方接口,查询上传文件的相关属性
            ResponseDataBO<Object> responseWrapper = codeReviewClient.getReviewSummary(new ReviewSummaryBO().setReviewId(reviewId), HttpRequestUtil.getDebugId(), HttpRequestUtil.getLoginName());
            if (!responseWrapper.getCode().equals(200)) { // 第三方接口调用失败, 更新状态审查结果为失败
                log.error("查询上传文件的相关属性,调用接口失败: {}", responseWrapper.getMessage());
                throw new KnowException(responseWrapper.getMessage());
            } else {
                log.info("查询上传文件的相关属性成功");
                ReviewSummaryResultBO reviewSummaryResultBO = objectMapper.convertValue(responseWrapper.getData(), ReviewSummaryResultBO.class);
                return reviewSummaryResultBO;
            }
        } catch (Exception e) {
            log.error("查询上传文件的相关属性失败", e);
            throw new KnowException(e.getMessage(), e);
        }
    }

    @SneakyThrows
    @Override
    public SourceCodeContentVO getSourceCodeContent(Long resultId, String fileName, String offset, String version) {
        // 获取reviewId
        SourceCodeReviewResult sourceCodeReviewResult = this.getById(resultId);
        SourceCodeFileVersionService sourceCodeFileVersionService = SpringContextUtils.getInstanceByType(SourceCodeFileVersionService.class);
        SourceCodeFileVersionVO sourceCodeFileVersionVO = sourceCodeFileVersionService.getSourceCodeFileVersionVOByVersionId(sourceCodeReviewResult.getFileVersionId());
        return getSourceCode(sourceCodeFileVersionVO.getMinioId(), fileName, offset, version);
    }

    private SourceCodeContentVO getSourceCode(String  reviewId, String fileName, String offset, String version) {
        try {
            //调用第三方接口,获取文件源代码
            ResponseDataBO<Object> responseWrapper = codeReviewClient.getSourceCode(reviewId, HttpRequestUtil.getDebugId(), HttpRequestUtil.getLoginName(), fileName,offset, version);
            if (!responseWrapper.getCode().equals(200)) { // 第三方接口调用失败, 更新状态审查结果为失败
                log.error("获取文件源代码,调用接口失败: {}", responseWrapper.getMessage());
                throw new KnowException(responseWrapper.getMessage());
            } else {
                log.info("获取文件源代码成功");
                SourceCodeContentVO sourceCodeContentVO = objectMapper.convertValue(responseWrapper.getData(), SourceCodeContentVO.class);
                return sourceCodeContentVO;
            }
        } catch (Exception e) {
            log.error("获取文件源代码失败", e);
            throw new KnowException(e.getMessage(), e);
        }
    }

    @SneakyThrows
    @Override
    public List<String> getVersionByResultId(Long id) {
        //按照用户给定的新方案,这里需要根据审查结果ID,通过用户提供的API获取代码版本列表
        SourceCodeReviewResultService sourceCodeReviewResultService = SpringContextUtils.getInstanceByType(SourceCodeReviewResultService.class);
        SourceCodeReviewResult sourceCodeReviewResult = sourceCodeReviewResultService.getById(id);
        SourceCodeFileVersionService sourceCodeFileVersionService = SpringContextUtils.getInstanceByType(SourceCodeFileVersionService.class);
        SourceCodeFileVersionVO sourceCodeFileVersionVO = sourceCodeFileVersionService.getSourceCodeFileVersionVOByVersionId(sourceCodeReviewResult.getFileVersionId());
        //调用第三方接口获取审查结果;
        //注: 虽然用户后端是多个版本, 但是在当前系统中仍然按照一个版本对待
        if (Objects.nonNull(sourceCodeFileVersionVO) && StringUtils.isNotBlank(sourceCodeFileVersionVO.getMinioId())) {
            return getVersionList(sourceCodeFileVersionVO.getMinioId());
        }
        else{
            return List.of();
        }
    }

    @SneakyThrows
    private List<String> getVersionList(String reviewId) {
        try {
            // 调用第三方接口获取代码版本列表
            log.info("获取代码版本列表 ID:{}", reviewId);
            ResponseDataBO<Object>  responseWrapper = codeReviewClient.getVersionList(reviewId,HttpRequestUtil.getDebugId(),HttpRequestUtil.getLoginName());
            if (responseWrapper.getCode().equals(200)) { // 第三方接口调用成功
                //将结果转换为BO
                ResultVersionListBO resultVersionListBO = objectMapper.convertValue(responseWrapper.getData(), ResultVersionListBO.class);
                return resultVersionListBO.getVersionList();
            }
            else { // 第三方接口调用失败
                log.error("获取代码版本列表,调用接口失败: {}", responseWrapper.getMessage());
                throw new KnowException(responseWrapper.getMessage());
            }
        } catch (Exception e) {
            log.error("获取代码版本列表出错", e);
            throw new KnowException(e.getMessage(), e);
        }
    }

    /**
     * 将实体转换为VO
     */
    private SourceCodeReviewResultVO convertToVO(SourceCodeReviewResult sourceCodeReviewResult) {
        if (sourceCodeReviewResult == null) {
            return null;
        }
        return objectMapper.convertValue(sourceCodeReviewResult, SourceCodeReviewResultVO.class);
    }
}
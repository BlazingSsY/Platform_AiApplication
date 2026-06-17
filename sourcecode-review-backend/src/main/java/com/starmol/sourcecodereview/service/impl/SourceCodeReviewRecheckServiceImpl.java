package com.starmol.sourcecodereview.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starmol.sourcecodereview.bean.bo.CodeReCheckFileResultBO;
import com.starmol.sourcecodereview.bean.bo.CodeRecheckCodeResultBO;
import com.starmol.sourcecodereview.bean.bo.CodeRecheckResultBO;
import com.starmol.sourcecodereview.bean.bo.CodeReviewRecheckBO;
import com.starmol.sourcecodereview.bean.bo.CodeReviewRecheckRecoderBO;
import com.starmol.sourcecodereview.bean.bo.CodeReviewRecheckListRequestBO;
import com.starmol.sourcecodereview.bean.bo.CodeReviewRecheckResultBO;
import com.starmol.sourcecodereview.bean.bo.CodeReviewRecheckResultSubmitBO;
import com.starmol.sourcecodereview.bean.bo.ResponseDataBO;
import com.starmol.sourcecodereview.bean.dto.CodeReviewRecheckResultSubmitDTO;
import com.starmol.sourcecodereview.bean.dto.SourceCodeReviewRecheckDTO;
import com.starmol.sourcecodereview.bean.dto.UserDTO;
import com.starmol.sourcecodereview.bean.vo.CodeRecheckDetailCodesVO;
import com.starmol.sourcecodereview.bean.vo.CodeRecheckDetailVO;
import com.starmol.sourcecodereview.bean.vo.LoginNameDepartmentVO;
import com.starmol.sourcecodereview.bean.vo.SourceCodeFileAuditVO;
import com.starmol.sourcecodereview.bean.vo.SourceCodeFileVersionVO;
import com.starmol.sourcecodereview.bean.vo.SourceCodeReviewResultVO;
import com.starmol.sourcecodereview.common.ResponseWrapper;
import com.starmol.sourcecodereview.common.UserMetaData;
import com.starmol.sourcecodereview.constant.SysRoleTypeEnum;
import com.starmol.sourcecodereview.exception.KnowException;
import com.starmol.sourcecodereview.model.User;
import com.starmol.sourcecodereview.model.codereview.SourceCodeFileVersion;
import com.starmol.sourcecodereview.model.codereview.SourceCodeReviewResult;
import com.starmol.sourcecodereview.service.SourceCodeFileVersionService;
import com.starmol.sourcecodereview.service.SourceCodeReviewRecheckService;
import com.starmol.sourcecodereview.service.SourceCodeReviewResultService;
import com.starmol.sourcecodereview.service.feign.CodeReviewClient;
import com.starmol.sourcecodereview.service.feign.UserServiceClient;
import com.starmol.sourcecodereview.utils.HttpRequestUtil;
import com.starmol.sourcecodereview.utils.IdWorker;
import com.starmol.sourcecodereview.utils.SpringContextUtils;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * 代码复核结果服务实现类
 *
 * @author system
 * @since 2025-01-07
 */

@Service
@RequiredArgsConstructor
@Slf4j
@EnableAsync
public class SourceCodeReviewRecheckServiceImpl implements SourceCodeReviewRecheckService {

    private final ObjectMapper objectMapper;
    private final CodeReviewClient codeReviewClient;

    @Override
    public void createSourceCodeReviewRecheck(SourceCodeReviewRecheckDTO sourceCodeReviewRecheckDTO) {
        SourceCodeReviewResultService sourceCodeReviewResultService = SpringContextUtils.getInstanceByType(SourceCodeReviewResultService.class);
        SourceCodeReviewResult sourceCodeReviewResult = sourceCodeReviewResultService.getById(sourceCodeReviewRecheckDTO.getResultId());
        SourceCodeFileVersionService sourceCodeFileVersionService = SpringContextUtils.getInstanceByType(SourceCodeFileVersionService.class);
        SourceCodeFileVersionVO sourceCodeFileVersionVO = sourceCodeFileVersionService.getSourceCodeFileVersionVOByVersionId(sourceCodeReviewResult.getFileVersionId());
        //调用第三方接口获取审查结果;
        //注: 虽然用户后端是多个版本, 但是在当前系统中仍然按照一个版本对待
        if (Objects.nonNull(sourceCodeFileVersionVO) && StringUtils.isNotBlank(sourceCodeFileVersionVO.getMinioId())) {
            CodeReviewRecheckBO codeReviewRecheckBO = objectMapper.convertValue(sourceCodeReviewRecheckDTO, CodeReviewRecheckBO.class);
            codeReviewRecheckBO.setReviewId(sourceCodeFileVersionVO.getMinioId());
            createReviewRecheck(codeReviewRecheckBO);
        }
    }

    @SneakyThrows
    private void createReviewRecheck(CodeReviewRecheckBO codeReviewRecheckBO) {
        try {
            // 调用第三方接口创建复审
            log.info("调用第三方接口创建复审 Review ID:{}", codeReviewRecheckBO.getReviewId());
            ResponseDataBO<Object>  responseWrapper = codeReviewClient.reviewRecheck(codeReviewRecheckBO,HttpRequestUtil.getDebugId(),HttpRequestUtil.getLoginName());
            if (!responseWrapper.getCode().equals(200)) { // 第三方接口调用识别
                log.error("创建复审,调用接口失败: {}", responseWrapper.getMessage());
                throw new KnowException(responseWrapper.getMessage());
            }
        } catch (Exception e) {
            log.error("创建复审出错", e);
            throw new KnowException(e.getMessage(), e);
        }
    }


    @Override
    public IPage<SourceCodeFileAuditVO> getCodeRecheckList(Long pageNumber, Long pageSize, String fileName, Long depId, Long userId, Integer status) {
        //将 fileName 转换 reviewIds
        //从dmsc_file_version表中获取file_origin_name like fileName的记录
        SourceCodeFileVersionService sourceCodeFileVersionService = SpringContextUtils.getInstanceByType(SourceCodeFileVersionService.class);
        List<String> reviewIds = null; //没有指定fileName,此过滤条件无效
        if (StringUtils.isNotBlank(fileName)) {
            List<SourceCodeFileVersion> sourceCodeFiles = sourceCodeFileVersionService.lambdaQuery().like(StringUtils.isNotBlank(fileName), SourceCodeFileVersion::getFileOriginName, fileName).list();
            reviewIds = CollectionUtils.isEmpty(sourceCodeFiles) ? Collections.emptyList() : sourceCodeFiles.stream().map(SourceCodeFileVersion::getMinioId).collect(Collectors.toList());
        }
        //获取用户信息
        List<String> submitUserIds = null; //没有指定部门和用户,此过滤条件无效
        if (Objects.nonNull(depId) && Objects.isNull(userId))  { //指定部门, 没有指定用户
            UserServiceClient userServiceClient = SpringContextUtils.getInstanceByType(UserServiceClient.class);
            ResponseWrapper<List<User>> responseWrapper = userServiceClient.getUsersByDepartmentsIds(Collections.singletonList(depId));
            if (responseWrapper.isSucc()) { // 第三方接口调用成功
                submitUserIds = responseWrapper.getContent().stream().map(User::getLoginName).collect(Collectors.toList());
            }
        }
        else if (Objects.nonNull(depId) && Objects.nonNull(userId)) {//指定部门, 指定用户
            UserServiceClient userServiceClient = SpringContextUtils.getInstanceByType(UserServiceClient.class);
            ResponseWrapper<UserDTO> responseWrapper = userServiceClient.getUserById(userId);
            if (responseWrapper.isSucc()) { // 第三方接口调用成功
                submitUserIds = Collections.singletonList(responseWrapper.getContent().getLoginName());
            }
        }

        //获取当前用户的角色ID
        Integer userRoleTypeValue =Optional.ofNullable(HttpRequestUtil.getUser()).map(UserMetaData::getSysRoleType).map(SysRoleTypeEnum::getValue).orElse( null);

        CodeReviewRecheckListRequestBO codeReviewRecheckListRequestBO = new CodeReviewRecheckListRequestBO();
        codeReviewRecheckListRequestBO.setReviewIds(reviewIds);
        codeReviewRecheckListRequestBO.setSubmitUserIds(submitUserIds);
        codeReviewRecheckListRequestBO.setPageNum(pageNumber.intValue());
        codeReviewRecheckListRequestBO.setPageSize(pageSize.intValue());
        codeReviewRecheckListRequestBO.setCurUserRoleType(userRoleTypeValue);
        codeReviewRecheckListRequestBO.setRecheckStatus(status);
        CodeReviewRecheckResultBO codeReviewRecheckResultBO = getReviewRecheckList(codeReviewRecheckListRequestBO);
        //获取其中的reviewId列表
        List<String> resultReviewIds = codeReviewRecheckResultBO.getRecords().stream().map(CodeReviewRecheckRecoderBO::getReviewId).collect(Collectors.toList());
        List<SourceCodeFileVersion> resultSourceCodeFiles = CollectionUtils.isEmpty(resultReviewIds)? Collections.emptyList(): sourceCodeFileVersionService.lambdaQuery().in(CollectionUtils.isNotEmpty(resultReviewIds), SourceCodeFileVersion::getMinioId, resultReviewIds).list();

        //获取SourceCodeFileVersion中的file_version_id的List
        List<Long> fileVersionIdList = CollectionUtils.isEmpty(resultSourceCodeFiles)? Collections.emptyList(): resultSourceCodeFiles.stream().map(SourceCodeFileVersion::getId).collect(Collectors.toList());

        List<SourceCodeReviewResult> sourceCodeReviewResultList = Collections.emptyList();
        if (CollectionUtils.isNotEmpty(fileVersionIdList)) {
            QueryWrapper<SourceCodeReviewResult> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("DISTINCT file_id, id, file_version_id, review_time").lambda().in(SourceCodeReviewResult::getFileVersionId,fileVersionIdList).orderByDesc(SourceCodeReviewResult::getFileId, SourceCodeReviewResult::getReviewTime);
            SourceCodeReviewResultService sourceCodeReviewResultService = SpringContextUtils.getInstanceByType(SourceCodeReviewResultService.class);
            sourceCodeReviewResultList = sourceCodeReviewResultService.list(queryWrapper);
        }

        //获取其中的submitUserId列表
        List<String> submitUserIdList = codeReviewRecheckResultBO.getRecords().stream().map(CodeReviewRecheckRecoderBO::getSubmitUserId).collect(Collectors.toList());

        //获取用户部门信息
        List<LoginNameDepartmentVO> loginNameDepartmentList = Collections.emptyList();
        if (CollectionUtils.isNotEmpty(submitUserIdList))  {
            UserServiceClient userServiceClient = SpringContextUtils.getInstanceByType(UserServiceClient.class);
            ResponseWrapper<List<LoginNameDepartmentVO>> responseWrapper = userServiceClient.getDepartmentsByLoginNames(submitUserIdList);
            if (responseWrapper.isSucc()) { // 第三方接口调用成功
               loginNameDepartmentList = responseWrapper.getContent();
            }
        }
        //将loginNameDepartmentList转成以loginName为key的Map
        Map<String, LoginNameDepartmentVO> loginNameDepartmentMap = loginNameDepartmentList.stream().collect(Collectors.toMap(LoginNameDepartmentVO::getLoginName, loginNameDepartmentVO -> loginNameDepartmentVO, (key1, key2) -> key1));


        //将resultSourceCodeFiles转成以minioId为key的Map
        Map<String, SourceCodeFileVersion> resultSourceCodeFilesMap = resultSourceCodeFiles.stream().collect(Collectors.toMap(SourceCodeFileVersion::getMinioId, sourceCodeFileVersion -> sourceCodeFileVersion, (key1, key2) -> key1));
        //将sourceCodeReviewResultList转成以fileVersionId为key的Map
        Map<Long, SourceCodeReviewResult> sourceCodeReviewResultMap = sourceCodeReviewResultList.stream().collect(Collectors.toMap(SourceCodeReviewResult::getFileVersionId, sourceCodeReviewResult -> sourceCodeReviewResult, (key1, key2) -> key1));
        IPage<SourceCodeFileAuditVO> page = new Page<>(pageNumber, pageSize);
        page.setRecords(codeReviewRecheckResultBO.getRecords().stream().map(reviewRecheckListBO -> {
            SourceCodeFileAuditVO sourceCodeFileAuditVO = new SourceCodeFileAuditVO();
            Long fileVersionId = Optional.ofNullable(resultSourceCodeFilesMap.get(reviewRecheckListBO.getReviewId())).map(SourceCodeFileVersion::getId).orElse( null);
            if (Objects.nonNull(fileVersionId)){
                sourceCodeFileAuditVO.setFileVersionId(Optional.ofNullable(resultSourceCodeFilesMap.get(reviewRecheckListBO.getReviewId())).map(SourceCodeFileVersion::getId).orElse( null));//这里放了file_version表的id,为了查看详情时好查
                sourceCodeFileAuditVO.setReviewResultId(Optional.ofNullable(sourceCodeReviewResultMap.get(fileVersionId)).map(SourceCodeReviewResult::getId).orElse( null));
            }

            sourceCodeFileAuditVO.setFileName(Optional.ofNullable(resultSourceCodeFilesMap.get(reviewRecheckListBO.getReviewId())).map(SourceCodeFileVersion::getFileOriginName).orElse(""));
            sourceCodeFileAuditVO.setDepartmentName(Optional.ofNullable(loginNameDepartmentMap.get(reviewRecheckListBO.getSubmitUserId())).map(LoginNameDepartmentVO::getName).orElse(null));
            sourceCodeFileAuditVO.setDepartmentId(Optional.ofNullable(loginNameDepartmentMap.get(reviewRecheckListBO.getSubmitUserId())).map(LoginNameDepartmentVO::getId).orElse(null));
            sourceCodeFileAuditVO.setTime(reviewRecheckListBO.getTime());
            sourceCodeFileAuditVO.setOwnerName(reviewRecheckListBO.getSubmitUserId());
            sourceCodeFileAuditVO.setOwnerId(Optional.ofNullable(loginNameDepartmentMap.get(reviewRecheckListBO.getSubmitUserId())).map(LoginNameDepartmentVO::getUserId).orElse(null));
            sourceCodeFileAuditVO.setRecheckStatus(reviewRecheckListBO.getRecheckStatus());
            return sourceCodeFileAuditVO;
        }).collect(Collectors.toList()));
        page.setTotal(codeReviewRecheckResultBO.getTotal());
        page.setPages(codeReviewRecheckResultBO.getTotalPage());
        return page;
    }

    @SneakyThrows
    private CodeReviewRecheckResultBO getReviewRecheckList(CodeReviewRecheckListRequestBO codeReviewRecheckListRequestBO) {
        try {
            // 调用第三方接口获取复审列表
            log.info("调用第三方接口获取复审列表 Review ID List:{}", codeReviewRecheckListRequestBO.getReviewIds());
            ResponseDataBO<Object>  responseWrapper = codeReviewClient.reviewRecheckList(codeReviewRecheckListRequestBO,HttpRequestUtil.getDebugId(),HttpRequestUtil.getLoginName());
            if (!responseWrapper.getCode().equals(200)) { // 第三方接口调用失败, 更新状态审查结果为失败
                log.error("获取复审列表失败: {}", responseWrapper.getMessage());
                throw new KnowException(responseWrapper.getMessage());
            } else {
                log.info("获取复审列表成功");
                CodeReviewRecheckResultBO codeReviewRecheckResultBO = objectMapper.convertValue(responseWrapper.getData(),CodeReviewRecheckResultBO.class);
                return codeReviewRecheckResultBO;
            }
        } catch (Exception e) {
            log.error("获取复审列表出错", e);
            throw new KnowException(e.getMessage(), e);
        }
    }


    //2026-04-05: 按照用户要求增加复核详情接口
    @SneakyThrows
    @Override
    public CodeRecheckDetailVO getSourceCodeReviewRecheckDetailByResultAndVersion(Long fileVersionId, String version) { //这里的id是file_version表的id
        SourceCodeFileVersionService sourceCodeFileVersionService = SpringContextUtils.getInstanceByType(SourceCodeFileVersionService.class);
        SourceCodeFileVersion sourceCodeFileVersion = sourceCodeFileVersionService.getById(fileVersionId);
        CodeRecheckDetailVO codeRecheckDetailVO = new CodeRecheckDetailVO();
        //调用第三方接口获取审查结果
        if (Objects.nonNull(sourceCodeFileVersion) && StringUtils.isNotBlank(sourceCodeFileVersion.getMinioId())) {
            Integer userRoleTypeValue =Optional.ofNullable(HttpRequestUtil.getUser()).map(UserMetaData::getSysRoleType).map(SysRoleTypeEnum::getValue).orElse( null);
            Map<String, Object> resultMap = getStringObjectMap(sourceCodeFileVersion.getMinioId(),HttpRequestUtil.getDebugId(), HttpRequestUtil.getLoginName(), version, userRoleTypeValue);
            if (resultMap.get("code").equals(200)) {
                //将resultMap中的Map结构的data属性转换成CodeReviewResultBO
                CodeRecheckResultBO codeRecheckResultBO = objectMapper.convertValue(resultMap.get("data"), CodeRecheckResultBO.class);
                codeRecheckDetailVO = objectMapper.convertValue(codeRecheckResultBO, CodeRecheckDetailVO.class);//转换为CodeReviewResultVO
                //判断codeReviewResultBO中是否有值
                if (Objects.nonNull(codeRecheckResultBO)) {
                    //将结果转换为 CodeRecheckDetailCodesVO列表
                    List<CodeRecheckDetailCodesVO> details = new ArrayList<>();
                    for (CodeReCheckFileResultBO codeReCheckFileResultBO : codeRecheckResultBO.getFilesResult()) {
                        CodeRecheckDetailCodesVO detail = new CodeRecheckDetailCodesVO();
                        detail.setFileVersionId(fileVersionId);
                        detail.setFileName(codeReCheckFileResultBO.getFileName());
                        detail.setRuleId(codeReCheckFileResultBO.getRuleId());
                        detail.setRule(codeReCheckFileResultBO.getRule());
                        detail.setRuleSource(codeReCheckFileResultBO.getRuleSource());
                        detail.setLanguage(codeReCheckFileResultBO.getLanguage());
                        detail.setExplain(codeReCheckFileResultBO.getExplain());
                        if (CollectionUtils.isNotEmpty(codeReCheckFileResultBO.getCodes())) {
                            //遍历codeReviewFileResultBO.getCodes()将数据保存到details中
                            for (CodeRecheckCodeResultBO code : codeReCheckFileResultBO.getCodes()) {
                                CodeRecheckDetailCodesVO errorDetail = new CodeRecheckDetailCodesVO();
                                //把detail的属性复制到errorDetail中
                                errorDetail.setFileVersionId(detail.getFileVersionId());
                                errorDetail.setFileName(detail.getFileName());
                                errorDetail.setRuleId(detail.getRuleId());
                                errorDetail.setRule(detail.getRule());
                                errorDetail.setRuleSource(detail.getRuleSource());
                                errorDetail.setLanguage(detail.getLanguage());
                                errorDetail.setExplain(detail.getExplain());
                                errorDetail.setQuestionId(code.getQuestionId());
                                errorDetail.setCode(code.getCode());
                                errorDetail.setQuestionDesc(code.getQuestionDesc());
                                errorDetail.setLineNumber(code.getLineNumber());
                                errorDetail.setErrorReason(code.getErrorReason());
                                errorDetail.setModifySuggest(code.getModifySuggest());
                                errorDetail.setRecheckConclusion(code.getRecheckConclusion());
                                errorDetail.setQuestionDesc(code.getQuestionDesc());
                                errorDetail.setRecheckResultStatus(code.getRecheckResultStatus());
                                errorDetail.setRejectReason(code.getRejectReason());
                                errorDetail.setRecheckUserId(code.getRecheckUserId());

                                errorDetail.setId(IdWorker.getId());
                                details.add(errorDetail);
                            }
                        } else {
                            //代码文件审核通过只有一条记录
                            detail.setId(IdWorker.getId());
                            details.add(detail);
                        }
                    }
                    codeRecheckDetailVO.setDetailVOList(details);

                    return codeRecheckDetailVO;
                }
            } else {
                log.error("调用查看复核详情接口失败, Review ID: {}", sourceCodeFileVersion.getMinioId());
                throw new KnowException("调用查看复核详情接口失败");
            }
        }
        return codeRecheckDetailVO;
    }

    private Map<String, Object> getStringObjectMap(String reviewId, String debugId, String loginName, String version, Integer curUserRoleType) throws JsonProcessingException {
        //调用第三方接口获取审查结果
        Map<String, Object> resultMap = new HashMap<>();
        try {
            String jsonString = codeReviewClient.getRecheckDetail(reviewId, debugId, loginName, version, curUserRoleType);
            log.info("查看复核详情.成功, ID: {}, 结果:{}", reviewId, jsonString);
            resultMap = objectMapper.readValue(jsonString, Map.class);
        } catch (Exception e) {
            log.error("查看复核详情错误,Exception: {}", e.getMessage(), e);
            resultMap.put("code", 500);
            resultMap.put("message", e.getMessage());
        }
        return resultMap;
    }

    @Override
    public void submitReviewRecheckResults(CodeReviewRecheckResultSubmitDTO codeReviewRecheckResultSubmitDTO) {
        SourceCodeFileVersionService sourceCodeFileVersionService = SpringContextUtils.getInstanceByType(SourceCodeFileVersionService.class);
        SourceCodeFileVersion sourceCodeFileVersion = sourceCodeFileVersionService.getById(codeReviewRecheckResultSubmitDTO.getFileVersionId());

        if (Objects.nonNull(sourceCodeFileVersion) && StringUtils.isNotBlank(sourceCodeFileVersion.getMinioId())) {
            CodeReviewRecheckResultSubmitBO codeReviewRecheckResultSubmitBO = objectMapper.convertValue(codeReviewRecheckResultSubmitDTO, CodeReviewRecheckResultSubmitBO.class);
            codeReviewRecheckResultSubmitBO.setReviewId(sourceCodeFileVersion.getMinioId());
            submitReviewRecheckResult(codeReviewRecheckResultSubmitBO);
        }
    }

    @SneakyThrows
    private void submitReviewRecheckResult(CodeReviewRecheckResultSubmitBO codeReviewRecheckResultSubmitBO) {
        try {
            // 调用第三方接口提交评审结果
            log.info("调用第三方接口提交评审结果");
            ResponseDataBO<Object>  responseWrapper = codeReviewClient.reviewRecheckResultSubmit(codeReviewRecheckResultSubmitBO,HttpRequestUtil.getDebugId(),HttpRequestUtil.getLoginName());
            if (!responseWrapper.getCode().equals(200)) { // 第三方接口调用识别
                log.error("提交评审结果,调用接口失败: {}", responseWrapper.getMessage());
                throw new KnowException(responseWrapper.getMessage());
            }
        } catch (Exception e) {
            log.error("提交评审结果出错", e);
            throw new KnowException(e.getMessage(), e);
        }
    }

}
package com.starmol.circuitreview.backend.service.circuitreview;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starmol.circuitreview.backend.bean.dto.DepartmentDTO;
import com.starmol.circuitreview.backend.bean.dto.UserDTO;
import com.starmol.circuitreview.backend.bean.vo.CircuitFileVersionVO;
import com.starmol.circuitreview.backend.bean.vo.CircuitReviewResultVO;
import com.starmol.circuitreview.backend.common.ResponseWrapper;
import com.starmol.circuitreview.backend.constant.CircuitReviewStatusEnum;
import com.starmol.circuitreview.backend.constant.RuleTypeEnum;
import com.starmol.circuitreview.backend.exception.KnowException;
import com.starmol.circuitreview.backend.model.circuitreview.CircuitFileVersion;
import com.starmol.circuitreview.backend.model.circuitreview.CircuitReviewResult;
import com.starmol.circuitreview.backend.model.circuitreview.CircuitReviewResultDetail;
import com.starmol.circuitreview.backend.model.circuitreview.CircuitReviewRule;
import com.starmol.circuitreview.backend.repository.circuitreview.CircuitReviewResultMapper;
import com.starmol.circuitreview.backend.service.circuitreview.feign.ReviewClient;
import com.starmol.circuitreview.backend.service.circuitreview.feign.UserServiceClient;
import com.starmol.circuitreview.backend.service.common.StorageService;
import com.starmol.circuitreview.backend.utils.MultipartFileUtils;
import com.starmol.circuitreview.backend.utils.SpringContextUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 重新生成审查结果服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RegenerateReviewResultService {
    private final ObjectMapper objectMapper;
    private final ReviewClient reviewClient;
    private final CircuitReviewRuleService circuitReviewRuleService;
    private final CircuitReviewResultService circuitReviewResultService;
    private final CircuitReviewResultDetailService circuitReviewResultDetailService;
    private final CircuitFileService circuitFileService;
    private final CircuitFileVersionService circuitFileVersionService;
    private final StorageService storageService;
    private final CircuitReviewResultMapper circuitReviewResultMapper;
    private final UserServiceClient userServiceClient;

    public void regenerateReviewResult(Integer targetParam, List<String> departments) {
        RegenerateReviewResultService regenerateReviewResultService = SpringContextUtils.getInstanceByType(RegenerateReviewResultService.class);
        regenerateReviewResultService.process(targetParam, departments);
    }

    @Async
    public void process(Integer targetParam, List<String> departments) {
        log.info("开始根据参数重新生成审查结果数据，targetParam: {}, departments: {}", targetParam, departments);
        
        // 获取需要重跑的部门ID列表
        List<Long> departmentIds = new ArrayList<>();
        if (departments != null && !departments.isEmpty()) {
            try {
                ResponseWrapper<List<DepartmentDTO>> departmentResponse = userServiceClient.getDepartmentsByNames(departments);
                if (departmentResponse.isSucc() && departmentResponse.getContent() != null) {
                    departmentIds = departmentResponse.getContent().stream()
                            .map(DepartmentDTO::getId)
                            .collect(Collectors.toList());
                    log.info("获取到部门ID列表: {}", departmentIds);
                } else {
                    log.warn("无法获取部门信息，将不包含任何部门的记录");
                }
            } catch (Exception e) {
                log.error("获取部门信息失败", e);
                // 如果获取部门失败，设置为空列表，这样就不会有符合条件的记录
                departmentIds = new ArrayList<>();
            }
        }

        // 获取需要重跑的审查结果列表
        List<CircuitReviewResult> targetCircuitReviewResults = getTargetCircuitReviewResults(targetParam, departmentIds);
        List<Long> userIdList = targetCircuitReviewResults.stream().map(CircuitReviewResult::getCreateUser).collect(Collectors.toSet()).stream().toList();
        Map<Long, UserDTO> userIdAndUserDTOMap = new HashMap<>();
        try {
            ResponseWrapper<List<UserDTO>> usersResponseWrapper = userServiceClient.getUsersByIds(userIdList);
            if(!usersResponseWrapper.isSucc()) {
                log.error("获取用户列表失败: {} 错误信息: {}", userIdList, usersResponseWrapper.getMsg());
            }
            List<UserDTO> users = usersResponseWrapper.getContent();
            userIdAndUserDTOMap = users.stream().collect(Collectors.toMap(UserDTO::getId, Function.identity()));

        } catch (Exception e) {
            log.error("获取用户信息失败", e);
        }

        log.info("找到 {} 条需要重新生成的审查结果记录", targetCircuitReviewResults.size());

        int index = 0;
        int total = targetCircuitReviewResults.size();

        for (CircuitReviewResult targetCircuitReviewResult : targetCircuitReviewResults) {
            index++;
            log.info("正在处理第 {}/{} 个审查结果", index, total);
            log.info("处理审查结果ID: {}, 文件版本ID: {}", targetCircuitReviewResult.getId(), targetCircuitReviewResult.getFileVersionId());

            // 没删除、没粉碎的文件
            List<CircuitFileVersion> fileVersions = circuitFileVersionService.list(Wrappers.<CircuitFileVersion>lambdaQuery()
                    .eq(CircuitFileVersion::getId, targetCircuitReviewResult.getFileVersionId())
                    .eq(CircuitFileVersion::getIsRecycle, 0));
            if(CollectionUtils.isNotEmpty(fileVersions)) {
                CircuitFileVersionVO circuitFileVersionVO = objectMapper.convertValue(fileVersions.get(0), CircuitFileVersionVO.class);
                try {
                    log.info("获取文件版本成功，文件名: {}", circuitFileVersionVO.getFileOriginName());
                    // 从存储服务获取文件内容并创建MultipartFile
                    MultipartFile multipartFile = MultipartFileUtils.createMultipartFileFromStorage(storageService, circuitFileVersionVO);
                    log.info("从存储服务创建MultipartFile成功");

                    // 调用第三方服务上传文件
                    log.info("开始上传文件到第三方服务，文件名: {}", multipartFile.getOriginalFilename());
                    ResponseWrapper<Map<String, Object>> uploadResponse = reviewClient.uploadFile(multipartFile);
                    log.info("文件上传响应状态: {}", uploadResponse.isSucc());

                    RegenerateReviewResultService regenerateReviewResultService = SpringContextUtils.getInstanceByType(RegenerateReviewResultService.class);
                    if(uploadResponse.isSucc()) {
                        // 审查文件并生成审查结果详情，同时需要更新审查记录的总检查点数、通过的检查点数、通过率和是否闭环几个属性
                        log.info("文件上传成功，开始同步审查，审查结果ID: {}", targetCircuitReviewResult.getId());
                        // 使用事务确保立即提交数据库更改
                        String userName = "Unknown";
                        String departmentName = "Unknown";
                        if(userIdAndUserDTOMap.containsKey(targetCircuitReviewResult.getCreateUser())) {
                            UserDTO userDTO = userIdAndUserDTOMap.get(targetCircuitReviewResult.getCreateUser());
                            userName = userDTO.getLoginName();
                            departmentName = userDTO.getDepartmentName();
                        }
                        regenerateReviewResultService.syncReview(targetCircuitReviewResult.getId(), targetCircuitReviewResult.getReviewTime(), multipartFile.getOriginalFilename(), targetCircuitReviewResult.getCreateUser(), targetCircuitReviewResult.getUpdateDate(), circuitFileVersionVO, userName, departmentName);
                    }
                    else {
                        // 上传文件失败，将审查结果标为失败，删除结果的所有详情
                        log.error("文件上传到第三方服务失败: {}", uploadResponse.getMsg());
                        log.info("更新审查结果为失败状态，审查结果ID: {}, 文件版本ID: {}, 文件名: {}", targetCircuitReviewResult.getId(), targetCircuitReviewResult.getFileVersionId(), circuitFileVersionVO.getFileOriginName());
                        // 使用事务确保立即提交数据库更改
                        regenerateReviewResultService.updateReviewStatusFailed(targetCircuitReviewResult.getId(), uploadResponse.getMsg(), targetCircuitReviewResult.getCreateUser(), targetCircuitReviewResult.getUpdateDate());
                    }
                } catch (Exception e) { // 捕获所有异常，而不仅仅是 IOException
                    log.error("重新生成审查结果错误: 文件版本id: {} 审查结果id: {}，文件名: {}", targetCircuitReviewResult.getId(), targetCircuitReviewResult.getFileVersionId(), circuitFileVersionVO.getFileOriginName(), e);
                    // 即使出错也继续处理下一个
                    continue;
                }
            } else {
                log.warn("文件版本不存在，文件版本ID: {}", targetCircuitReviewResult.getFileVersionId());
            }
        }
        log.info("完成所有指定条件审查结果的重新生成");
    }

    /**
     * 根据参数获取需要重跑的目标审查结果
     * @param targetParam 重跑参数：0-只重跑包含标记为废弃规则的结果；1-重跑全部结果
     * @param departmentIds 部门ID列表
     * @return 目标审查结果列表
     */
    private List<CircuitReviewResult> getTargetCircuitReviewResults(Integer targetParam, List<Long> departmentIds) {
        // 构建查询条件
        LambdaQueryWrapper<CircuitReviewResult> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CircuitReviewResult::getIsDelete, 0); // 只查询没有删除的记录
        queryWrapper.eq(CircuitReviewResult::getIsClosedLoop, 0); // 只查询问题未关闭的记录

        // 如果指定了部门ID列表，则只查询这些部门的记录。如果为空或不指定则全部单位都重跑。
        if (departmentIds != null && !departmentIds.isEmpty()) {
            // 获取这些部门ID对应的文件版本ID列表
            List<Long> fileVersionIds = circuitFileVersionService.list(new LambdaQueryWrapper<CircuitFileVersion>()
                            .in(CircuitFileVersion::getDepartmentId, departmentIds))
                    .stream()
                    .map(CircuitFileVersion::getId)
                    .collect(Collectors.toList());
            
            if (!fileVersionIds.isEmpty()) {
                queryWrapper.in(CircuitReviewResult::getFileVersionId, fileVersionIds);
            }
        }

        if (targetParam != null && targetParam == 0) {
            // 只重跑包含标记为废弃规则的结果
            // 获取所有废弃规则的ID和代码列表
            List<CircuitReviewRule> deprecatedRules = circuitReviewRuleService.list(
                    new LambdaQueryWrapper<CircuitReviewRule>()
                            .eq(CircuitReviewRule::getIsDeprecate, 1));
            
            if (deprecatedRules.isEmpty()) {
                // 如果没有废弃的规则，返回空列表
                return new ArrayList<>();
            }
            
            // 提取废弃规则的ID和代码列表
            List<Long> deprecatedRuleIds = deprecatedRules.stream()
                    .map(CircuitReviewRule::getId)
                    .collect(Collectors.toList());
            List<String> deprecatedRuleCodes = deprecatedRules.stream()
                    .map(CircuitReviewRule::getCode)
                    .collect(Collectors.toList());
            
            // 查询引用了这些废弃规则的审查结果详情
            LambdaQueryWrapper<CircuitReviewResultDetail> detailQueryWrapper = new LambdaQueryWrapper<>();
            if (!deprecatedRuleIds.isEmpty()) {
                detailQueryWrapper.in(CircuitReviewResultDetail::getRuleId, deprecatedRuleIds);
            }
            if (!deprecatedRuleCodes.isEmpty()) {
                if (!deprecatedRuleIds.isEmpty()) {
                    detailQueryWrapper.or(); // 添加OR条件
                }
                detailQueryWrapper.in(CircuitReviewResultDetail::getRuleCode, deprecatedRuleCodes);
            }
            
            List<CircuitReviewResultDetail> deprecatedResultDetails = circuitReviewResultDetailService.list(detailQueryWrapper);
            
            if (deprecatedResultDetails.isEmpty()) {
                // 如果没有找到引用废弃规则的详情，返回空列表
                return new ArrayList<>();
            }
            
            // 提取这些详情对应的结果ID列表
            List<Long> resultIdsWithDeprecatedRules = deprecatedResultDetails.stream()
                    .map(CircuitReviewResultDetail::getResultId)
                    .distinct()
                    .collect(Collectors.toList());
            
            // 在查询条件中添加结果ID限制
            queryWrapper.in(CircuitReviewResult::getId, resultIdsWithDeprecatedRules);
        }

        // 查询符合条件的审查结果
        return circuitReviewResultService.list(queryWrapper);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void syncReview(Long resultId, LocalDateTime reviewTime, String filePath, Long userId, LocalDateTime updateTime, CircuitFileVersionVO circuitFileVersionVO, String userName, String departmentName) {
        log.info("开始同步审查结果，结果ID: {}, 文件路径: {}", resultId, filePath);
        LocalDateTime now = LocalDateTime.now();
        try {
            // 1. 调用第三方接口
            log.info("调用第三方审查接口，文件路径: {}", filePath);
            Map<String, String> paramsMap = Map.of(
                    "filePath", filePath,
                    "userId", userName,
                    "departmentId", departmentName
            );
            ResponseWrapper<Map<String, Map<String, Object>>> responseWrapper = reviewClient.review(paramsMap);
            log.info("第三方审查接口响应状态: {}", responseWrapper.isSucc());

            if (!responseWrapper.isSucc()) {
                // 审查失败，将审查结果标为失败，删除结果的所有详情
                log.error("电路审查失败: {}", responseWrapper.getMsg());
                log.info("更新审查结果为失败状态，结果ID: {}", resultId);
                updateReviewStatusFailed(resultId, responseWrapper.getMsg(), userId, updateTime);
                // 审查失败后删除python服务的文件
                deleteReviewFileFromThirdParty(filePath);
                return;
            }

            // 2. 处理规则
            // 3. 组装详情
            Map<String, Object> result = responseWrapper.getContent().get("data");
            log.debug("收到审查数据: {}", result);
            
            @SuppressWarnings("unchecked")
            List<String> ruleTypes = (List<String>) result.get("规则类型");
            @SuppressWarnings("unchecked")
            List<String> ruleNames = (List<String>) result.get("审查规则");
            @SuppressWarnings("unchecked")
            List<String> deviceTypes = (List<String>) result.get("器件型号");
            @SuppressWarnings("unchecked")
            List<String> tagPins = (List<String>) result.get("位号引脚");
            @SuppressWarnings("unchecked")
            List<String> suggestions = (List<String>) result.get("审查意见");
            @SuppressWarnings("unchecked")
            List<String> isPassedList = (List<String>) result.get("是否通过");
            
            log.info("解析到审查规则数量: {}", ruleNames != null ? ruleNames.size() : 0);
            
            List<CircuitReviewResultDetail> details = new ArrayList<>();
            boolean isClosedLoop = true;
            int passedCount = 0;
            int failedCount = 0;
            if (ruleNames != null) {
                for (int i = 0; i < ruleNames.size(); i++) {
                    String code = ruleNames.get(i);
                    log.debug("处理规则: {}", code);
                    // 查找是否已存在
                    List<CircuitReviewRule> circuitReviewRules = circuitReviewRuleService.list(Wrappers.<CircuitReviewRule>lambdaQuery().eq(CircuitReviewRule::getCode, code));
                    if(CollectionUtils.isEmpty(circuitReviewRules)) {
                        log.warn("未找到匹配的审查规则，规则代码: {}", code);
                        continue;
                    }
                    log.debug("找到匹配的审查规则，规则ID: {}", circuitReviewRules.get(0).getId());
                    
                    CircuitReviewRule rule = circuitReviewRules.get(0);
                    Long ruleId = rule.getId();
                    RuleTypeEnum ruleType = rule.getType();

                    CircuitReviewResultDetail detail = new CircuitReviewResultDetail();
                    detail.setResultId(resultId);
                    detail.setRuleId(ruleId);
                    detail.setRuleCode(rule.getCode());
                    detail.setDeviceType(deviceTypes != null && i < deviceTypes.size() ? deviceTypes.get(i) : null);
                    detail.setTagPin(tagPins != null && i < tagPins.size() ? tagPins.get(i) : null);
                    detail.setReviewSuggestion(suggestions != null && i < suggestions.size() ? suggestions.get(i) : null);
                    detail.setIsPassed(isPassedList != null && i < isPassedList.size() && isPassedList.get(i).contains("不通过") ? 0 : 1);
                    detail.setCreateUser(userId);
                    detail.setCreateDate(now);
                    detail.setUpdateDate(now);
                    details.add(detail);
                    if(StringUtils.isNotEmpty(detail.getTagPin())) {
                        // 如果存在引脚标签，则根据逗号分隔的引脚数量来计算检查点数量
                        int checkpointCount = detail.getTagPin().split(",").length;
                        if(detail.getIsPassed() == 0) {
                            // 如果未通过，则增加失败的检查点数量
                            failedCount = failedCount + checkpointCount;
                        }
                        else {
                            // 如果通过，则增加通过的检查点数量
                            passedCount = passedCount + checkpointCount;
                        }
                    }
                    else {
                        // 如果不存在引脚标签，则每个规则计为一个检查点
                        if(detail.getIsPassed() == 0) {
                            // 如果未通过，则增加失败的检查点数量
                            failedCount++;
                        }
                        else {
                            // 如果通过，则增加通过的检查点数量
                            passedCount++;
                        }
                    }
                    log.debug("添加审查详情，规则ID: {}, 是否通过: {}", ruleId, detail.getIsPassed());

                    if ((ruleType.equals(RuleTypeEnum.COERCIVE_RULE) || ruleType.equals(RuleTypeEnum.NEEDFUL_RULE)) && detail.getIsPassed() == 0) {
                        isClosedLoop = false;
                        log.debug("发现强制性或必要性规则未通过，设置闭环状态为false");
                    }
                }
            }

            log.info("共生成 {} 条审查详情记录", details.size());
            
            // 4. 删除原来的该审查结果的所有详情
            log.info("删除原有的审查详情记录，结果ID: {}", resultId);
            circuitReviewResultDetailService.remove(Wrappers.<CircuitReviewResultDetail>lambdaQuery().eq(CircuitReviewResultDetail::getResultId, resultId));

            // 5. 批量保存
            if (!details.isEmpty()) {
                log.info("批量保存新的审查详情记录，数量: {}", details.size());
                circuitReviewResultDetailService.saveBatch(details);
            } else {
                log.warn("没有有效的审查详情需要保存");
            }

            // 6. 更新CircuitReviewResult状态
            int total = passedCount + failedCount;
            int lastVersionFailCheckPoints = getLastVersionFailedCheckPoints(circuitFileVersionVO);
            int closedFailCheckPoints = lastVersionFailCheckPoints - failedCount;
            if(closedFailCheckPoints < 0) {
                closedFailCheckPoints = 0;
            }


            log.info("更新审查结果统计信息，通过数: {}, 总数: {}, 闭环状态: {}", passedCount, total, isClosedLoop);
            updateReviewResultSuccess(resultId, passedCount, total, isClosedLoop, userId, updateTime);
            
            // 7. 审查成功后删除python服务的文件
            log.info("审查处理完成，删除第三方服务上的文件");
            deleteReviewFileFromThirdParty(filePath);
            
            log.info("同步审查结果完成，结果ID: {}", resultId);
        } catch (Exception e) {
            log.error("电路审查异步处理失败，结果ID: {}", resultId, e);
            // 审查失败后删除python服务的文件
            deleteReviewFileFromThirdParty(filePath);
            throw new KnowException(e.getMessage(), e);
        }
    }

    private int getLastVersionFailedCheckPoints(CircuitFileVersionVO circuitFileVersionVO) {
        List<CircuitFileVersion> fileVersions = circuitFileVersionService.list(Wrappers.<CircuitFileVersion>lambdaQuery().eq(CircuitFileVersion::getFileId, circuitFileVersionVO.getFileId()).lt(CircuitFileVersion::getFileVersion, circuitFileVersionVO.getFileVersion()).orderByDesc(CircuitFileVersion::getFileVersion));
        // 没有比当前文件版本更小的文件版本，则返回0
        if(org.apache.commons.collections.CollectionUtils.isEmpty(fileVersions)) {
            return 0;
        }
        List<Long> fileVersionIdList = fileVersions.stream().map(CircuitFileVersion::getId).toList();
        List<CircuitReviewResult> circuitReviewResults = circuitReviewResultService.list(Wrappers.<CircuitReviewResult>lambdaQuery().in(CircuitReviewResult::getFileVersionId, fileVersionIdList));
        // 比当前文件版本更小的文件版本没有审查结果，则返回0
        if(org.apache.commons.collections.CollectionUtils.isEmpty(circuitReviewResults)) {
            return 0;
        }
        for (CircuitFileVersion fileVersion : fileVersions) {
            List<CircuitReviewResult> fileVersionCircuitReviewResults = circuitReviewResults.stream().filter(circuitReviewResult -> circuitReviewResult.getFileVersionId().equals(fileVersion.getId())).toList();
            if(org.apache.commons.collections.CollectionUtils.isNotEmpty(fileVersionCircuitReviewResults)) {
                CircuitReviewResult circuitReviewResult = fileVersionCircuitReviewResults.get(0);
                return circuitReviewResult.getCheckPoints() - circuitReviewResult.getPassCheckPoints();
            }
        }
        return 0;
    }

    private void deleteReviewFileFromThirdParty(String filePath) {
        log.info("请求删除第三方服务上的文件，文件路径: {}", filePath);
        ResponseWrapper<Void> voidResponseWrapper = reviewClient.deleteFile(filePath);
        if (!voidResponseWrapper.isSucc()) {
            log.error("删除文件失败: {}", voidResponseWrapper.getMsg());
        }
        else {
            log.info("删除文件成功: {}", filePath);
        }
    }

    /**
     * 更新审查结果为成功状态
     * 使用REQUIRES_NEW事务传播行为，确保在独立事务中执行
     */
    @Transactional
    public void updateReviewResultSuccess(Long resultId, int passedCount, int total, boolean isClosedLoop, Long userId, LocalDateTime updateTime) {
        log.info("更新审查结果为成功状态，结果ID: {}, 通过数: {}, 总数: {}, 闭环状态: {}", resultId, passedCount, total, isClosedLoop);

        CircuitReviewResult update = circuitReviewResultService.getById(resultId);
        update.setPassCheckPoints(passedCount);
        update.setCheckPoints(total);
        update.setPassRate(total > 0 ? new BigDecimal(passedCount).divide(new BigDecimal(total), 5, RoundingMode.HALF_UP) : BigDecimal.ZERO);
        update.setIsClosedLoop(isClosedLoop ? 1 : 0);
        update.setUpdateDate(updateTime);
        update.setUpdateUser(userId);
        update.setStatus(CircuitReviewStatusEnum.FINISHED);

        CircuitFileVersion circuitFileVersion = circuitFileVersionService.getById(update.getFileVersionId());

        updateCircuitReviewResultClosedFailCheckPoints(circuitFileVersion, update);
        
        boolean updated = circuitReviewResultService.updateById(update);
        log.info("审查结果更新完成，结果ID: {}, 更新状态: {}", resultId, updated);
    }

    private void updateCircuitReviewResultClosedFailCheckPoints(CircuitFileVersion circuitFileVersion, CircuitReviewResult circuitReviewResult) {
        if (circuitFileVersion != null) {
            int lastVersionFailedCheckPoints = getLastVersionFailedCheckPoints(objectMapper.convertValue(circuitFileVersion, CircuitFileVersionVO.class));
            int failedCount = circuitReviewResult.getCheckPoints() - circuitReviewResult.getPassCheckPoints();
            int closedFailCheckPoints = lastVersionFailedCheckPoints - failedCount;
            if(closedFailCheckPoints < 0) {
                closedFailCheckPoints = 0;
            }
            circuitReviewResult.setClosedFailCheckPoints(closedFailCheckPoints);
        }
        else {
            circuitReviewResult.setClosedFailCheckPoints(0);
        }
    }

    /**
     * 更新审查结果为失败状态
     * 使用REQUIRES_NEW事务传播行为，确保在独立事务中执行
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateReviewStatusFailed(Long resultId, String errorMessage, Long userId, LocalDateTime updateTime) {
        log.info("更新审查结果为失败状态，结果ID: {}, 错误信息: {}", resultId, errorMessage);
        
        CircuitReviewResult update = new CircuitReviewResult();
        update.setId(resultId);
        update.setStatus(CircuitReviewStatusEnum.FAILED);
        update.setErrorMessage(errorMessage);
        update.setUpdateDate(updateTime);
        
        boolean updated = circuitReviewResultService.updateById(update);
        log.info("审查结果状态更新为失败，结果ID: {}, 更新状态: {}", resultId, updated);
        
        log.info("删除相关的审查详情记录，结果ID: {}", resultId);
        boolean removed = circuitReviewResultDetailService.remove(Wrappers.<CircuitReviewResultDetail>lambdaQuery().eq(CircuitReviewResultDetail::getResultId, resultId));
        log.info("审查详情记录删除成功，结果ID: {}, 删除状态: {}", resultId, removed);
    }
}

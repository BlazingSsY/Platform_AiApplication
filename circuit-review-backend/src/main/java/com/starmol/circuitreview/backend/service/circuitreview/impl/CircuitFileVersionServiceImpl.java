package com.starmol.circuitreview.backend.service.circuitreview.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starmol.circuitreview.backend.bean.bo.FileCheckResult;
import com.starmol.circuitreview.backend.bean.dto.ChangeFileRecycleStatusDTO;
import com.starmol.circuitreview.backend.bean.vo.CircuitFileVO;
import com.starmol.circuitreview.backend.bean.vo.CircuitFileVersionAndResultVO;
import com.starmol.circuitreview.backend.bean.vo.CircuitFileVersionVO;
import com.starmol.circuitreview.backend.bean.vo.FileVO;
import com.starmol.circuitreview.backend.constant.CircuitReviewResultDetailAuditStatusEnum;
import com.starmol.circuitreview.backend.constant.CircuitReviewStatusEnum;
import com.starmol.circuitreview.backend.constant.FileSecretLevelEnum;
import com.starmol.circuitreview.backend.exception.KnowException;
import com.starmol.circuitreview.backend.model.base.DeleteDTO;
import com.starmol.circuitreview.backend.model.circuitreview.*;
import com.starmol.circuitreview.backend.repository.circuitreview.CircuitFileVersionMapper;
import com.starmol.circuitreview.backend.service.base.impl.BaseCascadeServiceImpl;
import com.starmol.circuitreview.backend.service.circuitreview.*;
import com.starmol.circuitreview.backend.service.common.StorageService;
import com.starmol.circuitreview.backend.utils.FileUtils;
import com.starmol.circuitreview.backend.utils.HttpRequestUtil;
import com.starmol.circuitreview.backend.utils.SpringContextUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Lazy
public class CircuitFileVersionServiceImpl extends BaseCascadeServiceImpl<CircuitFileVersionMapper, CircuitFileVersion> implements CircuitFileVersionService {
    @Value("${deployment.type}")
    private Integer deploymentType;

    private final ObjectMapper objectMapper;
    private final StorageService storageService;
    private final CircuitFileService circuitFileService;
    private final CircuitReviewResultDetailAuditService circuitReviewResultDetailAuditService;

    @Override
    public CircuitFileVersionVO createCircuitFileVersion(MultipartFile file, Long fileId, FileSecretLevelEnum fileSecretLevelEnum) {
        if (file != null && file.getOriginalFilename() != null) {
            String originalFilename = file.getOriginalFilename();
            FileCheckResult result = FileUtils.checkCircuitFileExtension(deploymentType, originalFilename);
            if (result.isValid()) {
                CircuitFileVO circuitFileVO = circuitFileService.getCircuitFileVOById(fileId);
                if (circuitFileVO != null) {
                    // 获取该文件的所有版本
                    List<CircuitFileVersionVO> fileVersions = getCircuitFileVersionVOById(fileId);

                    // 如果存在有效版本，则需要验证新上传的文件是否为该文件的新版本（文件相似度大于50%）
                    if (!fileVersions.isEmpty()) {
                        // 找到最新版本（fileVersion最大的）
                        CircuitFileVersionVO latestVersion = fileVersions.stream()
                                .max((v1, v2) -> Integer.compare(v1.getFileVersion(), v2.getFileVersion()))
                                .orElse(null);

                        if (latestVersion != null) {
                            // 验证新上传的文件是否为该文件的新版本（文件相似度大于50%）
                            validateFileVersion(latestVersion, file);
                        }
                    }

                    String minioId = storageService.uploadFile(file, originalFilename);
                    if (minioId != null) {  // 创建新的文件
                        CircuitFileVersionVO circuitFileVersionVO = createCircuitFileVersion(minioId, circuitFileVO.getFileName(), originalFilename, circuitFileVO.getId(), fileSecretLevelEnum);
                        // 更新文件记录的最新版本id和版本序号
                        circuitFileService.update(Wrappers.<CircuitFile>lambdaUpdate().eq(CircuitFile::getId, fileId).set(CircuitFile::getLatestFileVersionId, circuitFileVersionVO.getId()).set(CircuitFile::getLatestFileVersion, circuitFileVersionVO.getFileVersion()));
                        discardAllAuditData(fileId);
                        return circuitFileVersionVO;
                    }
                    else {
                        throw new KnowException("上传文件失败");
                    }
                } else {
                    throw new KnowException("电路图文件不存在");
                }
            } else {
                throw new KnowException("无效的电路图文件，电路图文件应该是%s文件".formatted(result.getExpectedExtension()));
            }
        } else {
            throw new KnowException("无效的电路图文件");
        }
    }

    /**
     * 废弃指定文件的所有审查数据
     * 
     * <p>该方法用于在上传新版本文件时，清理旧版本的审查相关数据：</p>
     * <ul>
     *     <li>将状态为 IN_PROCESS（处理中）的审查结果详情审核记录标记为 CANCELLED（已取消）</li>
     *     <li>根据 fileId 查询所有审查结果详情审核记录</li>
     *     <li>统计每个审查结果审核对应的详情审核数量</li>
     *     <li>根据详情审核数量决定是更新还是删除审查结果审核记录：
     *         <ul>
     *             <li>如果详情审核数量 > 0：更新审查结果审核记录的审核完成状态</li>
     *             <li>如果详情审核数量 = 0：删除审查结果审核记录</li>
     *         </ul>
     *     </li>
     *     <li>批量删除或更新审查结果审核记录</li>
     *     <li>将对应文件的审查中状态设置为 0（不在审查中）</li>
     * </ul>
     * 
     * @param fileId 电路图文件 ID
     */
    private void discardAllAuditData(Long fileId) {
        // 获取相关的服务实例
        CircuitReviewResultAuditService circuitReviewResultAuditService = SpringContextUtils.getInstanceByType(CircuitReviewResultAuditService.class);
        CircuitReviewResultService circuitReviewResultService = SpringContextUtils.getInstanceByType(CircuitReviewResultService.class);
        
        // 步骤 1: 将状态为 IN_PROCESS（处理中）的审查结果详情审核记录的状态更新为 CANCELLED（已取消）
        circuitReviewResultDetailAuditService.update(Wrappers.<CircuitReviewResultDetailAudit>lambdaUpdate()
                .eq(CircuitReviewResultDetailAudit::getFileId, fileId)
                .eq(CircuitReviewResultDetailAudit::getStatus, CircuitReviewResultDetailAuditStatusEnum.IN_PROCESS)
                .set(CircuitReviewResultDetailAudit::getStatus, CircuitReviewResultDetailAuditStatusEnum.CANCELLED));

        // 步骤 2: 查询该文件的所有审查结果详情审核记录（包含所有状态的记录）
        List<CircuitReviewResultDetailAudit> reviewResultDetailAuditList = circuitReviewResultDetailAuditService.list(
                Wrappers.<CircuitReviewResultDetailAudit>lambdaQuery()
                        .eq(CircuitReviewResultDetailAudit::getFileId, fileId));

        // 步骤 3: 统计每个审查结果审核 ID 对应的详情审核记录数量
        // key: resultAuditId, value: 该 resultAuditId 对应的详情审核记录数
        Map<Long, Long> resultAuditIdAndDetailAuditCountMap = reviewResultDetailAuditList.stream()
                .collect(Collectors.groupingBy(CircuitReviewResultDetailAudit::getResultAuditId, Collectors.counting()));

        // 步骤 4: 查询该文件的所有审查结果审核记录
        List<CircuitReviewResultAudit> reviewResultAuditList = circuitReviewResultAuditService.list(
                Wrappers.<CircuitReviewResultAudit>lambdaQuery()
                        .eq(CircuitReviewResultAudit::getFileId, fileId));

        // 准备两个集合：一个用于存储需要删除的记录 ID，一个用于存储需要更新的记录对象
        List<Long> deleteIdList = new ArrayList<>();
        List<CircuitReviewResultAudit> updateObjectList = new ArrayList<>();

        // 步骤 5: 遍历所有审查结果审核记录，根据对应的详情审核数量决定是删除还是更新
        for (CircuitReviewResultAudit circuitReviewResultAudit : reviewResultAuditList) {
            // 如果该审查结果审核在统计 map 中存在（即有对应的详情审核记录）
            if(resultAuditIdAndDetailAuditCountMap.containsKey(circuitReviewResultAudit.getId())) {
                Long detailAuditCount = resultAuditIdAndDetailAuditCountMap.get(circuitReviewResultAudit.getId());
                // 如果详情审核数量大于 0，说明还有有效的详情审核记录，需要标记为审核完成
                if(detailAuditCount > 0) {
                    circuitReviewResultAudit.setIsAuditFinished(1);        // 标记审核完成
                    circuitReviewResultAudit.setIsAdminAuditFinished(1);   // 标记管理员审核完成
                    circuitReviewResultAudit.setIsExpertAuditFinished(1);  // 标记专家审核完成
                    updateObjectList.add(circuitReviewResultAudit);
                }
                else {
                    // 详情审核数量为 0，说明没有有效的详情审核记录，需要删除
                    deleteIdList.add(circuitReviewResultAudit.getId());
                }
            }
            else {
                // 如果该审查结果审核在统计 map 中不存在，说明没有对应的详情审核记录，需要删除
                deleteIdList.add(circuitReviewResultAudit.getId());
            }
        }

        // 步骤 6: 批量删除需要删除的审查结果审核记录
        if (CollectionUtils.isNotEmpty(deleteIdList)) {
            circuitReviewResultAuditService.remove(Wrappers.<CircuitReviewResultAudit>lambdaUpdate()
                    .in(CircuitReviewResultAudit::getId, deleteIdList));
        }
        
        // 步骤 7: 批量更新需要更新的审查结果审核记录
        if(CollectionUtils.isNotEmpty(updateObjectList)) {
            circuitReviewResultAuditService.updateBatchById(updateObjectList);
        }

        // 步骤 8: 将该文件的审查中状态设置为 0（不在审查中），表示可以开始新的审查
        circuitReviewResultService.update(Wrappers.<CircuitReviewResult>lambdaUpdate()
                .eq(CircuitReviewResult::getFileId, fileId)
                .set(CircuitReviewResult::getIsInAudit, 0));
    }

    /**
     * 验证新上传的文件是否为指定文件版本的新版本
     * 
     * @param latestVersion 最新的文件版本
     * @param newFile 新上传的文件
     */
    private void validateFileVersion(CircuitFileVersionVO latestVersion, MultipartFile newFile) {
        try {
            // 构造最新版本文件的信息
            FileVO latestFileVO = new FileVO();
            latestFileVO.setFileId(latestVersion.getMinioId());
            latestFileVO.setFileName(latestVersion.getFileName());
            
            // 获取最新版本文件的内容行集合
            Set<String> latestFileLines = getFileGGXHLines(storageService.downloadFileToStream(latestFileVO));
            
            // 获取新上传文件的内容行集合
            Set<String> newFileLines = getFileGGXHLines(newFile.getInputStream());
            
            // 如果最新版本文件没有GGXH行，则跳过验证
            if (latestFileLines.isEmpty()) {
                return;
            }
            
            // 计算相同的GGXH行数
            long commonLines = latestFileLines.stream()
                    .filter(newFileLines::contains)
                    .count();
            
            // 如果相同的行数小于最新版本GGXH行数的50%，则认为不是同一文件的新版本
            if (commonLines < latestFileLines.size() * 0.5) {
                throw new KnowException("新上传电路图不是该图的新版本，请上传该图的新版本文件");
            }
        } catch (IOException e) {
            log.error("验证文件版本时发生IO异常", e);
            throw new KnowException("文件验证失败");
        }
    }
    
    /**
     * 从输入流中提取以GGXH开头的行
     * 
     * @param inputStream 文件输入流
     * @return 以GGXH开头的行的集合
     * @throws IOException IO异常
     */
    private Set<String> getFileGGXHLines(InputStream inputStream) throws IOException {
        // 将输入流转换为字节数组，以便多次读取
        byte[] bytes = inputStream.readAllBytes();
        
        // 先尝试使用UTF-16 LE编码读取
        Set<String> lines = getFileGGXHLinesWithCharset(new ByteArrayInputStream(bytes), StandardCharsets.UTF_16LE);
        
        // 如果结果为空，再尝试使用UTF-8编码读取
        if (lines.isEmpty()) {
            lines = getFileGGXHLinesWithCharset(new ByteArrayInputStream(bytes), StandardCharsets.UTF_8);
        }
        
        return lines;
    }
    
    /**
     * 使用指定字符集从输入流中提取以GGXH开头的行
     * 
     * @param inputStream 文件输入流
     * @param charset 字符集
     * @return 以GGXH开头的行的集合
     * @throws IOException IO异常
     */
    private Set<String> getFileGGXHLinesWithCharset(InputStream inputStream, java.nio.charset.Charset charset) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charset))) {
            return reader.lines()
                    .filter(line -> line.startsWith("'GGXH"))
                    .collect(Collectors.toSet());
        }
    }

    @Override
    public CircuitFileVersionVO createCircuitFileVersion(String minioId, String fileName, String fileOriginName, Long fileId, FileSecretLevelEnum fileSecretLevelEnum) {
        List<CircuitFileVersion> fileVersionList = this.getBaseMapper().getAllFileVersionByFileId(fileId);
        CircuitFileVersion circuitFileVersion = new CircuitFileVersion();
        circuitFileVersion.setFileId(fileId);
        circuitFileVersion.setMinioId(minioId);
        int fileVersion = fileVersionList.size() + 1;
        circuitFileVersion.setFileVersion(fileVersion);
        circuitFileVersion.setFileName(getFileVersionName(fileName, fileVersion));
        circuitFileVersion.setFileOriginName(fileOriginName);
        circuitFileVersion.setSecretLevel(fileSecretLevelEnum);
        circuitFileVersion.setOwnerId(HttpRequestUtil.getUserId());
        circuitFileVersion.setDepartmentId(HttpRequestUtil.getDepartmentId());
        CircuitFileVersion savedCircuitFileVersion = this.saveAndReturnObject(circuitFileVersion);
        return objectMapper.convertValue(savedCircuitFileVersion, CircuitFileVersionVO.class);
    }

    private String getFileVersionName(String fileName, int fileVersion) {
        // 安全的实现方式
        int lastDotIndex = fileName.lastIndexOf(".");
        String baseName, extension;

        if (lastDotIndex == -1) {
            // 没有扩展名的情况
            baseName = fileName;
            extension = "";
        } else {
            // 有扩展名的情况
            baseName = fileName.substring(0, lastDotIndex);
            extension = fileName.substring(lastDotIndex);
        }

        return baseName + "_v" + fileVersion + extension;
    }

    @Override
    public List<CircuitFileVersionVO> getCircuitFileVersionVOById(Long fileId) {
        CircuitFileVO circuitFileVO = circuitFileService.getCircuitFileVOById(fileId);
        if (circuitFileVO != null) {
            // 使用 Mapper 接口直接查询，包含部门名称和所有者名称
            return this.getBaseMapper().getCircuitFileVersionVOListByFileId(fileId);
        } else {
            throw new KnowException("电路图文件不存在");
        }
    }

    @Override
    public CircuitFileVersionVO getCircuitFileVersionVOByVersionId(Long fileVersionId) {
        List<CircuitFileVersion> fileVersions = this.list(Wrappers.<CircuitFileVersion>lambdaQuery().eq(CircuitFileVersion::getId, fileVersionId).eq(CircuitFileVersion::getIsRecycle, 0));
        if(CollectionUtils.isNotEmpty(fileVersions)) {
            return objectMapper.convertValue(fileVersions.get(0), CircuitFileVersionVO.class);
        }
        else {
            throw new KnowException(String.format("电路图文件不存在, versionId: %s", fileVersionId));
        }
    }

    @Override
    public List<CircuitFileVersionAndResultVO> getCircuitFileVersionAndResultVOById(Long fileId) {
        List<CircuitFileVersion> fileVersionList = this.lambdaQuery().eq(CircuitFileVersion::getFileId, fileId).eq(CircuitFileVersion::getIsRecycle, 0).orderByDesc(CircuitFileVersion::getFileVersion).list();
        List<Long> fileVersionId = fileVersionList.stream().map(CircuitFileVersion::getId).toList();
        CircuitReviewResultService circuitReviewResultService = SpringContextUtils.getInstanceByType(CircuitReviewResultService.class);
        List<CircuitReviewResult> circuitReviewResults = circuitReviewResultService.getResultByFileVersionIdList(fileVersionId);
        Map<Long, List<CircuitReviewResult>> resultMap = circuitReviewResults.stream().collect(Collectors.groupingBy(CircuitReviewResult::getFileVersionId));

        List<CircuitFileVersionAndResultVO> result = new ArrayList<>();
        for (CircuitFileVersion circuitFileVersion : fileVersionList) {
            if(resultMap.containsKey(circuitFileVersion.getId())) {
                List<CircuitReviewResult> circuitReviewResultList = resultMap.get(circuitFileVersion.getId());
                for (CircuitReviewResult circuitReviewResult : circuitReviewResultList) {
                    CircuitFileVersionAndResultVO circuitFileVersionAndResultVO = objectMapper.convertValue(circuitFileVersion, CircuitFileVersionAndResultVO.class);
                    circuitFileVersionAndResultVO.setFileVersionId(circuitFileVersion.getId());
                    circuitFileVersionAndResultVO.setFileVersion(circuitFileVersion.getFileVersion());
                    circuitFileVersionAndResultVO.setResultId(circuitReviewResult.getId());
                    circuitFileVersionAndResultVO.setCheckPoints(circuitReviewResult.getCheckPoints());
                    circuitFileVersionAndResultVO.setPassCheckPoints(circuitReviewResult.getPassCheckPoints());
                    int failCheckPoints = circuitReviewResult.getCheckPoints() - circuitReviewResult.getPassCheckPoints();
                    // 确保问题点数不会为负数
                    if (failCheckPoints < 0) {
                        failCheckPoints = 0;
                    }
                    circuitFileVersionAndResultVO.setFailCheckPoints(failCheckPoints);
                    circuitFileVersionAndResultVO.setClosedFailCheckPoints(circuitReviewResult.getClosedFailCheckPoints());
                    circuitFileVersionAndResultVO.setTotalFailCheckPoints(circuitReviewResult.getTotalFailCheckPoints());
                    circuitFileVersionAndResultVO.setPassRate(circuitReviewResult.getPassRate());
                    circuitFileVersionAndResultVO.setIsClosedLoop(circuitReviewResult.getIsClosedLoop());
                    circuitFileVersionAndResultVO.setReviewTime(circuitReviewResult.getReviewTime());
                    result.add(circuitFileVersionAndResultVO);
                }
            }
            else {
                CircuitFileVersionAndResultVO circuitFileVersionAndResultVO = objectMapper.convertValue(circuitFileVersion, CircuitFileVersionAndResultVO.class);
                circuitFileVersionAndResultVO.setFileVersionId(circuitFileVersion.getId());
                circuitFileVersionAndResultVO.setFileVersion(circuitFileVersion.getFileVersion());
                result.add(circuitFileVersionAndResultVO);
            }
        }
        return result;
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void removeCircuitFilesWithTransaction(List<DeleteDTO> removeCircuitFiles) {
        removeCircuitFiles.forEach(deleteDTO -> pulverizeFileVersion(deleteDTO.getId()));
    }

    /**
     * 单文件版本 粉碎逻辑
     *
     * @param id 文件id
     */
    private void pulverizeFileVersion(Long id){
        CircuitFileVersion fileVersion = Optional.ofNullable(this.getById(id)).orElseThrow(() -> new KnowException("文件不存在"));
        storageService.deleteFile(fileVersion.getMinioId());//删除文件Minio上的文件.
        CircuitReviewResultService circuitReviewResultService = SpringContextUtils.getInstanceByType(CircuitReviewResultService.class);
        List<CircuitReviewResult>  circuitReviewResultList = circuitReviewResultService.lambdaQuery().eq(CircuitReviewResult::getFileVersionId, fileVersion.getId()).ne(CircuitReviewResult::getStatus, CircuitReviewStatusEnum.FAILED.getValue()).list();
        if (CollectionUtils.isNotEmpty(circuitReviewResultList)) {
            //有审查结果,放进回收站
            fileVersion.setIsRecycle(1);
            this.updateByIDAndReturnObject(fileVersion);
        }
        else {
            removeByIdWithFill(fileVersion); //未审查的文件, 直接删除文件记录
        }
        //如果当前Version是这个主文件下的最后一个Version,删除主文件
        if (this.lambdaQuery().eq(CircuitFileVersion::getFileId, fileVersion.getFileId()).eq(CircuitFileVersion::getIsRecycle, 0).count() == 0) {
            ChangeFileRecycleStatusDTO changeFileRecycleStatusDTO = new ChangeFileRecycleStatusDTO();
            changeFileRecycleStatusDTO.setFileIdList(List.of(fileVersion.getFileId()));
            changeFileRecycleStatusDTO.setIsRecycle(1);
            circuitFileService.changeCircuitFileRecycle(changeFileRecycleStatusDTO);
        }
    }
}

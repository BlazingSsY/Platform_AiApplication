package com.starmol.logicreview.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starmol.logicreview.bean.bo.ResponseDataBO;
import com.starmol.logicreview.bean.vo.SourceCodeFileVO;
import com.starmol.logicreview.bean.vo.SourceCodeFileVersionAndResultVO;
import com.starmol.logicreview.bean.vo.SourceCodeFileVersionVO;
import com.starmol.logicreview.constant.ReviewStatusEnum;
import com.starmol.logicreview.constant.FileSecretLevelEnum;
import com.starmol.logicreview.exception.KnowException;
import com.starmol.logicreview.model.base.DeleteDTO;
import com.starmol.logicreview.model.codereview.SourceCodeFile;
import com.starmol.logicreview.model.codereview.SourceCodeFileVersion;
import com.starmol.logicreview.model.codereview.SourceCodeReviewResult;
import com.starmol.logicreview.repository.codereview.SourceCodeFileVersionMapper;
import com.starmol.logicreview.service.base.impl.BaseCascadeServiceImpl;
import com.starmol.logicreview.service.SourceCodeFileService;
import com.starmol.logicreview.service.SourceCodeFileVersionService;
import com.starmol.logicreview.service.SourceCodeReviewResultService;
import com.starmol.logicreview.service.feign.CodeReviewClient;
import com.starmol.logicreview.service.common.StorageService;
import com.starmol.logicreview.utils.HttpRequestUtil;
import com.starmol.logicreview.utils.SpringContextUtils;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Lazy
public class SourceCodeFileVersionServiceImpl extends BaseCascadeServiceImpl<SourceCodeFileVersionMapper, SourceCodeFileVersion> implements SourceCodeFileVersionService {
    private final ObjectMapper objectMapper;
    private final StorageService storageService;
    private final CodeReviewClient codeReviewClient;
    private final SourceCodeFileService sourceCodeFileService;
    private final SourceCodeReviewResultService sourceCodeReviewResultService;

    @Override
    public SourceCodeFileVersionVO createSourceCodeFileVersion(MultipartFile file, Long fileId, FileSecretLevelEnum fileSecretLevelEnum, String compatibleModels, String productModel, String productName, String configName, String codeFileVersion) {
        if (file != null && file.getOriginalFilename() != null) {
            SourceCodeFile sourceCodeFile = sourceCodeFileService.getById(fileId);
            if (sourceCodeFile != null) {
                String reviewId;
                //调用用户提供的API,代替保存到Minio
                ResponseDataBO<Map<String, String>> uploadResponse = codeReviewClient.uploadFile(file,HttpRequestUtil.getDebugId(),HttpRequestUtil.getLoginName(), sourceCodeFile.getMinioId());
                if (!uploadResponse.getCode().equals(200)) {
                    // 上传失败，更新状态并抛出异常
                    throw new KnowException("文件上传到第三方服务,调用接口失败: " + uploadResponse.getMessage());
                }
                else {
                    reviewId = uploadResponse.getData().get("reviewId").toString();
                }
                //用新的reviewId更新文件和文件版本
                sourceCodeFile.setMinioId(reviewId);
                if (StringUtils.isNotEmpty(compatibleModels)) {
                    sourceCodeFile.setCompatibleModels(compatibleModels);
                }
                if (StringUtils.isNotEmpty(productModel)) {
                    sourceCodeFile.setProductModel(productModel);
                }
                if (StringUtils.isNotEmpty(productName)) {
                    sourceCodeFile.setProductName(productName);
                }
                if (StringUtils.isNotEmpty(configName)) {
                    sourceCodeFile.setConfigName(configName);
                }
                if (StringUtils.isNotEmpty(codeFileVersion)) {
                    sourceCodeFile.setCodeFileVersion(codeFileVersion);
                }
                sourceCodeFileService.updateByIDAndReturnObject(sourceCodeFile);
                SourceCodeFileVersion sourceCodeFileVersion = this.getOne(Wrappers.<SourceCodeFileVersion>lambdaQuery().eq(SourceCodeFileVersion::getFileId, fileId).last("LIMIT 1"));
                sourceCodeFileVersion.setMinioId(reviewId);
                SourceCodeFileVersion returrSourceCodeFileVersion = this.updateByIDAndReturnObject(sourceCodeFileVersion);
                //根据用户需求,在文件被覆盖后,审查结果要显示和新创建的一样,因此删除原来的审查结果
                sourceCodeReviewResultService.removeResultByFileIdWithoutCheckPermission(fileId);
                return objectMapper.convertValue(returrSourceCodeFileVersion, SourceCodeFileVersionVO.class);
            } else {
                throw new KnowException("逻辑文件不存在");
            }
        } else {
            throw new KnowException("无效的逻辑文件");
        }
    }

    @Override
    public SourceCodeFileVersionVO createSourceCodeFileVersion(String minioId, String fileName, String fileOriginName, Long fileId, FileSecretLevelEnum fileSecretLevelEnum) {
        List<SourceCodeFileVersion> fileVersionList = this.getBaseMapper().getAllFileVersionByFileId(fileId);
        SourceCodeFileVersion sourceCodeFileVersion = new SourceCodeFileVersion();
        sourceCodeFileVersion.setFileId(fileId);
        sourceCodeFileVersion.setMinioId(minioId);
        int fileVersion = fileVersionList.size() + 1;
        sourceCodeFileVersion.setFileVersion(fileVersion);
        sourceCodeFileVersion.setFileName(fileName.substring(0, fileName.lastIndexOf(".")) + "_v" + fileVersion + fileName.substring(fileName.lastIndexOf(".")));
        sourceCodeFileVersion.setFileOriginName(fileOriginName);
        sourceCodeFileVersion.setSecretLevel(fileSecretLevelEnum);
        sourceCodeFileVersion.setOwnerId(HttpRequestUtil.getUserId());
        sourceCodeFileVersion.setDepartmentId(HttpRequestUtil.getDepartmentId());
        SourceCodeFileVersion savedSourceCodeFileVersion = this.saveAndReturnObject(sourceCodeFileVersion);
        return objectMapper.convertValue(savedSourceCodeFileVersion, SourceCodeFileVersionVO.class);
    }

    @Override
    public List<SourceCodeFileVersionVO> getSourceCodeFileVersionVOById(Long fileId) {
        SourceCodeFileVO sourceCodeFileVO = sourceCodeFileService.getSourceCodeFileVOById(fileId);
        if (sourceCodeFileVO != null) {
            List<SourceCodeFileVersion> fileVersionList = this.lambdaQuery().eq(SourceCodeFileVersion::getFileId, fileId).orderByDesc(SourceCodeFileVersion::getFileVersion).list();
            return fileVersionList.stream().map(fileVersion -> objectMapper.convertValue(fileVersion, SourceCodeFileVersionVO.class)).toList();
        } else {
            throw new KnowException("逻辑文件不存在");
        }
    }

    @Override
    public SourceCodeFileVersionVO getSourceCodeFileVersionVOByVersionId(Long fileVersionId) {
        List<SourceCodeFileVersion> fileVersions = this.list(Wrappers.<SourceCodeFileVersion>lambdaQuery().eq(SourceCodeFileVersion::getId, fileVersionId));
        if(CollectionUtils.isNotEmpty(fileVersions)) {
            return objectMapper.convertValue(fileVersions.get(0), SourceCodeFileVersionVO.class);
        }
        else {
            throw new KnowException(String.format("逻辑文件不存在, versionId: %s", fileVersionId));
        }
    }

    @Override
    public List<SourceCodeFileVersionAndResultVO> getSourceCodeFileVersionAndResultVOById(Long fileId) {
        List<SourceCodeFileVersion> fileVersionList = this.lambdaQuery().eq(SourceCodeFileVersion::getFileId, fileId).orderByDesc(SourceCodeFileVersion::getFileVersion).list();
        List<Long> fileVersionId = fileVersionList.stream().map(SourceCodeFileVersion::getId).toList();
        List<SourceCodeReviewResult> sourceCodeReviewResults = sourceCodeReviewResultService.getResultByFileIdList(fileVersionId);
        Map<Long, List<SourceCodeReviewResult>> resultMap = sourceCodeReviewResults.stream().collect(Collectors.groupingBy(SourceCodeReviewResult::getFileVersionId));

        List<SourceCodeFileVersionAndResultVO> result = new ArrayList<>();
        for (SourceCodeFileVersion sourceCodeFileVersion : fileVersionList) {
            if(resultMap.containsKey(sourceCodeFileVersion.getId())) {
                List<SourceCodeReviewResult> sourceCodeReviewResultList = resultMap.get(sourceCodeFileVersion.getId());
                for (SourceCodeReviewResult sourceCodeReviewResult : sourceCodeReviewResultList) {
                    SourceCodeFileVersionAndResultVO sourceCodeFileVersionAndResultVO = objectMapper.convertValue(sourceCodeFileVersion, SourceCodeFileVersionAndResultVO.class);
                    sourceCodeFileVersionAndResultVO.setFileVersionId(sourceCodeFileVersion.getId());
                    sourceCodeFileVersionAndResultVO.setFileVersion(sourceCodeFileVersion.getFileVersion());
                    sourceCodeFileVersionAndResultVO.setResultId(sourceCodeReviewResult.getId());
                    sourceCodeFileVersionAndResultVO.setCheckPoints(sourceCodeReviewResult.getCheckPoints());
                    sourceCodeFileVersionAndResultVO.setPassCheckPoints(sourceCodeReviewResult.getPassCheckPoints());
                    sourceCodeFileVersionAndResultVO.setPassRate(sourceCodeReviewResult.getPassRate());
                    sourceCodeFileVersionAndResultVO.setIsClosedLoop(sourceCodeReviewResult.getIsClosedLoop());
                    sourceCodeFileVersionAndResultVO.setReviewTime(sourceCodeReviewResult.getReviewTime());
                    result.add(sourceCodeFileVersionAndResultVO);
                }
            }
            else {
                SourceCodeFileVersionAndResultVO sourceCodeFileVersionAndResultVO = objectMapper.convertValue(sourceCodeFileVersion, SourceCodeFileVersionAndResultVO.class);
                sourceCodeFileVersionAndResultVO.setFileVersionId(sourceCodeFileVersion.getId());
                sourceCodeFileVersionAndResultVO.setFileVersion(sourceCodeFileVersion.getFileVersion());
                result.add(sourceCodeFileVersionAndResultVO);
            }
        }
        return result;
    }


    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void removeSourceCodeFilesWithTransaction(List<DeleteDTO> removeSourceCodeFiles) {
        removeSourceCodeFiles.forEach(deleteDTO -> pulverizeFileVersion(deleteDTO.getId()));
    }

    /**
     * 单文件版本 粉碎逻辑
     *
     * @param id 文件id
     * @return 删除结果
     */
    private void pulverizeFileVersion(Long id){
        SourceCodeFileVersion file = Optional.ofNullable(this.getById(id)).orElseThrow(() -> new KnowException("文件不存在"));
        storageService.deleteFile(file.getMinioId());//删除文件Minio上的文件.
        SourceCodeReviewResultService sourceCodeReviewResultService = SpringContextUtils.getInstanceByType(SourceCodeReviewResultService.class);
        List<SourceCodeReviewResult>  sourceCodeReviewResultList = sourceCodeReviewResultService.lambdaQuery().eq(SourceCodeReviewResult::getFileId, file.getId()).ne(SourceCodeReviewResult::getStatus, ReviewStatusEnum.FAILED.getValue()).list();
        if (CollectionUtils.isNotEmpty(sourceCodeReviewResultList)) {
            //有审查结果,放进回收站
            file.setIsRecycle(1);
            this.updateByIDAndReturnObject(file);
        }
        else {
            removeByIdWithFill(file); //未审查的文件, 直接删除文件记录
        }
        //如果当前Version是这个主文件下的最后一个Version,删除主文件
        if (this.lambdaQuery().eq(SourceCodeFileVersion::getFileId, file.getFileId()).count() == 0) {
            sourceCodeFileService.removeById(file.getFileId(),true);
        }
    }

    @Override
    public Map<String, String> getExportLink(String fileId,  String fileName){
        ResponseDataBO<Map<String, String>> responseWrapper = codeReviewClient.getExportLink(fileId,HttpRequestUtil.getDebugId(),HttpRequestUtil.getLoginName());
        if (!responseWrapper.getCode().equals(200)) {
            // 上传失败，更新状态并抛出异常
            throw new KnowException("从第三方服务获取下载连接,调用接口失败: " + responseWrapper.getMessage());
        }
        else {
            return responseWrapper.getData();
        }
    }
}

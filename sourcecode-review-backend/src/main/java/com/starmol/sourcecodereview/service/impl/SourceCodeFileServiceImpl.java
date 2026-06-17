package com.starmol.sourcecodereview.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starmol.sourcecodereview.bean.bo.PulverizeFileResultBO;
import com.starmol.sourcecodereview.bean.bo.ResponseDataBO;
import com.starmol.sourcecodereview.bean.dto.ChangeFileRecycleStatusDTO;
import com.starmol.sourcecodereview.bean.dto.DepartmentDTO;
import com.starmol.sourcecodereview.bean.dto.SourceCodeFileDTO;
import com.starmol.sourcecodereview.bean.vo.FileExistCheckResultVO;
import com.starmol.sourcecodereview.bean.vo.SourceCodeFileDetailVO;
import com.starmol.sourcecodereview.bean.vo.SourceCodeFileVO;
import com.starmol.sourcecodereview.common.ResponseWrapper;
import com.starmol.sourcecodereview.common.UserMetaData;
import com.starmol.sourcecodereview.constant.FileSecretLevelEnum;
import com.starmol.sourcecodereview.constant.ReviewStatusEnum;
import com.starmol.sourcecodereview.constant.SysRoleTypeEnum;
import com.starmol.sourcecodereview.exception.KnowException;
import com.starmol.sourcecodereview.model.base.DeleteDTO;
import com.starmol.sourcecodereview.model.codereview.SourceCodeFile;
import com.starmol.sourcecodereview.model.codereview.SourceCodeFileVersion;
import com.starmol.sourcecodereview.model.codereview.SourceCodeReviewResult;
import com.starmol.sourcecodereview.repository.codereview.SourceCodeFileMapper;
import com.starmol.sourcecodereview.service.SourceCodeFileService;
import com.starmol.sourcecodereview.service.SourceCodeFileVersionService;
import com.starmol.sourcecodereview.service.SourceCodeReviewResultService;
import com.starmol.sourcecodereview.service.base.impl.BaseCascadeServiceImpl;
import com.starmol.sourcecodereview.service.common.StorageService;
import com.starmol.sourcecodereview.service.feign.CodeReviewClient;
import com.starmol.sourcecodereview.service.feign.UserServiceClient;
import com.starmol.sourcecodereview.utils.HttpRequestUtil;
import com.starmol.sourcecodereview.utils.SpringContextUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 代码文件服务实现类
 *
 * @author system
 * @date 2025-01-07
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Lazy
public class SourceCodeFileServiceImpl extends BaseCascadeServiceImpl<SourceCodeFileMapper, SourceCodeFile> implements SourceCodeFileService {

    private final ObjectMapper objectMapper;
    private final CodeReviewClient codeReviewClient;
    private final StorageService storageService;
    private final UserServiceClient userServiceClient;


    @Override
    public SourceCodeFileVO createSourceCodeFile(SourceCodeFileDTO sourceCodeFileDTO) {
        SourceCodeFile sourceCodeFile = objectMapper.convertValue(sourceCodeFileDTO, SourceCodeFile.class);
        sourceCodeFile.setFileSaveName(String.format("%d_%d_%s", HttpRequestUtil.getDepartmentId(), HttpRequestUtil.getUserId(), sourceCodeFile.getFileName()));
        sourceCodeFile.setOwnerId(HttpRequestUtil.getUserId());
        sourceCodeFile.setDepartmentId(HttpRequestUtil.getDepartmentId());
        SourceCodeFile savedSourceCodeFile = this.saveAndReturnObject(sourceCodeFile);
        return convertToVO(savedSourceCodeFile);
    }

    @Override
    public FileExistCheckResultVO checkFileExist(String fileName) {
        FileExistCheckResultVO fileExistCheckResultVO = new FileExistCheckResultVO();
        List<SourceCodeFile> fileList = this.lambdaQuery().eq(SourceCodeFile::getFileName, fileName).eq(SourceCodeFile::getIsRecycle, 0).eq(SourceCodeFile::getOwnerId, HttpRequestUtil.getUserId()).list();
        if (CollectionUtils.isEmpty(fileList)) {
            fileExistCheckResultVO.setExist(false);
        } else {
            fileExistCheckResultVO.setExist(true).setFileId(fileList.get(0).getId());
        }
        //20260531根据用户要求:同一账号，上传同名项目，如果存在已粉碎项目，提示[存在同名闭环项目，不可重复上传】
        List<SourceCodeFile> pulverizedFileList = this.lambdaQuery().eq(SourceCodeFile::getFileName, fileName).eq(SourceCodeFile::getIsRecycle, 1).eq(SourceCodeFile::getOwnerId, HttpRequestUtil.getUserId()).list();
        if (CollectionUtils.isNotEmpty(pulverizedFileList)) {
            fileExistCheckResultVO.setPulverized(true);
        }
        return fileExistCheckResultVO;
    }

    @Override
    public SourceCodeFileVO createSourceCodeFile(MultipartFile file, FileSecretLevelEnum fileSecretLevelEnum, String compatibleModels, String productModel, String productName, String configName, String codeFileVersion, Integer isTestFile) {
        //上传前再次检查同名文件是否存在
        FileExistCheckResultVO fileExistCheckResultVO = checkFileExist(file.getOriginalFilename());
        if(fileExistCheckResultVO.getExist()) {
            throw new KnowException("同名的文件已存在,请刷新后重试！");
        }
        String originalFilename = file.getOriginalFilename();
        if(isTestFile != null) {
            if(!isTestFile.equals(0)) {
                if(!originalFilename.startsWith("test-")) {
                    throw new KnowException("设置为测试文件后,文件名应以'test-'开头！");
                }
            }
        }

        String reviewId = null;
        //调用用户提供的API,代替保存到Minio
        ResponseDataBO<Map<String, String>> uploadResponse = codeReviewClient.uploadFile(file, HttpRequestUtil.getDebugId(),HttpRequestUtil.getLoginName(), null);
        if (!uploadResponse.getCode().equals(200)) {
            // 上传失败，更新状态并抛出异常
            throw new KnowException("文件上传到第三方服务,调用接口失败: " + uploadResponse.getMessage());
        }
        else {
            reviewId = uploadResponse.getData().get("reviewId").toString();
        }
        if (reviewId != null) {  // 创建新的文件
            SourceCodeFile sourceCodeFile = new SourceCodeFile();
            sourceCodeFile.setMinioId(reviewId);
            sourceCodeFile.setSecretLevel(fileSecretLevelEnum);
            sourceCodeFile.setCompatibleModels(compatibleModels);
            sourceCodeFile.setProductModel(productModel);
            sourceCodeFile.setProductName(productName);
            sourceCodeFile.setConfigName(configName);
            sourceCodeFile.setCodeFileVersion(codeFileVersion);
            sourceCodeFile.setFileName(file.getOriginalFilename());
            sourceCodeFile.setFileSaveName(String.format("%d_%d_%s", HttpRequestUtil.getDepartmentId(), HttpRequestUtil.getUserId(), file.getOriginalFilename()));
            sourceCodeFile.setOwnerId(HttpRequestUtil.getUserId());
            sourceCodeFile.setDepartmentId(HttpRequestUtil.getDepartmentId());
            SourceCodeFile savedSourceCodeFile =this.saveAndReturnObject(sourceCodeFile);
            SourceCodeFileVersionService sourceCodeFileVersionService = SpringContextUtils.getInstanceByType(SourceCodeFileVersionService.class);
            sourceCodeFileVersionService.createSourceCodeFileVersion(reviewId, file.getOriginalFilename(), file.getOriginalFilename(), savedSourceCodeFile.getId(), fileSecretLevelEnum);
            return convertToVO(savedSourceCodeFile);
        } else {
            throw new KnowException("上传文件失败");
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public PulverizeFileResultBO removeSourceCodeFilesWithTransaction(DeleteDTO removeSourceCodeFile) {
       return pulverizeAllFileVersionAndSourceCodeFile(removeSourceCodeFile.getId());
    }

    /**
     * 代码文件的逻辑--粉碎文件所有物理版本
     *
     * @param id 文件id
     * @return 删除结果
     */
    private PulverizeFileResultBO pulverizeAllFileVersionAndSourceCodeFile(Long id){
        //根据Id获取代码文件的所有物理版本
        SourceCodeFileVersionService sourceCodeFileVersionService = SpringContextUtils.getInstanceByType(SourceCodeFileVersionService.class);
        List<SourceCodeFileVersion> fileVersionList = sourceCodeFileVersionService.lambdaQuery().eq(SourceCodeFileVersion::getFileId, id).eq(SourceCodeFileVersion::getIsRecycle, 0).list();
        List<Long> fileVersionIdList =fileVersionList.stream().map(SourceCodeFileVersion::getId).collect(Collectors.toList()) ;
        SourceCodeReviewResultService sourceCodeReviewResultService = SpringContextUtils.getInstanceByType(SourceCodeReviewResultService.class);
        List<SourceCodeReviewResult>  sourceCodeReviewResultList = CollectionUtils.isNotEmpty(fileVersionIdList) ? sourceCodeReviewResultService.lambdaQuery().in(SourceCodeReviewResult::getFileVersionId , fileVersionIdList).ne(SourceCodeReviewResult::getStatus, ReviewStatusEnum.FAILED.getValue()).list() : Collections.emptyList();
        List<Long> haveReviewResultFileVersionIdList = sourceCodeReviewResultList.stream().map(SourceCodeReviewResult::getFileVersionId).distinct().toList();
        //检分有审查结果的文件版本和无审查结果的文件版本
        AtomicReference<Boolean> haveReviewResult = new AtomicReference<>(false); //当前文件是否·
        List<SourceCodeFileVersion>  haveReviewResultFileVersionList = new ArrayList<>();
        List<SourceCodeFileVersion>  haveNoReviewResultFileVersionList = new ArrayList<>();
        fileVersionList.forEach(file -> {
            if (haveReviewResultFileVersionIdList.contains(file.getId())) {
                //有审查结果,放进回收站
                file.setIsRecycle(1);
                haveReviewResult.set(true);
                haveReviewResultFileVersionList.add(file);
            }
            else {
                haveNoReviewResultFileVersionList.add(file);
            }
        });

        //调用第三方的API,代替删除文件
        ResponseDataBO<Object>  responseWrapper = codeReviewClient.deleteFile(this.getById( id).getMinioId(),HttpRequestUtil.getDebugId(),HttpRequestUtil.getLoginName());
        if (!responseWrapper.getCode().equals(200)) { // 第三方接口调用失败, 更新状态审查结果为失败
            log.error("粉碎文件失败: {}", responseWrapper.getMessage());
            throw new KnowException("粉碎文件,调用接口失败: " + responseWrapper.getMessage());
        }
        else{
            PulverizeFileResultBO pulverizeFileResultBO = objectMapper.convertValue(responseWrapper.getData(), PulverizeFileResultBO.class);
            if (pulverizeFileResultBO.getDelete().equals("1")) {

                sourceCodeFileVersionService.updateBatchById(haveReviewResultFileVersionList);   //有审查结果,放进回收站 -- 一次性更新
                sourceCodeFileVersionService.removeBatchByIds(haveNoReviewResultFileVersionList); //无审查结果的文件, 直接删除文件记录

                if (haveReviewResult.get()) { // 有审查结果, 将代码件放进回收站;无审查结果的直接删除
                    ChangeFileRecycleStatusDTO changeFileRecycleStatusDTO = new ChangeFileRecycleStatusDTO();
                    changeFileRecycleStatusDTO.setFileIdList(List.of(id));
                    changeFileRecycleStatusDTO.setIsRecycle(1);
                    this.changeSourceCodeFileRecycleStatus(changeFileRecycleStatusDTO); //将代码件放进回收站
                }
                else {
                    this.removeById(id, true); //删除代码文件记录
                }
            }
            return pulverizeFileResultBO;
        }
    }


    @Override
    public SourceCodeFileVO updateSourceCodeFile(Long id, SourceCodeFile sourceCodeFile) {
        sourceCodeFile.setId(id);
        SourceCodeFile updatedSourceCodeFile = this.updateByIDAndReturnObject(sourceCodeFile);
        return convertToVO(updatedSourceCodeFile);
    }

    @Override
    public Boolean changeSourceCodeFileRecycleStatus(ChangeFileRecycleStatusDTO changeFileRecycleStatusDTO) {
        if (CollectionUtils.isNotEmpty(changeFileRecycleStatusDTO.getFileIdList())) {
            return this.update(Wrappers.<SourceCodeFile>lambdaUpdate()
                    .in(SourceCodeFile::getId, changeFileRecycleStatusDTO.getFileIdList())
                    .set(SourceCodeFile::getIsRecycle, changeFileRecycleStatusDTO.getIsRecycle())
                    .set(SourceCodeFile::getUpdateDate, LocalDateTime.now())
            );
        }
        else {
            return true;
        }
    }

    @Override
    public SourceCodeFileVO getSourceCodeFileVOById(Long id) {
        SourceCodeFile sourceCodeFile = this.getById(id);
        return convertToVO(sourceCodeFile);
    }

    @Override
    public SourceCodeFileVO getSourceCodeFileVOByFileName(String fileName) {
        List<SourceCodeFile> fileList = this.lambdaQuery().eq(SourceCodeFile::getFileName, fileName).eq(SourceCodeFile::getOwnerId, HttpRequestUtil.getUserId()).list();
        if (CollectionUtils.isNotEmpty(fileList)) {
            return convertToVO(fileList.get(0));
        } else {
            throw new KnowException("代码文件不存在");
        }
    }

    @Override
    public IPage<SourceCodeFileVO> getRecycledSourceCodeFileVOPage(Page<SourceCodeFile> page, String fileName) {
        Long userId = HttpRequestUtil.getUserId();
        IPage<SourceCodeFile> sourceCodeFilePage = this.page(page,
                Wrappers.<SourceCodeFile>lambdaQuery().eq(SourceCodeFile::getIsRecycle, 1).eq(SourceCodeFile::getOwnerId, userId)
                        .like(StringUtils.isNotEmpty(fileName), SourceCodeFile::getFileName, fileName)
        );
        return sourceCodeFilePage.convert(this::convertToVO);
    }

    @Override
    public IPage<SourceCodeFileDetailVO> getSourceCodeFileDetailVOPage(Page<SourceCodeFileDetailVO> page, Long depId, Long selectedUserId, String fileName) {
        UserMetaData user = HttpRequestUtil.getUser();
        SysRoleTypeEnum userRole = user.getSysRoleType();
        if (userRole == null) {
            log.warn("用户没有角色: {}", user.getId());
            return page;
        }
        // 确定查询范围
        List<Long> departmentIds = new ArrayList<>();
        Long userId = null;
        // 检查用户角色
        boolean isNormalUser = SysRoleTypeEnum.NORMAL_USER.equals(userRole);
        boolean isDepartmentSupervisor = SysRoleTypeEnum.ORG_SUPERVISOR.equals(userRole);
        List<DepartmentDTO> departments;
        long deptStart = System.currentTimeMillis();
        if (isNormalUser) {
            // 普通用户：只查看自己上传和审查的文件
            userId = user.getId();
        } else if (isDepartmentSupervisor) {
            // 部门负责人：查看全部门（包括子部门）的文件
            if(depId != null) {
                departmentIds.add(depId);
            }
            else {
                log.info("获取部门及其子部门ID列表，部门ID: {}", user.getDepartmentId());
                ResponseWrapper<List<DepartmentDTO>> departmentsResponseWrapper = userServiceClient.getDepartmentsByIdOrFId(user.getDepartmentId());
                if(!departmentsResponseWrapper.isSucc()) {
                    log.error("获取部门及其子部门列表失败: {} 错误信息: {}", user.getDepartmentId(), departmentsResponseWrapper.getMsg());
                    departments = new ArrayList<>();
                }
                departments = departmentsResponseWrapper.getContent();
                departmentIds = departments.stream().map(DepartmentDTO::getId).collect(Collectors.toList());
            }
            if(selectedUserId != null) {
                userId = selectedUserId;
            }
        } else {
            // 部门管理员或其他角色：查看全部门（包括子部门）的文件
            if(depId != null) {
                departmentIds.add(depId);
            }
            else {
                log.info("获取部门及其子部门ID列表，部门ID: {}", user.getDepartmentId());
                ResponseWrapper<List<DepartmentDTO>> departmentsResponseWrapper = userServiceClient.getDepartmentsByIdOrFId(user.getDepartmentId());
                if(!departmentsResponseWrapper.isSucc()) {
                    log.error("获取部门及其子部门列表失败: {} 错误信息: {}", user.getDepartmentId(), departmentsResponseWrapper.getMsg());
                    departments = new ArrayList<>();
                }
                departments = departmentsResponseWrapper.getContent();
                departmentIds = departments.stream().map(DepartmentDTO::getId).collect(Collectors.toList());
            }
            if(selectedUserId != null) {
                userId = selectedUserId;
            }
        }
        return this.baseMapper.selectFileDetailPage(page, departmentIds, userId, fileName);
    }

    private SourceCodeFileVO convertToVO(SourceCodeFile sourceCodeFile) {
        if (sourceCodeFile == null) {
            return null;
        }
        return objectMapper.convertValue(sourceCodeFile, SourceCodeFileVO.class);
    }
}
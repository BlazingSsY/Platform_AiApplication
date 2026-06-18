package com.starmol.circuitreview.backend.service.circuitreview.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starmol.circuitreview.backend.bean.bo.FileCheckResult;
import com.starmol.circuitreview.backend.bean.dto.ChangeFileRecycleStatusDTO;
import com.starmol.circuitreview.backend.bean.dto.CircuitFileDTO;
import com.starmol.circuitreview.backend.bean.dto.DepartmentDTO;
import com.starmol.circuitreview.backend.bean.dto.UserDTO;
import com.starmol.circuitreview.backend.bean.vo.CircuitFileDetailVO;
import com.starmol.circuitreview.backend.bean.vo.CircuitFileVO;
import com.starmol.circuitreview.backend.bean.vo.CircuitFileVersionVO;
import com.starmol.circuitreview.backend.bean.vo.FileExistCheckResultVO;
import com.starmol.circuitreview.backend.common.ResponseWrapper;
import com.starmol.circuitreview.backend.common.UserMetaData;
import com.starmol.circuitreview.backend.constant.*;
import com.starmol.circuitreview.backend.exception.KnowException;
import com.starmol.circuitreview.backend.model.base.DeleteDTO;
import com.starmol.circuitreview.backend.model.circuitreview.CircuitFile;
import com.starmol.circuitreview.backend.model.circuitreview.CircuitFileVersion;
import com.starmol.circuitreview.backend.model.circuitreview.CircuitReviewResult;
import com.starmol.circuitreview.backend.model.circuitreview.CircuitReviewResultAudit;
import com.starmol.circuitreview.backend.repository.circuitreview.CircuitFileMapper;
import com.starmol.circuitreview.backend.service.base.impl.BaseCascadeServiceImpl;
import com.starmol.circuitreview.backend.service.circuitreview.CircuitFileService;
import com.starmol.circuitreview.backend.service.circuitreview.CircuitFileVersionService;
import com.starmol.circuitreview.backend.service.circuitreview.CircuitReviewResultService;
import com.starmol.circuitreview.backend.service.circuitreview.feign.UserServiceClient;
import com.starmol.circuitreview.backend.service.common.StorageService;
import com.starmol.circuitreview.backend.utils.FileUtils;
import com.starmol.circuitreview.backend.utils.HttpRequestUtil;
import com.starmol.circuitreview.backend.utils.SpringContextUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 电路图文件服务实现类
 *
 * @author system
 * @date 2025-01-07
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Lazy
public class CircuitFileServiceImpl extends BaseCascadeServiceImpl<CircuitFileMapper, CircuitFile> implements CircuitFileService {

    @Value("${deployment.type}")
    private Integer deploymentType;

    private final ObjectMapper objectMapper;
    private final StorageService storageService;
    private final UserServiceClient userServiceClient;


    @Override
    public CircuitFileVO createCircuitFile(CircuitFileDTO circuitFileDTO) {
        CircuitFile circuitFile = objectMapper.convertValue(circuitFileDTO, CircuitFile.class);
        circuitFile.setFileSaveName(String.format("%d_%d_%s", HttpRequestUtil.getDepartmentId(), HttpRequestUtil.getUserId(), circuitFile.getFileName()));
        circuitFile.setOwnerId(HttpRequestUtil.getUserId());
        circuitFile.setDepartmentId(HttpRequestUtil.getDepartmentId());
        CircuitFile savedCircuitFile = this.saveAndReturnObject(circuitFile);
        return convertToVO(savedCircuitFile);
    }

    @Override
    public FileExistCheckResultVO checkFileExist(String fileName){
      List<CircuitFile> fileList = this.lambdaQuery().eq(CircuitFile::getFileName, fileName).eq(CircuitFile::getIsRecycle, 0).eq(CircuitFile::getOwnerId, HttpRequestUtil.getUserId()).list();
      if(CollectionUtils.isEmpty(fileList)) {
          // 非测试文件需要检查是否存在已粉碎文件同时是否有审查记录
          if(!fileName.startsWith("test-")) {
              // 检查是否存在同名的已粉碎文件
              List<CircuitFile> recycledFileList = this.lambdaQuery().eq(CircuitFile::getFileName, fileName).eq(CircuitFile::getIsRecycle, 1).eq(CircuitFile::getOwnerId, HttpRequestUtil.getUserId()).list();
              if(CollectionUtils.isEmpty(recycledFileList)) {
                  return new FileExistCheckResultVO().setExist(false).setConflictWithRecycledFile(false).setInAudit(false);
              }
              else {
                  if (checkRecycleFileHasReviewResult(recycledFileList.stream().map(CircuitFile::getId).toList())) {
                      return new FileExistCheckResultVO().setExist(false).setConflictWithRecycledFile(true).setInAudit(false);
                  } else {
                      return new FileExistCheckResultVO().setExist(false).setConflictWithRecycledFile(false).setInAudit(false);
                  }
              }
          }
          else {
              return new FileExistCheckResultVO().setExist(false).setConflictWithRecycledFile(false).setInAudit(false);
          }
      }
      else {
          Long fileId = fileList.get(0).getId();
          FileExistCheckResultVO inAuditResult = checkCircuitFileInAudit(fileId);
          // 非测试文件需要检查是否存在已粉碎文件同时是否有审查记录
          if(!fileName.startsWith("test-")) {
              // 检查是否存在同名的已粉碎文件
              List<CircuitFile> recycledFileList = this.lambdaQuery().eq(CircuitFile::getFileName, fileName).eq(CircuitFile::getIsRecycle, 1).eq(CircuitFile::getOwnerId, HttpRequestUtil.getUserId()).list();
              if(CollectionUtils.isEmpty(recycledFileList)) {
                  return new FileExistCheckResultVO().setExist(true).setConflictWithRecycledFile(false).setInAudit(inAuditResult.getInAudit()).setFileId(fileId);
              }
              else {
                  if(checkRecycleFileHasReviewResult(recycledFileList.stream().map(CircuitFile::getId).toList())) {
                      return new FileExistCheckResultVO().setExist(true).setConflictWithRecycledFile(true).setInAudit(inAuditResult.getInAudit()).setFileId(fileId);
                  }
                  else {
                      return new FileExistCheckResultVO().setExist(true).setConflictWithRecycledFile(false).setInAudit(inAuditResult.getInAudit()).setFileId(fileId);
                  }
              }
          }
          else {
              return new FileExistCheckResultVO().setExist(true).setConflictWithRecycledFile(false).setInAudit(inAuditResult.getInAudit()).setFileId(fileId);
          }
      }
    }

    private boolean checkRecycleFileHasReviewResult(List<Long> fileIdList) {
        CircuitReviewResultServiceImpl circuitReviewResultService = SpringContextUtils.getInstanceByType(CircuitReviewResultServiceImpl.class);
        List<CircuitReviewResult> resultByFileIdList = circuitReviewResultService.getResultByFileIdList(fileIdList);
        return CollectionUtils.isNotEmpty(resultByFileIdList);
    }

    @Override
    public FileExistCheckResultVO checkCircuitFileInAudit(Long fileId) {
        CircuitReviewResultAuditServiceImpl circuitReviewResultAuditService = SpringContextUtils.getInstanceByType(CircuitReviewResultAuditServiceImpl.class);
        List<CircuitReviewResultAudit> circuitReviewResultAuditList = circuitReviewResultAuditService.list(Wrappers.<CircuitReviewResultAudit>lambdaQuery().eq(CircuitReviewResultAudit::getFileId, fileId).eq(CircuitReviewResultAudit::getIsAuditFinished, 0));
        return new FileExistCheckResultVO().setInAudit(!circuitReviewResultAuditList.isEmpty());
    }

    @Override
    public CircuitFileVO createCircuitFile(MultipartFile file, FileSecretLevelEnum fileSecretLevelEnum, String compatibleModels, String productModel, String productName, String diagramNumber, String diagramVersion, Integer isTestFile) {

        if (file != null && file.getOriginalFilename() != null) {
            String originalFilename = file.getOriginalFilename();
            FileCheckResult result = FileUtils.checkCircuitFileExtension(deploymentType, originalFilename);
            if (result.isValid()) {
                if(isTestFile != null) {
                    if(!isTestFile.equals(0)) {
                        if(!originalFilename.startsWith("test-")) {
                            originalFilename = "test-" + originalFilename;
                        }
                    }
                }
                String minioId = storageService.uploadFile(file, originalFilename);
                if (minioId != null) {  // 创建新的文件
                    CircuitFile circuitFile = new CircuitFile();
                    circuitFile.setFileId(minioId);
                    circuitFile.setSecretLevel(fileSecretLevelEnum);
                    circuitFile.setCompatibleModels(compatibleModels);
                    circuitFile.setProductModel(productModel);
                    circuitFile.setProductName(productName);
                    circuitFile.setDiagramNumber(diagramNumber);
                    circuitFile.setDiagramVersion(diagramVersion);
                    circuitFile.setFileName(originalFilename);
                    circuitFile.setFileSaveName(String.format("%d_%d_%s", HttpRequestUtil.getDepartmentId(), HttpRequestUtil.getUserId(), originalFilename));
                    circuitFile.setOwnerId(HttpRequestUtil.getUserId());
                    circuitFile.setDepartmentId(HttpRequestUtil.getDepartmentId());
                    CircuitFile savedCircuitFile =this.saveAndReturnObject(circuitFile);
                    CircuitFileVersionService circuitFileVersionService = SpringContextUtils.getInstanceByType(CircuitFileVersionService.class);
                    CircuitFileVersionVO circuitFileVersion = circuitFileVersionService.createCircuitFileVersion(minioId, originalFilename, originalFilename, savedCircuitFile.getId(), fileSecretLevelEnum);
                    CircuitFileVO circuitFileVO = convertToVO(savedCircuitFile);
                    circuitFileVO.setFileVersionId(circuitFileVersion.getId());
                    return circuitFileVO;
                }
                else {
                    throw new KnowException("上传文件失败");
                }
            } else {
                throw new KnowException("无效的电路图文件，电路图文件应该是%s文件".formatted(result.getExpectedExtension()));
            }
        } else {
            throw new KnowException("无效的电路图文件");
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void removeCircuitFilesWithTransaction(List<DeleteDTO> removeCircuitFiles) {
        removeCircuitFiles.forEach(deleteDTO -> pulverizeAllFileVersionAndCircuitFile(deleteDTO.getId()));
    }

    @Override
    public void removeCircuitFileReviewResult(List<DeleteDTO> removeObjects) {
        List<Long> removeTargets = removeObjects.stream().map(DeleteDTO::getId).toList();
        CircuitReviewResultService circuitReviewResultService = SpringContextUtils.getInstanceByType(CircuitReviewResultService.class);
        if (CollectionUtils.isNotEmpty(removeTargets)) {
            circuitReviewResultService.removeCircuitFileReviewResult(removeTargets);
            this.removeByIds(removeTargets);
        }
    }

    /**
     * 电路图文件的逻辑--粉碎文件所有物理版本
     *
     * @param id 文件id
     * @return 删除结果
     */
    private void pulverizeAllFileVersionAndCircuitFile(Long id){
        //根据Id获取电路图文件的所有物理版本
        CircuitFileVersionService circuitFileVersionService = SpringContextUtils.getInstanceByType(CircuitFileVersionService.class);
        List<CircuitFileVersion> fileVersionList = circuitFileVersionService.lambdaQuery().eq(CircuitFileVersion::getFileId, id).eq(CircuitFileVersion::getIsRecycle, 0).list();
        List<Long> fileVersionIdList =fileVersionList.stream().map(CircuitFileVersion::getId).collect(Collectors.toList()) ;
        CircuitReviewResultService circuitReviewResultService = SpringContextUtils.getInstanceByType(CircuitReviewResultService.class);
        List<CircuitReviewResult>  circuitReviewResultList = CollectionUtils.isNotEmpty(fileVersionIdList) ? circuitReviewResultService.lambdaQuery().in(CircuitReviewResult::getFileVersionId , fileVersionIdList).ne(CircuitReviewResult::getStatus, CircuitReviewStatusEnum.FAILED.getValue()).list() : Collections.emptyList();
        List<Long> haveReviewResultFileVersionIdList = circuitReviewResultList.stream().map(CircuitReviewResult::getFileVersionId).distinct().toList();
        //检分有审查结果的文件版本和无审查结果的文件版本
        List<CircuitFileVersion>  haveReviewResultFileVersionList = new ArrayList<>();
        List<CircuitFileVersion>  haveNoReviewResultFileVersionList = new ArrayList<>();
        fileVersionList.forEach(fileVersion -> {
            storageService.deleteFile(fileVersion.getMinioId());//删除文件Minio上的文件.
            if (haveReviewResultFileVersionIdList.contains(fileVersion.getId())) {
                //有审查结果,放进回收站
                fileVersion.setIsRecycle(1);
                haveReviewResultFileVersionList.add(fileVersion);
            }
            else {
                haveNoReviewResultFileVersionList.add(fileVersion);
            }
        });
        circuitFileVersionService.updateBatchById(haveReviewResultFileVersionList);   //有审查结果,放进回收站 -- 一次性更新
        circuitFileVersionService.removeBatchByIds(haveNoReviewResultFileVersionList); //未审查的文件, 直接删除文件记录
        ChangeFileRecycleStatusDTO changeFileRecycleStatusDTO = new ChangeFileRecycleStatusDTO();
        changeFileRecycleStatusDTO.setFileIdList(List.of(id));
        changeFileRecycleStatusDTO.setIsRecycle(1);
        this.changeCircuitFileRecycle(changeFileRecycleStatusDTO); //将电路图文件放进回收站
    }


    @Override
    public CircuitFileVO updateCircuitFile(Long id, CircuitFile circuitFile) {
        circuitFile.setId(id);
        CircuitFile updatedCircuitFile = this.updateByIDAndReturnObject(circuitFile);
        return convertToVO(updatedCircuitFile);
    }

    @Override
    public Boolean changeCircuitFileRecycle(ChangeFileRecycleStatusDTO changeFileRecycleStatusDTO) {
        if (CollectionUtils.isNotEmpty(changeFileRecycleStatusDTO.getFileIdList())) {
            return this.update(Wrappers.<CircuitFile>lambdaUpdate()
                    .in(CircuitFile::getId, changeFileRecycleStatusDTO.getFileIdList())
                    .set(CircuitFile::getIsRecycle, changeFileRecycleStatusDTO.getIsRecycle())
                    .set(CircuitFile::getUpdateDate, LocalDateTime.now())
            );
        }
        else {
            return true;
        }
    }

    @Override
    public CircuitFileVO getCircuitFileVOById(Long id) {
        CircuitFile circuitFile = this.getById(id);
        return convertToVO(circuitFile);
    }

    @Override
    public CircuitFileVO getCircuitFileVOByFileName(String fileName) {
        List<CircuitFile> fileList = this.lambdaQuery().eq(CircuitFile::getFileName, fileName).eq(CircuitFile::getIsRecycle, 0).eq(CircuitFile::getOwnerId, HttpRequestUtil.getUserId()).list();
        if (CollectionUtils.isNotEmpty(fileList)) {
            return convertToVO(fileList.get(0));
        } else {
            throw new KnowException("电路图文件不存在");
        }
    }

    @Override
    public IPage<CircuitFileDetailVO> getRecycledCircuitFileVOPage(Page<CircuitFile> page, Long depId, Long userId, String fileName) {
        Page<CircuitFileDetailVO> resultPage = new Page<>(page.getCurrent(), page.getSize());
        IPage<CircuitFileDetailVO> circuitFilePage = this.baseMapper.selectRecycledCircuitFileWithReviewResult(
            resultPage, depId, userId, fileName);
        
        // 处理返回结果并填充部门和用户信息
        List<CircuitFileDetailVO> records = circuitFilePage.getRecords();
        List<Long> ownerIdList = records.stream().map(CircuitFileDetailVO::getOwnerId).filter(Objects::nonNull).collect(Collectors.toSet()).stream().toList();
        List<Long> departmentIdList = records.stream().map(CircuitFileDetailVO::getDepartmentId).filter(Objects::nonNull).collect(Collectors.toSet()).stream().toList();

        ResponseWrapper<List<DepartmentDTO>> departmentsResponseWrapper = userServiceClient.getDepartmentsByIds(departmentIdList);
        if(!departmentsResponseWrapper.isSucc()) {
            log.error("获取部门列表失败: {} 错误信息: {}", departmentIdList, departmentsResponseWrapper.getMsg());
            return circuitFilePage; // 已经是CircuitFileDetailVO类型的分页结果
        }
        List<DepartmentDTO> departments = departmentsResponseWrapper.getContent();
        Map<Long, String> departmentIdToNameMap = departments.stream().collect(Collectors.toMap(DepartmentDTO::getId, DepartmentDTO::getName));
        
        ResponseWrapper<List<UserDTO>> usersResponseWrapper = userServiceClient.getUsersByIds(ownerIdList);
        if(!usersResponseWrapper.isSucc()) {
            log.error("获取用户列表失败: {} 错误信息: {}", ownerIdList, usersResponseWrapper.getMsg());
            return circuitFilePage; // 已经是CircuitFileDetailVO类型的分页结果
        }
        List<UserDTO> users = usersResponseWrapper.getContent();
        Map<Long, String> userIdToNameMap = users.stream().collect(Collectors.toMap(UserDTO::getId, UserDTO::getName));

        // 更新记录中的部门名称和用户名
        records.forEach(record -> {
            if (record.getDepartmentId() != null && departmentIdToNameMap.containsKey(record.getDepartmentId())) {
                record.setDepartmentName(departmentIdToNameMap.get(record.getDepartmentId()));
            }
            if (record.getOwnerId() != null && userIdToNameMap.containsKey(record.getOwnerId())) {
                record.setOwnerName(userIdToNameMap.get(record.getOwnerId()));
            }
        });

        return circuitFilePage;
    }

    @Override
    public IPage<CircuitFileDetailVO> getCircuitFileDetailVOPage(Page<CircuitFileDetailVO> page, Long depId, Long selectedUserId, String fileName, CircuitFileSortFieldEnum sortField, SortDirectionEnum sortDirection) {
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
            // 部门领导：查看全部门（包括子部门）的文件
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
            // jz领导或admin：查看全部门（包括子部门）的文件
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
        return this.baseMapper.selectFileDetailPage(page, departmentIds, userId, fileName, sortField, sortDirection);
    }

    /**
     * 将实体转换为VO
     */
    private CircuitFileVO convertToVO(CircuitFile circuitFile) {
        if (circuitFile == null) {
            return null;
        }
        return objectMapper.convertValue(circuitFile, CircuitFileVO.class);
    }

    /**
     * 将CircuitFile转换为CircuitFileDetailVO
     */
    private CircuitFileDetailVO convertCircuitFileToCircuitFileDetailVO(CircuitFile circuitFile, Map<Long, String> departmentIdToNameMap, Map<Long, String> userIdToNameMap) {
        if (circuitFile == null) {
            return null;
        }

        CircuitFileDetailVO vo = new CircuitFileDetailVO();
        vo.setFileId(circuitFile.getId());
        vo.setFileName(circuitFile.getFileName());
        vo.setSecretLevel(circuitFile.getSecretLevel());
        vo.setDepartmentId(circuitFile.getDepartmentId());
        vo.setOwnerId(circuitFile.getOwnerId());
        vo.setIsRecycle(circuitFile.getIsRecycle());
        vo.setComments(circuitFile.getComments());
        vo.setCreateDate(circuitFile.getCreateDate());
        vo.setVersion(circuitFile.getVersion());

        // 设置部门名称
        if (circuitFile.getDepartmentId() != null && departmentIdToNameMap != null) {
            vo.setDepartmentName(departmentIdToNameMap.get(circuitFile.getDepartmentId()));
        }

        // 设置用户名称
        if (circuitFile.getOwnerId() != null && userIdToNameMap != null) {
            vo.setOwnerName(userIdToNameMap.get(circuitFile.getOwnerId()));
        }

        return vo;
    }
}
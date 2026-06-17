package com.starmol.logicreview.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starmol.logicreview.bean.vo.ToolFileVO;
import com.starmol.logicreview.common.UserMetaData;
import com.starmol.logicreview.constant.SysRoleTypeEnum;
import com.starmol.logicreview.exception.KnowException;
import com.starmol.logicreview.model.base.DeleteDTO;
import com.starmol.logicreview.model.codereview.ToolFile;
import com.starmol.logicreview.repository.codereview.ToolFileMapper;
import com.starmol.logicreview.service.ToolFileService;
import com.starmol.logicreview.service.base.impl.BaseCascadeServiceImpl;
import com.starmol.logicreview.service.common.StorageService;
import com.starmol.logicreview.utils.HttpRequestUtil;


import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * 工具文件服务实现类
 *
 * @author system
 * @date 2025-01-07
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ToolFileServiceImpl extends BaseCascadeServiceImpl<ToolFileMapper, ToolFile> implements ToolFileService, ApplicationRunner {

    private final ObjectMapper objectMapper;
    private final StorageService storageService;


    /**
     * 应用启动时清空已经删除的记录对应的Minio上的文件
     *
     * @param args 应用参数
     */
    @Override
    public void run(ApplicationArguments args) {
        log.info("开始清空已经删除的记录对应的Minio上的文件...");
        List<ToolFile> toolFiles = this.getBaseMapper().selectDeletedRecords();
        toolFiles.forEach(toolFile -> {
            storageService.deleteFile(toolFile.getFileId());
        });
    }

    @Override
    public ToolFileVO createToolFile(MultipartFile file, String toolName, String comments) {
        // 获取当前用户信息
        UserMetaData currentUser = HttpRequestUtil.getUser();
        if (currentUser == null) {
           throw new KnowException("获取当前用户信息失败");
        }
        // 获取用户角色
        SysRoleTypeEnum userRole = currentUser.getSysRoleType();
        if ((userRole == null) || !SysRoleTypeEnum.ADMIN.equals(userRole)) {
            throw new KnowException("当前用户无权限上传文件");
        }
        String fileId = storageService.uploadFile(file);
        if (fileId != null) {
            ToolFile toolFile = new ToolFile();
            toolFile.setFileId(fileId);
            toolFile.setFileName(file.getOriginalFilename());
            toolFile.setToolName(toolName);
            toolFile.setComments(comments);

            ToolFile savedToolFile = this.saveAndReturnObject(toolFile);
            return convertToVO(savedToolFile);
        }
        else {
            throw new KnowException("上传文件失败");
        }
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void removeToolFilesWithTransaction(List<DeleteDTO> removeToolFiles) {
        List<Long> idList = removeToolFiles.stream().map(DeleteDTO::getId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(idList)) {
            List<ToolFile> deleteToolFileList = list(Wrappers.<ToolFile>lambdaQuery().in(ToolFile::getId, idList));
            this.removeByObjectsWithFill(deleteToolFileList);
            deleteToolFileList.forEach(toolFile-> storageService.deleteFile(toolFile.getFileId()));
        }
    }

    @Override
    @SneakyThrows
    public IPage<ToolFileVO> getToolFileVOPage(Long pageNumber,Long pageSize, String fileName) {
        IPage<ToolFile> toolFilePage = this.page(new Page<>(pageNumber, pageSize), Wrappers.<ToolFile>lambdaQuery().like(StringUtils.isNotBlank(fileName),ToolFile::getFileName, fileName));
        return toolFilePage.convert(t -> objectMapper.convertValue(t, ToolFileVO.class));
    }


    @Override
    public ToolFileVO getToolFileVOById(Long id) {
        ToolFile toolFile = this.getById(id);
        return convertToVO(toolFile);
    }

    /**
     * 将实体转换为VO
     */
    private ToolFileVO convertToVO(ToolFile toolFile) {
        if (toolFile == null) {
            return null;
        }
        return objectMapper.convertValue(toolFile, ToolFileVO.class);
    }
}
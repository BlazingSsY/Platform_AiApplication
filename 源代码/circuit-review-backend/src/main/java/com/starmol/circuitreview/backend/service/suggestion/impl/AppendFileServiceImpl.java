/*
 * STARMOL Confidential
 * OCO Source Materials
 * STARMOL
 * © Copyright YunLian Advanced Technology Ltd. 2018-2020
 * The source code for this program is not published or otherwise divested of its trade secrets, irrespective of what has been deposited with the P.R.China Copyright Office
 */
package com.starmol.circuitreview.backend.service.suggestion.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starmol.circuitreview.backend.bean.dto.AppendFileDTO;
import com.starmol.circuitreview.backend.bean.vo.AppendFileVO;
import com.starmol.circuitreview.backend.model.base.DeleteDTO;
import com.starmol.circuitreview.backend.model.suggestion.AppendFile;
import com.starmol.circuitreview.backend.repository.suggestion.AppendFileMapper;
import com.starmol.circuitreview.backend.service.base.impl.BaseServiceImpl;
import com.starmol.circuitreview.backend.service.common.StorageService;
import com.starmol.circuitreview.backend.service.suggestion.AppendFileService;
import com.starmol.circuitreview.backend.utils.SpringContextUtils;


import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;


/**
 * AppendFileService接口的实现类
 *
 * @author Yuexiaopeng
 * @date 2019/11/21
 **/
@Service
@Slf4j
public class AppendFileServiceImpl extends BaseServiceImpl<AppendFileMapper, AppendFile> implements AppendFileService {
    private final ObjectMapper objectMapper;

    @Lazy
    public AppendFileServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public AppendFileVO createAppendFile(AppendFileDTO appendFileDTO){
        AppendFile appendFile = objectMapper.convertValue(appendFileDTO, AppendFile.class);
        return objectMapper.convertValue(this.saveAndReturnObject(appendFile), AppendFileVO.class);
    }

    @Override
    public void  deleteAppendFilesByFid(List<Long> fidList){
        if (Objects.nonNull(fidList) && fidList.size() > 0) {
            List<AppendFile> deleteAppendFileList = list(Wrappers.<AppendFile>lambdaQuery().in(AppendFile::getFId, fidList));
            this.removeByObjectsWithFill(deleteAppendFileList);
            deleteAppendFileList.forEach(i-> SpringContextUtils.getInstanceByType(StorageService.class).deleteFile(i.getFileId()));
        }
    }

    @Override
    public AppendFileVO updateAppendFile(Long id, AppendFileDTO appendFileDTO){
        AppendFile appendFile = objectMapper.convertValue(appendFileDTO, AppendFile.class);
        appendFile.setId(id);
        return objectMapper.convertValue(this.updateByIDAndReturnObject(appendFile), AppendFileVO.class);
    }

    @Override
    public IPage<AppendFileVO> getAppendFileVOByPage(IPage<AppendFile> objectPage, String type){
        IPage<AppendFile> page = this.page(objectPage, Wrappers.<AppendFile>lambdaQuery().eq(StringUtils.isNotEmpty(type), AppendFile::getType, type));
        return page.convert(i-> objectMapper.convertValue(i, AppendFileVO.class));
    }

    @Override
    public AppendFileVO getAppendFileVOById(Long id){
        return objectMapper.convertValue(this.getById(id), AppendFileVO.class);
    }

    @Override
    public List<AppendFile> createAppendFilesWithoutSave(List<AppendFileDTO> appendFileDTOList, Long fid, String type){
        List<AppendFile> appendFileList = appendFileDTOList.stream().map(i->{
            AppendFile appendFile = objectMapper.convertValue(i, AppendFile.class);
            appendFile.setId(IdWorker.getId());
            appendFile.setFId(fid);
            appendFile.setType(type);
            return appendFile;
        }).collect(Collectors.toList());
        return appendFileList;
    }

    @Override
    public void createAppendFiles(List<AppendFileDTO> appendFileDTOList, Long fid, String type){
        List<AppendFile> appendFileList = appendFileDTOList.stream().map(i->{
            AppendFile appendFile = objectMapper.convertValue(i, AppendFile.class);
            appendFile.setId(IdWorker.getId());
            appendFile.setFId(fid);
            appendFile.setType(type);
            return appendFile;
        }).collect(Collectors.toList());
        this.saveBatch(appendFileList);
    }

    @Override
    public void updateAppendFiles(List<AppendFileDTO> newAppendFileDTOList, Long fid, String type){
        List<AppendFile> oldAppendFileList = this.list(Wrappers.<AppendFile>lambdaQuery().eq(AppendFile::getFId, fid).eq(AppendFile::getType, type));
        //id为空的为, 新增加的附加列表
        List<AppendFileDTO> addedAppendFileList = newAppendFileDTOList.stream().filter(i-> Objects.isNull(i.getId())).collect(Collectors.toList());
        createAppendFiles(addedAppendFileList, fid, type);
        //获取newAppendFileDTOList中的id列表
        List<Long> newAppendFileIdList = newAppendFileDTOList.stream().filter(Objects::nonNull).map(i -> i.getId()).collect(Collectors.toList());
        //原始列表中存在，本次提交的附加列表中没有的附加文件 为要删除的
        List<AppendFile> deleteAppendFileList = oldAppendFileList.stream().filter(i-> !newAppendFileIdList.contains(i.getId())).collect(Collectors.toList());
        this.removeByObjectsWithFill(deleteAppendFileList);
        deleteAppendFileList.forEach(i-> SpringContextUtils.getInstanceByType(StorageService.class).deleteFile(i.getFileId()));
    }

    @Override
    public AppendFile findByAppendFileName(String appendFileName) {
        return getOne(Wrappers.<AppendFile>lambdaQuery().eq(AppendFile::getFileName, appendFileName));
    }

    @Override
    public List<AppendFileDTO> findByAppendFId(String fId, String type) {
        final List<AppendFile> list = this.list(Wrappers.<AppendFile>lambdaQuery().eq(AppendFile::getFId, fId).eq(AppendFile::getType, type));
        return objectMapper.convertValue(list, new TypeReference<>() {
        });
    }

    @Override
    @SneakyThrows
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public void deleteAppendFilesWithTransaction(List<DeleteDTO> deleteAppendFiles) {
         removeByIdsWithFill(deleteAppendFiles);
    }

    @Override
    @SneakyThrows
    public Map<String, List<AppendFile>> getAppendAllFileMap(List<Long> fidList) {
        List<AppendFile> appendAllFileList = this.list(Wrappers.<AppendFile>lambdaQuery().in(fidList.size()>0, AppendFile::getFId, fidList));
        Map<String, List<AppendFile>> appendAllFilesMap = appendAllFileList.stream().collect(Collectors.groupingBy(AppendFile::getType));
        return appendAllFilesMap;
    }
    @Override
    @SneakyThrows
    public Map<Long, List<AppendFile>> getAppendFileMap(List<Long> fidList, String type) {
        List<AppendFile> appendFileList = CollectionUtils.isEmpty(fidList) ? Collections.emptyList() : this.list(Wrappers.<AppendFile>lambdaQuery().in(fidList.size() > 0, AppendFile::getFId, fidList).eq(AppendFile::getType, type));
        Map<Long, List<AppendFile>> appendFilesMap = appendFileList.stream().collect(Collectors.groupingBy(AppendFile::getFId));
        return appendFilesMap;
    }

}

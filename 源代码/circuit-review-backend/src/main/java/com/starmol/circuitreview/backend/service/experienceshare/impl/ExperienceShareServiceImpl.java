package com.starmol.circuitreview.backend.service.experienceshare.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starmol.circuitreview.backend.bean.dto.AppendFileDTO;
import com.starmol.circuitreview.backend.bean.dto.ExperienceShareDTO;
import com.starmol.circuitreview.backend.bean.vo.AppendFileVO;
import com.starmol.circuitreview.backend.bean.vo.ExperienceShareVO;
import com.starmol.circuitreview.backend.constant.Constant;
import com.starmol.circuitreview.backend.constant.SysRoleTypeEnum;
import com.starmol.circuitreview.backend.exception.KnowException;
import com.starmol.circuitreview.backend.model.base.DeleteDTO;
import com.starmol.circuitreview.backend.model.experienceshare.ExperienceShare;
import com.starmol.circuitreview.backend.model.experienceshare.LikeRecord;
import com.starmol.circuitreview.backend.model.suggestion.AppendFile;
import com.starmol.circuitreview.backend.repository.experienceshare.ExperienceShareMapper;
import com.starmol.circuitreview.backend.repository.experienceshare.LikeRecordMapper;
import com.starmol.circuitreview.backend.service.base.impl.BaseServiceImpl;
import com.starmol.circuitreview.backend.service.suggestion.AppendFileService;
import com.starmol.circuitreview.backend.service.experienceshare.ExperienceShareService;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * ExperienceShareService接口的实现类
 *
 * @author system
 * @date 2026-05-27
 */
@Service
@Slf4j
public class ExperienceShareServiceImpl extends BaseServiceImpl<ExperienceShareMapper, ExperienceShare> implements ExperienceShareService {

    private final AppendFileService appendFileService;
    private final LikeRecordMapper likeRecordMapper;
    private final ObjectMapper objectMapper;

    @Lazy
    public ExperienceShareServiceImpl(ObjectMapper objectMapper, AppendFileService appendFileService, LikeRecordMapper likeRecordMapper) {
        this.objectMapper = objectMapper;
        this.appendFileService = appendFileService;
        this.likeRecordMapper = likeRecordMapper;
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public ExperienceShare createExperienceShareAndUpdateAppendFiles(ExperienceShareDTO dto) {
        ExperienceShare experienceShare = objectMapper.convertValue(dto, ExperienceShare.class);
        experienceShare.setLikeCount(0);
        ExperienceShare result = saveAndReturnObject(experienceShare);
        if (CollectionUtils.isNotEmpty(dto.getAppendFileList())) {
            appendFileService.createAppendFiles(dto.getAppendFileList(), result.getId(), Constant.SJJYFX_EXPERIENCE_SHARE_APPEND_FILES);
        }
        return result;
    }

    @Override
    @SneakyThrows
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public void deleteExperienceSharesWithTransaction(List<DeleteDTO> deleteList) {
        checkIfCurrentUserHasPermission(deleteList);
        removeByIdsWithFill(deleteList);
        appendFileService.deleteAppendFilesByFid(deleteList.stream().map(DeleteDTO::getId).collect(Collectors.toList()));
    }

    /**
     * 检查当前用户是否有权限进行操作
     */
    private void checkIfCurrentUserHasPermission(List<DeleteDTO> deleteList) {
        Long operatorId = getOperatorId();
        SysRoleTypeEnum userRole = getSysRoleType();
        if (userRole == null) {
            throw new KnowException("当前用户无权限删除经验分享");
        }
        if ((userRole == SysRoleTypeEnum.ADMIN) || (userRole == SysRoleTypeEnum.SUPER_SUPERVISOR)) {
            return;
        }
        List<ExperienceShare> shareList = listByIds(deleteList.stream().map(DeleteDTO::getId).collect(Collectors.toList()));
        for (ExperienceShare share : shareList) {
            if (!share.getCreateUser().equals(operatorId)) {
                throw new KnowException("当前用户无权限删除ID为:%s 的经验分享".formatted(share.getId()));
            }
        }
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public ExperienceShare updateExperienceShareAndUpdateAppendFiles(ExperienceShareDTO dto) {
        ExperienceShare experienceShare = objectMapper.convertValue(dto, ExperienceShare.class);
        ExperienceShare result = updateByIDAndReturnObject(experienceShare);
        List<AppendFileDTO> newFileList = CollectionUtils.isNotEmpty(dto.getAppendFileList()) ? dto.getAppendFileList() : Collections.emptyList();
        appendFileService.updateAppendFiles(newFileList, result.getId(), Constant.SJJYFX_EXPERIENCE_SHARE_APPEND_FILES);
        return result;
    }

    @Override
    @SneakyThrows
    public IPage<ExperienceShareVO> listExperienceShareVOByPage(IPage<ExperienceShare> page, String title, String contributor, Long departmentId) {
        IPage<ExperienceShareVO> voPage = this.getBaseMapper().getExperienceShareVOPage(page, title, contributor, departmentId);
        List<Long> fidList = voPage.getRecords().stream().map(ExperienceShareVO::getId).collect(Collectors.toList());
        Map<Long, List<AppendFile>> appendFilesMap = appendFileService.getAppendFileMap(fidList, Constant.SJJYFX_EXPERIENCE_SHARE_APPEND_FILES);
        Long currentUserId = getOperatorId();

        IPage<ExperienceShareVO> resultPage = voPage.convert(vo -> {
            ExperienceShareVO shareVO = objectMapper.convertValue(vo, ExperienceShareVO.class);
            // 设置附件列表
            shareVO.setAppendFileList(Optional.ofNullable(appendFilesMap.get(vo.getId()))
                    .map(list -> list.stream().map(file -> objectMapper.convertValue(file, AppendFileVO.class)).toList())
                    .orElse(Collections.emptyList()));
            // 设置当前用户是否点赞
            shareVO.setCurrentUserLiked(getCurrentUserLikeStatus(vo.getId()));
            return shareVO;
        });
        return resultPage;
    }

    @Override
    @SneakyThrows
    public ExperienceShareVO getExperienceShareVOById(Long id) {
        ExperienceShareVO vo = objectMapper.convertValue(getById(id), ExperienceShareVO.class);
        // 设置附件列表
        List<AppendFile> appendFiles = appendFileService.list(
                Wrappers.<AppendFile>lambdaQuery()
                        .eq(AppendFile::getFId, id)
                        .eq(AppendFile::getType, Constant.SJJYFX_EXPERIENCE_SHARE_APPEND_FILES));
        vo.setAppendFileList(appendFiles.stream()
                .map(file -> objectMapper.convertValue(file, AppendFileVO.class))
                .toList());
        // 设置当前用户是否点赞
        vo.setCurrentUserLiked(getCurrentUserLikeStatus(id));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public Integer toggleLike(Long experienceId) {
        Long userId = getOperatorId();
        if (userId == null || userId == 0L) {
            throw new KnowException("请先登录后再点赞");
        }

        ExperienceShare experienceShare = getById(experienceId);
        if (experienceShare == null) {
            throw new KnowException("经验分享不存在");
        }

        // 查询当前用户的点赞记录
        List<LikeRecord> existingRecords = likeRecordMapper.selectList(
                Wrappers.<LikeRecord>lambdaQuery()
                        .eq(LikeRecord::getExperienceId, experienceId)
                        .eq(LikeRecord::getUserId, userId));

        if (CollectionUtils.isNotEmpty(existingRecords)) {
            LikeRecord record = existingRecords.get(0);
            if (Objects.equals(record.getIsLiked(), 1)) {
                // 已点赞，取消点赞
                record.setIsLiked(0);
                likeRecordMapper.updateById(record);
                experienceShare.setLikeCount(Math.max(0, experienceShare.getLikeCount() - 1));
            } else {
                // 已取消，重新点赞
                record.setIsLiked(1);
                likeRecordMapper.updateById(record);
                experienceShare.setLikeCount(experienceShare.getLikeCount() + 1);
            }
        } else {
            // 首次点赞
            LikeRecord newRecord = new LikeRecord();
            newRecord.setExperienceId(experienceId);
            newRecord.setUserId(userId);
            newRecord.setIsLiked(1);
            likeRecordMapper.insert(newRecord);
            experienceShare.setLikeCount(experienceShare.getLikeCount() + 1);
        }

        updateById(experienceShare);
        return experienceShare.getLikeCount();
    }

    @Override
    public Boolean getCurrentUserLikeStatus(Long experienceId) {
        Long userId = getOperatorId();
        if (userId == null || userId == 0L) {
            return false;
        }
        List<LikeRecord> records = likeRecordMapper.selectList(
                Wrappers.<LikeRecord>lambdaQuery()
                        .eq(LikeRecord::getExperienceId, experienceId)
                        .eq(LikeRecord::getUserId, userId)
                        .eq(LikeRecord::getIsLiked, 1));
        return CollectionUtils.isNotEmpty(records);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public void updateUpdateDate(Long id, LocalDateTime updateDate) {
        ExperienceShare share = getById(id);
        if (share != null) {
            share.setUpdateDate(updateDate);
            updateById(share);
        }
    }
}

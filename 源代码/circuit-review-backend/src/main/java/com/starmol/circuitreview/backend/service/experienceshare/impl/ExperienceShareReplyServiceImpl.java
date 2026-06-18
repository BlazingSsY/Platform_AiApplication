package com.starmol.circuitreview.backend.service.experienceshare.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starmol.circuitreview.backend.bean.dto.AppendFileDTO;
import com.starmol.circuitreview.backend.bean.dto.ExperienceShareReplyDTO;
import com.starmol.circuitreview.backend.bean.vo.AppendFileVO;
import com.starmol.circuitreview.backend.bean.vo.ExperienceShareReplyVO;
import com.starmol.circuitreview.backend.constant.Constant;
import com.starmol.circuitreview.backend.constant.SysRoleTypeEnum;
import com.starmol.circuitreview.backend.exception.KnowException;
import com.starmol.circuitreview.backend.model.base.DeleteDTO;
import com.starmol.circuitreview.backend.model.experienceshare.ExperienceShare;
import com.starmol.circuitreview.backend.model.experienceshare.ExperienceShareReply;
import com.starmol.circuitreview.backend.model.suggestion.AppendFile;
import com.starmol.circuitreview.backend.repository.experienceshare.ExperienceShareReplyMapper;
import com.starmol.circuitreview.backend.service.base.impl.BaseServiceImpl;
import com.starmol.circuitreview.backend.service.experienceshare.ExperienceShareReplyService;
import com.starmol.circuitreview.backend.service.experienceshare.ExperienceShareService;
import com.starmol.circuitreview.backend.service.suggestion.AppendFileService;

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
 * ExperienceShareReplyService接口的实现类
 *
 * @author system
 * @date 2026-06-04
 */
@Service
@Slf4j
public class ExperienceShareReplyServiceImpl extends BaseServiceImpl<ExperienceShareReplyMapper, ExperienceShareReply> implements ExperienceShareReplyService {

    private final ExperienceShareService experienceShareService;
    private final AppendFileService appendFileService;
    private final ObjectMapper objectMapper;

    @Lazy
    public ExperienceShareReplyServiceImpl(ExperienceShareService experienceShareService, ObjectMapper objectMapper, AppendFileService appendFileService) {
        this.experienceShareService = experienceShareService;
        this.objectMapper = objectMapper;
        this.appendFileService = appendFileService;
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public ExperienceShareReply createReplyAndUpdateAppendFiles(ExperienceShareReplyDTO dto) {
        ExperienceShareReply reply = objectMapper.convertValue(dto, ExperienceShareReply.class);
        ExperienceShareReply result = saveAndReturnObject(reply);
        if (CollectionUtils.isNotEmpty(dto.getAppendFileList())) {
            appendFileService.createAppendFiles(dto.getAppendFileList(), result.getId(), Constant.SJJYFX_EXPERIENCE_SHARE_REPLY_APPEND_FILES);
        }
        // 更新经验分享的更新时间
        experienceShareService.updateUpdateDate(reply.getFId(), reply.getUpdateDate());
        return result;
    }

    @Override
    @SneakyThrows
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public void deleteRepliesWithTransaction(List<DeleteDTO> deleteReplies) {
        checkIfCurrentUserHasPermission(deleteReplies);
        List<Long> fidList = getFidList(deleteReplies);
        removeByIdsWithFill(deleteReplies);
        appendFileService.deleteAppendFilesByFid(deleteReplies.stream().map(DeleteDTO::getId).collect(Collectors.toList()));
        updateExperienceShareUpdateDateByIdList(fidList);
    }

    private List<Long> getFidList(List<DeleteDTO> deleteReplies) {
        List<Long> idList = deleteReplies.stream().map(DeleteDTO::getId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(idList)) {
            List<ExperienceShareReply> replyList = listByIds(idList);
            return replyList.stream().map(ExperienceShareReply::getFId).distinct().collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

    @SneakyThrows
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public void updateExperienceShareUpdateDateByIdList(List<Long> fidList) {
        for (Long id : fidList) {
            LocalDateTime lastUpdateDate = list(Wrappers.<ExperienceShareReply>lambdaQuery()
                    .eq(ExperienceShareReply::getFId, id)
                    .select(ExperienceShareReply::getUpdateDate))
                    .stream().map(ExperienceShareReply::getUpdateDate).max(LocalDateTime::compareTo).orElse(null);

            if (Objects.nonNull(lastUpdateDate)) {
                experienceShareService.updateUpdateDate(id, lastUpdateDate);
            } else {
                ExperienceShare share = experienceShareService.getById(id);
                experienceShareService.updateUpdateDate(id, Optional.ofNullable(share).map(ExperienceShare::getCreateDate).orElse(LocalDateTime.now()));
            }
        }
    }

    /**
     * 检查当前用户是否有权限进行操作
     */
    private void checkIfCurrentUserHasPermission(List<DeleteDTO> deleteReplies) {
        Long operatorId = getOperatorId();
        SysRoleTypeEnum userRole = getSysRoleType();
        if (userRole == null) {
            throw new KnowException("当前用户无权限删除回复");
        }
        if ((userRole == SysRoleTypeEnum.ADMIN) || (userRole == SysRoleTypeEnum.SUPER_SUPERVISOR)) {
            return;
        }
        List<ExperienceShareReply> replyList = listByIds(deleteReplies.stream().map(DeleteDTO::getId).collect(Collectors.toList()));
        for (ExperienceShareReply reply : replyList) {
            if (!reply.getCreateUser().equals(operatorId)) {
                throw new KnowException("当前用户无权限删除ID为:%s 的回复".formatted(reply.getId()));
            }
        }
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public ExperienceShareReply updateReplyAndUpdateAppendFiles(ExperienceShareReplyDTO dto) {
        ExperienceShareReply reply = objectMapper.convertValue(dto, ExperienceShareReply.class);
        ExperienceShareReply result = updateByIDAndReturnObject(reply);
        List<AppendFileDTO> newFileList = CollectionUtils.isNotEmpty(dto.getAppendFileList()) ? dto.getAppendFileList() : Collections.emptyList();
        appendFileService.updateAppendFiles(newFileList, result.getId(), Constant.SJJYFX_EXPERIENCE_SHARE_REPLY_APPEND_FILES);
        return result;
    }

    @Override
    public ExperienceShareReply findByReplyContent(String replyContent) {
        return getOne(Wrappers.<ExperienceShareReply>lambdaQuery().eq(ExperienceShareReply::getReply, replyContent));
    }

    @Override
    @SneakyThrows
    public IPage<ExperienceShareReplyVO> listReplyVOByPage(IPage<ExperienceShareReply> page, Long fId, String replyContent) {
        IPage<ExperienceShareReplyVO> replyPage = this.getBaseMapper().getReplyVOPage(page, fId, replyContent);
        List<Long> fidList = replyPage.getRecords().stream().map(ExperienceShareReplyVO::getId).collect(Collectors.toList());
        Map<Long, List<AppendFile>> appendFilesMap = appendFileService.getAppendFileMap(fidList, Constant.SJJYFX_EXPERIENCE_SHARE_REPLY_APPEND_FILES);
        return replyPage.convert(reply -> {
            ExperienceShareReplyVO vo = objectMapper.convertValue(reply, ExperienceShareReplyVO.class);
            vo.setAppendFileList(Optional.ofNullable(appendFilesMap.get(reply.getId()))
                    .map(list -> list.stream().map(file -> objectMapper.convertValue(file, AppendFileVO.class)).toList())
                    .orElse(Collections.emptyList()));
            return vo;
        });
    }

    @Override
    @SneakyThrows
    public ExperienceShareReplyVO getReplyVOById(Long id) {
        ExperienceShareReplyVO vo = objectMapper.convertValue(getById(id), ExperienceShareReplyVO.class);
        List<AppendFile> appendFiles = appendFileService.list(
                Wrappers.<AppendFile>lambdaQuery()
                        .eq(AppendFile::getFId, id)
                        .eq(AppendFile::getType, Constant.SJJYFX_EXPERIENCE_SHARE_REPLY_APPEND_FILES));
        vo.setAppendFileList(appendFiles.stream()
                .map(file -> objectMapper.convertValue(file, AppendFileVO.class))
                .toList());
        return vo;
    }
}

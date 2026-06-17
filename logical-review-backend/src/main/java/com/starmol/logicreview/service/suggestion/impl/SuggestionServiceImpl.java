package com.starmol.logicreview.service.suggestion.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starmol.logicreview.bean.dto.AppendFileDTO;
import com.starmol.logicreview.bean.dto.ChangeSuggestionStatusDTO;
import com.starmol.logicreview.bean.dto.SuggestionDTO;
import com.starmol.logicreview.bean.vo.AppendFileVO;
import com.starmol.logicreview.bean.vo.SuggestionVO;
import com.starmol.logicreview.constant.Constant;
import com.starmol.logicreview.constant.SuggestionStatusEnum;
import com.starmol.logicreview.constant.SysRoleTypeEnum;
import com.starmol.logicreview.exception.KnowException;
import com.starmol.logicreview.model.base.DeleteDTO;
import com.starmol.logicreview.model.suggestion.AppendFile;
import com.starmol.logicreview.model.suggestion.Suggestion;
import com.starmol.logicreview.model.suggestion.SuggestionStatus;
import com.starmol.logicreview.repository.suggestion.SuggestionMapper;
import com.starmol.logicreview.service.base.impl.BaseServiceImpl;
import com.starmol.logicreview.service.suggestion.AppendFileService;
import com.starmol.logicreview.service.suggestion.SuggestionService;
import com.starmol.logicreview.service.suggestion.SuggestionStatusService;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;


/**
 * SuggestionService接口的实现类
 *
 * @author Yuexiaopeng
 * @date 2019/11/21
 **/
@Service
@Slf4j
public class SuggestionServiceImpl extends BaseServiceImpl<SuggestionMapper, Suggestion> implements SuggestionService {
    private AppendFileService appendFileService;
    private final SuggestionStatusService suggestionStatusService;
    private ObjectMapper objectMapper;

    @Lazy
    public SuggestionServiceImpl(ObjectMapper objectMapper, AppendFileService appendFileService, SuggestionStatusService suggestionStatusService) {
        this.objectMapper = objectMapper;
        this.appendFileService = appendFileService;
        this.suggestionStatusService = suggestionStatusService;
    }

    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public Suggestion createSuggestionAndUpdateAppendFiles(SuggestionDTO suggestionDTO){
        Suggestion suggestion = objectMapper.convertValue(suggestionDTO, Suggestion.class);
        suggestion.setStatus(SuggestionStatusEnum.OPEN);
        Suggestion returnSuggestion = saveAndReturnObject(suggestion);
        if (CollectionUtils.isNotEmpty(suggestionDTO.getAppendFileList())) {
            appendFileService.createAppendFiles(suggestionDTO.getAppendFileList(), returnSuggestion.getId(), Constant.YJFK_SUGGESTION_APPEND_FILES);
        }
        //创建建议时,创建SuggestionStatus记录
        SuggestionStatus suggestionStatus = new SuggestionStatus().setSuggestionId(returnSuggestion.getId()).setStatus(SuggestionStatusEnum.NEW_OPEN);
        suggestionStatusService.saveAndReturnObject(suggestionStatus);
        return returnSuggestion;
    }


    @Override
    @SneakyThrows
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public void deleteSuggestionsWithTransaction(List<DeleteDTO> deleteSuggestions) {
        checkIfCurrentUserHasPermission(deleteSuggestions);
        removeByIdsWithFill(deleteSuggestions);
        appendFileService.deleteAppendFilesByFid(deleteSuggestions.stream().map(DeleteDTO::getId).collect(Collectors.toList()));
    }

    /**
     * 检查当前用户是否有权限进行操作,如果无权限则抛出异常
     * @param deleteSuggestions
     */
    private void checkIfCurrentUserHasPermission(List<DeleteDTO> deleteSuggestions) {
        Long operatorId = getOperatorId(); //当前用户id
        SysRoleTypeEnum userRole = getSysRoleType();
        if (userRole == null) {
            throw new KnowException("当前用户无权限删除建议");
        }
        if ((userRole == SysRoleTypeEnum.ADMIN) || (userRole == SysRoleTypeEnum.SUPER_SUPERVISOR)) {
            //管理员或机载领导可删除所有记录
            return;
        }
        //获取DeleteDTO中的id对应的 建议
        List<Suggestion> suggestionList = listByIds(deleteSuggestions.stream().map(DeleteDTO::getId).collect(Collectors.toList()));
        //检查suggestionList中的建议创建人,如果不是当前用户创建的,则抛出异常
        for (Suggestion suggestion : suggestionList) {
            if (!suggestion.getCreateUser().equals(operatorId)) {
                throw new KnowException("当前用户无权限删除ID为:%s 的建议".formatted(suggestion.getId()));
            }
        }
    }


    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public Suggestion updateSuggestionAndUpdateAppendFiles(SuggestionDTO suggestionDTO){
        Suggestion suggestion = objectMapper.convertValue(suggestionDTO, Suggestion.class);
        Suggestion returnSuggestion = updateByIDAndReturnObject(suggestion);
        List<AppendFileDTO> newModelFileList = CollectionUtils.isNotEmpty(suggestionDTO.getAppendFileList())?suggestionDTO.getAppendFileList():Collections.emptyList();
        appendFileService.updateAppendFiles(newModelFileList, returnSuggestion.getId(), Constant.YJFK_SUGGESTION_APPEND_FILES);
        return returnSuggestion;
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public void changeSuggestionStatusById(Long id, ChangeSuggestionStatusDTO changeSuggestionStatusDTO) {
        Long operatorId = getOperatorId(); //当前用户id
        SysRoleTypeEnum userRole = getSysRoleType();
        if (userRole == null) {
            throw new KnowException("当前用户无权限修改建议状态");
        }
        Suggestion suggestion = this.getById(id);
        if ((userRole == SysRoleTypeEnum.ADMIN) || (userRole == SysRoleTypeEnum.SUPER_SUPERVISOR) || (suggestion.getCreateUser().equals(operatorId))) {
            suggestion.setStatus(changeSuggestionStatusDTO.getNewStatus());
            suggestion.setVersion(changeSuggestionStatusDTO.getVersion());
            suggestion.setCreateDate(LocalDateTime.now());
            this.updateByIDAndReturnObject(suggestion);
            //在更新意见状态时,创建SuggestionStatus记录
            SuggestionStatus suggestionStatus = new SuggestionStatus().setSuggestionId(id).setStatus(changeSuggestionStatusDTO.getNewStatus());
            suggestionStatusService.saveAndReturnObject(suggestionStatus);
        }
        else {
            throw new KnowException("当前用户无权限修改ID为:%s 的建议状态".formatted(id));
        }
    }

    @Override
    @SneakyThrows
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public void updateSuggestionUpdateDateWithTransaction(Long id, LocalDateTime updateDate) {
        final LambdaUpdateWrapper<Suggestion> set = Wrappers.<Suggestion>lambdaUpdate().eq(Suggestion::getId, id).set(Suggestion::getUpdateDate, updateDate);
        this.update(set);
    }

    @Override
    public Suggestion findBySuggestionName(String title) {
        return getOne(Wrappers.<Suggestion>lambdaQuery().eq(Suggestion::getDescription, title));
    }

    @Override
    @SneakyThrows
    public IPage<SuggestionVO> listSuggestionVOByPage(IPage<Suggestion> page, String title){
        IPage<SuggestionVO> suggestionPage = this.getBaseMapper().getSuggestionVOPage(page, title);
        List<Long> fidList = suggestionPage.getRecords().stream().map(SuggestionVO::getId).collect(Collectors.toList());
        Map<Long, List<AppendFile>> appendFilesMap = appendFileService.getAppendFileMap(fidList,Constant.YJFK_SUGGESTION_APPEND_FILES);
        IPage<SuggestionVO> suggestionPageVOList = suggestionPage.convert(suggestion -> {
            SuggestionVO suggestionVO = objectMapper.convertValue(suggestion, SuggestionVO.class);
            suggestionVO.setAppendFileList(Optional.ofNullable(appendFilesMap.get(suggestion.getId())).map(list -> list.stream().map(type -> objectMapper.convertValue(type, AppendFileVO.class)).toList()).orElse(Collections.emptyList()));
            return suggestionVO;
        });
        return suggestionPageVOList;
    }


    @Override
    @SneakyThrows
    public SuggestionVO getSuggestionVOById(Long id){
        SuggestionVO suggestion = objectMapper.convertValue(getById(id), SuggestionVO.class);
        suggestion.setAppendFileList(appendFileService.list(Wrappers.<AppendFile>lambdaQuery().eq(AppendFile::getFId, id).eq(AppendFile::getType, Constant.YJFK_SUGGESTION_APPEND_FILES)).stream().map(type -> objectMapper.convertValue(type, AppendFileVO.class)).toList());
        return suggestion;
    }

}

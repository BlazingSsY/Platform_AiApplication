package com.starmol.circuitreview.backend.service.suggestion.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starmol.circuitreview.backend.bean.dto.AnswerDTO;
import com.starmol.circuitreview.backend.bean.dto.AppendFileDTO;
import com.starmol.circuitreview.backend.bean.vo.AnswerVO;
import com.starmol.circuitreview.backend.bean.vo.AppendFileVO;
import com.starmol.circuitreview.backend.constant.Constant;
import com.starmol.circuitreview.backend.constant.SysRoleTypeEnum;
import com.starmol.circuitreview.backend.exception.KnowException;
import com.starmol.circuitreview.backend.model.base.DeleteDTO;
import com.starmol.circuitreview.backend.model.suggestion.Answer;
import com.starmol.circuitreview.backend.model.suggestion.AppendFile;
import com.starmol.circuitreview.backend.model.suggestion.Suggestion;
import com.starmol.circuitreview.backend.repository.suggestion.AnswerMapper;
import com.starmol.circuitreview.backend.service.base.impl.BaseServiceImpl;
import com.starmol.circuitreview.backend.service.suggestion.AnswerService;
import com.starmol.circuitreview.backend.service.suggestion.AppendFileService;
import com.starmol.circuitreview.backend.service.suggestion.SuggestionService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * AnswerService接口的实现类
 *
 * @author Yuexiaopeng
 * @date 2019/11/21
 **/
@Service
@Slf4j
public class AnswerServiceImpl extends BaseServiceImpl<AnswerMapper, Answer> implements AnswerService {

    private final SuggestionService suggestionService;
    private final AppendFileService appendFileService;
    private final ObjectMapper objectMapper;

    @Lazy
    public AnswerServiceImpl(SuggestionService suggestionService, ObjectMapper objectMapper, AppendFileService appendFileService) {
        this.suggestionService = suggestionService;
        this.objectMapper = objectMapper;
        this.appendFileService = appendFileService;
    }

    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public Answer createAnswerAndUpdateAppendFiles(AnswerDTO answerDTO){
        Answer answer = objectMapper.convertValue(answerDTO, Answer.class);
        Answer returnAnswer = saveAndReturnObject(answer);
        if (CollectionUtils.isNotEmpty(answerDTO.getAppendFileList())) {
            appendFileService.createAppendFiles(answerDTO.getAppendFileList(), returnAnswer.getId(), Constant.YJFK_ANSWER_APPEND_FILES);
        }
        //更新建议的更新时间
        suggestionService.updateSuggestionUpdateDateWithTransaction(answer.getFId(), answer.getUpdateDate());
        return answer;
    }


    @Override
    @SneakyThrows
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public void deleteAnswersWithTransaction(List<DeleteDTO> deleteAnswers) {
        checkIfCurrentUserHasPermission(deleteAnswers);
        List<Long> fidList = getFidList(deleteAnswers);
        removeByIdsWithFill(deleteAnswers);
        appendFileService.deleteAppendFilesByFid(deleteAnswers.stream().map(DeleteDTO::getId).collect(Collectors.toList()));
        updateSuggestionUpdateDateByIdList(fidList);
    }

    private List<Long> getFidList(List<DeleteDTO> deleteAnswers){
        List<Long> idList = deleteAnswers.stream().map(DeleteDTO::getId).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(idList)) {
            List<Answer> answerList = listByIds(idList);
            return answerList.stream().map(Answer::getFId).distinct().collect(Collectors.toList());
        }
        else {
            return Collections.emptyList();
        }
    }

    @SneakyThrows
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public void updateSuggestionUpdateDateByIdList(List<Long> fidList) {
        for (Long id: fidList) {
            //获取fid为id的Answer列表最大的更新时间
            LocalDateTime lastUpdateDate = list(Wrappers.<Answer>lambdaQuery().eq(Answer::getFId, id).select(Answer::getUpdateDate)).stream().map(Answer::getUpdateDate).max(LocalDateTime::compareTo).orElse(null);

            if (Objects.nonNull(lastUpdateDate)) {
                //更新建议的更新时间
                suggestionService.updateSuggestionUpdateDateWithTransaction(id, lastUpdateDate);
            }
            else {
                Suggestion suggestion = suggestionService.getById(id);
                //更新建议的更新时间
                suggestionService.updateSuggestionUpdateDateWithTransaction(id, Optional.ofNullable(suggestion).map(Suggestion::getCreateDate).orElse(LocalDateTime.now()));
            }
        }
    }


    /**
     * 检查当前用户是否有权限进行操作,如果无权限则抛出异常
     * @param deleteSuggestions
     */
    private void checkIfCurrentUserHasPermission(List<DeleteDTO> deleteSuggestions) {
        Long operatorId = getOperatorId(); //当前用户id
        SysRoleTypeEnum userRole = getSysRoleType(); //当前用户
        if (userRole == null) {
            throw new KnowException("当前用户无权限删除回复");
        }
        if ((userRole == SysRoleTypeEnum.ADMIN) || (userRole == SysRoleTypeEnum.SUPER_SUPERVISOR)) {
            //管理员或机载领导可删除所有记录
            return;
        }
        //获取DeleteDTO中的id对应的 建议
        List<Answer> answerList = listByIds(deleteSuggestions.stream().map(DeleteDTO::getId).collect(Collectors.toList()));
        //检查suggestionList中的建议创建人,如果不是当前用户创建的,则抛出异常
        for (Answer answer : answerList) {
            if (!answer.getCreateUser().equals(operatorId)) {
                throw new KnowException("当前用户无权限删除ID为:%s 的回复".formatted(answer.getId()));
            }
        }
    }

    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public Answer updateAnswerAndUpdateAppendFiles(AnswerDTO answerDTO){
        Answer answer = objectMapper.convertValue(answerDTO, Answer.class);
        Answer returnAnswer = updateByIDAndReturnObject(answer);
        List<AppendFileDTO> newModelFileList = CollectionUtils.isNotEmpty(answerDTO.getAppendFileList())?answerDTO.getAppendFileList():Collections.emptyList();
        appendFileService.updateAppendFiles(newModelFileList, returnAnswer.getId(), Constant.YJFK_ANSWER_APPEND_FILES);
        return returnAnswer;
    }

    @Override
    public Answer findByAnswerName(String answerContent) {
        return getOne(Wrappers.<Answer>lambdaQuery().eq(Answer::getAnswer, answerContent));
    }

    @Override
    @SneakyThrows
    public IPage<AnswerVO> listAnswerVOByPage(IPage<Answer> page, Long fId, String answerContent){
        IPage<AnswerVO> answerPage = this.getBaseMapper().getAnswerVOPage(page, fId, answerContent);
        List<Long> fidList = answerPage.getRecords().stream().map(AnswerVO::getId).collect(Collectors.toList());
        Map<Long, List<AppendFile>> appendFilesMap = appendFileService.getAppendFileMap(fidList,Constant.YJFK_ANSWER_APPEND_FILES);
        //获取引用的回复的Map
        List<Long> refIdList = answerPage.getRecords().stream().map(AnswerVO::getRefId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        List<Answer> refAnswers =CollectionUtils.isEmpty(refIdList)?Collections.emptyList():this.list(Wrappers.<Answer>lambdaQuery().in(Answer::getId, refIdList));
        Map<Long, Answer> refAnswerMap = refAnswers.stream().collect(Collectors.toMap(Answer::getId, Function.identity()));
        IPage<AnswerVO> answerPageVOList = answerPage.convert(answer -> {
            AnswerVO answerVO = objectMapper.convertValue(answer, AnswerVO.class);
            answerVO.setAppendFileList(Optional.ofNullable(appendFilesMap.get(answer.getId())).map(list -> list.stream().map(type -> objectMapper.convertValue(type, AppendFileVO.class)).toList()).orElse(Collections.emptyList()));
            answerVO.setRefAnswer(Objects.isNull(answer.getRefId())?null:refAnswerMap.get(answer.getRefId()));
            return answerVO;
        });
        return answerPageVOList;
    }


    @Override
    @SneakyThrows
    public AnswerVO getAnswerVOById(Long id){
        AnswerVO answer = objectMapper.convertValue(getById(id), AnswerVO.class);
        answer.setAppendFileList(appendFileService.list(Wrappers.<AppendFile>lambdaQuery().eq(AppendFile::getFId, id).eq(AppendFile::getType, Constant.YJFK_ANSWER_APPEND_FILES)).stream().map(type -> objectMapper.convertValue(type, AppendFileVO.class)).toList());
        return answer;
    }

}

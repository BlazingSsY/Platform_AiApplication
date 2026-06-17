package com.starmol.circuitreview.backend.service.suggestion.impl;

import com.starmol.circuitreview.backend.bean.vo.SuggestionStatusVO;
import com.starmol.circuitreview.backend.model.base.DeleteDTO;
import com.starmol.circuitreview.backend.model.suggestion.SuggestionStatus;
import com.starmol.circuitreview.backend.repository.suggestion.SuggestionStatusMapper;
import com.starmol.circuitreview.backend.service.base.impl.BaseServiceImpl;
import com.starmol.circuitreview.backend.service.suggestion.SuggestionStatusService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;


/**
 * SuggestionStatusService接口的实现类
 *
 * @author Yuexiaopeng
 * @date 2019/11/21
 **/
@Service
@Slf4j
public class SuggestionStatusServiceImpl extends BaseServiceImpl<SuggestionStatusMapper, SuggestionStatus> implements SuggestionStatusService {

    @Override
    @SneakyThrows
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public void deleteSuggestionStatusWithTransaction(List<DeleteDTO> deleteSuggestionStatuss) {
        removeByIdsWithFill(deleteSuggestionStatuss);
    }


    @Override
    @SneakyThrows
    public List<SuggestionStatusVO> listSuggestionStatus(Long suggestionId){
        List<SuggestionStatusVO> suggestionStatusPage = this.getBaseMapper().getSuggestionStatusVOPage(suggestionId);
        return suggestionStatusPage;
    }

}

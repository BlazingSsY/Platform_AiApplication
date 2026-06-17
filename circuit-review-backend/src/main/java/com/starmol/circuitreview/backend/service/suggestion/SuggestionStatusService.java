package com.starmol.circuitreview.backend.service.suggestion;


import com.starmol.circuitreview.backend.bean.vo.SuggestionStatusVO;
import com.starmol.circuitreview.backend.model.base.DeleteDTO;
import com.starmol.circuitreview.backend.model.suggestion.SuggestionStatus;
import com.starmol.circuitreview.backend.service.base.BaseService;

import java.util.List;

/**
 * 反馈意见状态逻辑层
 *
 * @author Yuexiaopeng
 * @date 2018/12/27 14:36
 */
public interface SuggestionStatusService extends BaseService<SuggestionStatus> {


    /**
     * 用事务的方式删除反馈意见状态列表
     *
     * @param deleteSuggestionStatuss 要删除的反馈意见状态列表
     */
    void deleteSuggestionStatusWithTransaction(List<DeleteDTO> deleteSuggestionStatuss);


    /**
     * 根据反馈意见状态名查询反馈意见状态列表
     *
     * @return 反馈意见状态对象列表
     */
    List<SuggestionStatusVO> listSuggestionStatus(Long suggestionId);
}

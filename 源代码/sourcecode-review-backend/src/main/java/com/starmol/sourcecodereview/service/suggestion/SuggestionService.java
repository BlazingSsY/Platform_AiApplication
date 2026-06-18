package com.starmol.sourcecodereview.service.suggestion;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starmol.sourcecodereview.bean.dto.ChangeSuggestionStatusDTO;
import com.starmol.sourcecodereview.bean.dto.SuggestionDTO;
import com.starmol.sourcecodereview.bean.vo.SuggestionVO;
import com.starmol.sourcecodereview.model.base.DeleteDTO;
import com.starmol.sourcecodereview.model.suggestion.Suggestion;
import com.starmol.sourcecodereview.service.base.BaseService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 反馈意见逻辑层
 *
 * @author Yuexiaopeng
 * @date 2018/12/27 14:36
 */
public interface SuggestionService extends BaseService<Suggestion> {

    /**
     * 创建反馈意见对象,并更新附件文件(建立模型和主记录的关联)
     */
    Suggestion createSuggestionAndUpdateAppendFiles(SuggestionDTO SuggestionDTO);


    /**
     * 用事务的方式删除反馈意见列表
     *
     * @param deleteSuggestions 要删除的反馈意见列表
     */
    void  deleteSuggestionsWithTransaction(List<DeleteDTO> deleteSuggestions);



    /**
     * 更新反馈意见对象,并更新附件文件(建立模型和主记录的关联)
     */
    Suggestion updateSuggestionAndUpdateAppendFiles(SuggestionDTO SuggestionDTO);

    void changeSuggestionStatusById(Long id, ChangeSuggestionStatusDTO changeSuggestionStatusDTO);

    /**
     * 更新反馈意见的更新时间
     */
    void updateSuggestionUpdateDateWithTransaction(Long id, LocalDateTime updateDate);

    /**
     * 根据反馈意见名查询反馈意见
     *
     * @param description 反馈意见名(反馈意见名唯一)
     * @return 反馈意见对象
     */
    Suggestion findBySuggestionName(String description);


    /**
     * 根据反馈意见名查询反馈意见列表
     *
     * @param title 反馈意见名
     * @return 反馈意见对象列表
     */
    IPage<SuggestionVO> listSuggestionVOByPage(IPage<Suggestion> page, String title);

    /**
     * 根据id查询反馈意见对象
     * @param id
     * @return
     */
    SuggestionVO getSuggestionVOById(Long id);
}

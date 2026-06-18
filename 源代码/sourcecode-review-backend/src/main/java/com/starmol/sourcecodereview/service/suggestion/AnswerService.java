package com.starmol.sourcecodereview.service.suggestion;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starmol.sourcecodereview.bean.dto.AnswerDTO;
import com.starmol.sourcecodereview.bean.vo.AnswerVO;
import com.starmol.sourcecodereview.model.base.DeleteDTO;
import com.starmol.sourcecodereview.model.suggestion.Answer;
import com.starmol.sourcecodereview.service.base.BaseService;

import java.util.List;

/**
 * 回复逻辑层
 *
 * @author Yuexiaopeng
 * @date 2018/12/27 14:36
 */
public interface AnswerService extends BaseService<Answer> {

    /**
     * 创建回复对象,并更新附件文件(建立模型和主记录的关联)
     */
    Answer createAnswerAndUpdateAppendFiles(AnswerDTO AnswerDTO);


    /**
     * 用事务的方式删除回复列表
     *
     * @param deleteAnswers 要删除的回复列表
     */
    void  deleteAnswersWithTransaction(List<DeleteDTO> deleteAnswers);



    /**
     * 更新回复对象,并更新附件文件(建立模型和主记录的关联)
     */
    Answer updateAnswerAndUpdateAppendFiles(AnswerDTO AnswerDTO);

    /**
     * 根据回复内容查询回复
     *
     * @param answerContent 回复内容
     * @return 回复对象
     */
    Answer findByAnswerName(String answerContent);


    /**
     * 根据回复名查询回复列表
     *
     * @param answerContent 回复内容
     * @return 回复对象列表
     */
    IPage<AnswerVO> listAnswerVOByPage(IPage<Answer> page, Long fId, String answerContent);

    /**
     * 根据id查询回复对象
     * @param id
     * @return
     */
    AnswerVO getAnswerVOById(Long id);
}

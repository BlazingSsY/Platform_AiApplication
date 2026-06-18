package com.starmol.circuitreview.backend.service.experienceshare;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starmol.circuitreview.backend.bean.dto.ExperienceShareReplyDTO;
import com.starmol.circuitreview.backend.bean.vo.ExperienceShareReplyVO;
import com.starmol.circuitreview.backend.model.base.DeleteDTO;
import com.starmol.circuitreview.backend.model.experienceshare.ExperienceShareReply;
import com.starmol.circuitreview.backend.service.base.BaseService;

import java.util.List;

/**
 * 设计经验分享回复逻辑层
 *
 * @author system
 * @date 2026-06-04
 */
public interface ExperienceShareReplyService extends BaseService<ExperienceShareReply> {

    /**
     * 创建回复对象，并更新附件文件(建立模型和主记录的关联)
     */
    ExperienceShareReply createReplyAndUpdateAppendFiles(ExperienceShareReplyDTO dto);

    /**
     * 用事务的方式删除回复列表
     */
    void deleteRepliesWithTransaction(List<DeleteDTO> deleteReplies);

    /**
     * 更新回复对象，并更新附件文件
     */
    ExperienceShareReply updateReplyAndUpdateAppendFiles(ExperienceShareReplyDTO dto);

    /**
     * 根据回复内容查询回复
     */
    ExperienceShareReply findByReplyContent(String replyContent);

    /**
     * 分页查询回复列表
     */
    IPage<ExperienceShareReplyVO> listReplyVOByPage(IPage<ExperienceShareReply> page, Long fId, String replyContent);

    /**
     * 根据id查询回复详情VO
     */
    ExperienceShareReplyVO getReplyVOById(Long id);
}

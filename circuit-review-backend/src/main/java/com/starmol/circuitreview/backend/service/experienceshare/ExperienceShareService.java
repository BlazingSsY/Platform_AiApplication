package com.starmol.circuitreview.backend.service.experienceshare;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starmol.circuitreview.backend.bean.dto.ExperienceShareDTO;
import com.starmol.circuitreview.backend.bean.vo.ExperienceShareVO;
import com.starmol.circuitreview.backend.model.base.DeleteDTO;
import com.starmol.circuitreview.backend.model.experienceshare.ExperienceShare;
import com.starmol.circuitreview.backend.service.base.BaseService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 设计经验分享逻辑层
 *
 * @author system
 * @date 2026-05-27
 */
public interface ExperienceShareService extends BaseService<ExperienceShare> {

    /**
     * 创建设计经验分享，并更新附件文件
     */
    ExperienceShare createExperienceShareAndUpdateAppendFiles(ExperienceShareDTO dto);

    /**
     * 用事务的方式删除设计经验分享列表
     */
    void deleteExperienceSharesWithTransaction(List<DeleteDTO> deleteList);

    /**
     * 更新设计经验分享，并更新附件文件
     */
    ExperienceShare updateExperienceShareAndUpdateAppendFiles(ExperienceShareDTO dto);

    /**
     * 分页查询设计经验分享VO
     */
    IPage<ExperienceShareVO> listExperienceShareVOByPage(IPage<ExperienceShare> page, String title, String contributor, Long departmentId);

    /**
     * 根据id查询设计经验分享详情VO
     */
    ExperienceShareVO getExperienceShareVOById(Long id);

    /**
     * 点赞/取消点赞
     *
     * @param id 经验分享ID
     * @return 最新的点赞数
     */
    Integer toggleLike(Long id);

    /**
     * 获取当前用户对指定经验分享的点赞状态
     */
    Boolean getCurrentUserLikeStatus(Long experienceId);

    /**
     * 更新设计经验分享的最后更新时间
     *
     * @param id         经验分享ID
     * @param updateDate 更新时间
     */
    void updateUpdateDate(Long id, LocalDateTime updateDate);
}

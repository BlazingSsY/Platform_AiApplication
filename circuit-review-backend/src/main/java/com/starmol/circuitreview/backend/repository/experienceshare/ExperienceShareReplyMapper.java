package com.starmol.circuitreview.backend.repository.experienceshare;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starmol.circuitreview.backend.bean.vo.ExperienceShareReplyVO;
import com.starmol.circuitreview.backend.model.experienceshare.ExperienceShareReply;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 设计经验分享回复Mapper接口
 *
 * @author system
 * @date 2026-06-04
 */
public interface ExperienceShareReplyMapper extends BaseMapper<ExperienceShareReply> {

    @Select({"<script>",
            "SELECT a.*, u.name AS createUserName FROM sjjyfx_experience_share_reply a ",
            "LEFT JOIN urm_user u ON a.create_user = u.id AND a.is_delete = 0 AND u.is_delete = 0 ",
            "WHERE a.is_delete = 0 ",
            "<if test='fId != null'> ",
            "  AND a.f_id = #{fId} ",
            "</if> ",
            "<if test='replyContent!=null and replyContent!=\"\"'>",
            "  AND a.reply LIKE CONCAT('%', #{replyContent}, '%')",
            "</if>",
            "ORDER BY a.update_date ",
            "</script>"
    })
    IPage<ExperienceShareReplyVO> getReplyVOPage(IPage page, @Param("fId") Long fId, @Param("replyContent") String replyContent);
}

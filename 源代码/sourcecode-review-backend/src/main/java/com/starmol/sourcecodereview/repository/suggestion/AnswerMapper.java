package com.starmol.sourcecodereview.repository.suggestion;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starmol.sourcecodereview.bean.vo.AnswerVO;
import com.starmol.sourcecodereview.model.suggestion.Answer;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 意见回复Mapper接口
 *
 * @author system
 * @date 2025-01-07
 */
public interface AnswerMapper extends BaseMapper<Answer> {
    /**
     * @param page 翻页对象，可以作为 xml 参数直接使用，传递参数 Page 即自动分页
     */
    @Select({"<script>",
            "SELECT a.*,u.name AS createUserName FROM code_yjfk_answer a LEFT JOIN urm_user u ON a.create_user = u.id AND a.is_delete = 0 AND u.is_delete = 0 where a.is_delete=0 ",
            "<if test='fId != null'> ",
            "  AND a.f_id = #{fId} ",
            "</if> ",
            "<if test='answerContent!=null and answerContent!=\"\"'>",
            " and a.answer LIKE CONCAT('%', #{answerContent}, '%')",
            "</if>",
            " ORDER BY update_date ",
            "</script>"
    })
    IPage<AnswerVO> getAnswerVOPage(IPage page, @Param("fId") Long fId, @Param("answerContent") String answerContent);

} 
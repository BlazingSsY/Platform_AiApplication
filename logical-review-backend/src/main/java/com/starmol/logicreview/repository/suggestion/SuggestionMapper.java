package com.starmol.logicreview.repository.suggestion;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starmol.logicreview.bean.vo.SuggestionVO;
import com.starmol.logicreview.model.suggestion.Suggestion;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 反馈意见Mapper接口
 *
 * @author system
 * @date 2025-01-07
 */
public interface SuggestionMapper extends BaseMapper<Suggestion> {

    /**
     * @param page 翻页对象，可以作为 xml 参数直接使用，传递参数 Page 即自动分页
     */
    @Select({"<script>",
            "SELECT a.*,u.name AS createUserName FROM logic_yjfk_suggestion a LEFT JOIN urm_user u ON a.create_user = u.id AND a.is_delete = 0 AND u.is_delete = 0 where a.is_delete=0 ",
            "<if test='title!=null and title!=\"\"'>",
            " and a.title LIKE CONCAT('%', #{title}, '%')",
            "</if>",
            " ORDER BY update_date DESC ",
            "</script>"
    })
    IPage<SuggestionVO> getSuggestionVOPage(IPage page, @Param("title") String title);

} 
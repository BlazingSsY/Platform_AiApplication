package com.starmol.circuitreview.backend.repository.suggestion;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.starmol.circuitreview.backend.bean.vo.SuggestionStatusVO;
import com.starmol.circuitreview.backend.model.suggestion.SuggestionStatus;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 反馈意见状态Mapper接口
 *
 * @author system
 * @date 2025-01-07
 */
public interface SuggestionStatusMapper extends BaseMapper<SuggestionStatus> {

    /**
     * @param suggestionId 建议ID
     */
    @Select({"<script>",
            "SELECT a.*,u.name AS createUserName FROM yjfk_suggestion_status a LEFT JOIN urm_user u ON a.create_user = u.id AND a.is_delete = 0 AND u.is_delete = 0 where a.is_delete=0 ",
            "<if test='suggestionId != null'> ",
            "  AND a.suggestion_id = #{suggestionId} ",
            "</if> ",
            " ORDER BY create_date ",
            "</script>"
    })
    List<SuggestionStatusVO> getSuggestionStatusVOPage(@Param("suggestionId") Long suggestionId);
}
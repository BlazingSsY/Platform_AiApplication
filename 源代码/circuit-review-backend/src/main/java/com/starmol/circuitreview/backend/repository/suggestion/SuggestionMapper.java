package com.starmol.circuitreview.backend.repository.suggestion;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starmol.circuitreview.backend.bean.vo.SuggestionVO;
import com.starmol.circuitreview.backend.model.suggestion.Suggestion;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

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
            "SELECT a.*,u.name AS createUserName, b.id AS department_id, b.name AS department_name FROM yjfk_suggestion a LEFT JOIN urm_user u ON a.create_user = u.id AND a.is_delete = 0 AND u.is_delete = 0 ",
            " LEFT JOIN urm_department b ON u.department_id=b.id and b.is_delete=0 ",
            " where a.is_delete=0 ",
            "<if test='departmentIds != null and departmentIds.size() > 0'> ",
            "  AND b.id IN ",
            "  <foreach collection='departmentIds' item='deptId' open='(' separator=',' close=')'> ",
            "    #{deptId} ",
            "  </foreach> ",
            "</if> ",
            "<if test='userIds != null and userIds.size() > 0'> ",
            "  AND u.id IN ",
            "  <foreach collection='userIds' item='userId' open='(' separator=',' close=')'> ",
            "    #{userId} ",
            "  </foreach> ",
            "</if> ",
            "<if test='statusValue != null'> ",
            "  AND a.status = #{statusValue} ",
            "</if> ",
            "<if test='title!=null and title!=\"\"'>",
            " and a.title LIKE CONCAT('%', #{title}, '%')",
            "</if>",
            " ORDER BY update_date DESC ",
            "</script>"
    })
    IPage<SuggestionVO> getSuggestionVOPage(IPage page, @Param("departmentIds") List<Long> departmentIds, @Param("userIds") List<Long> userIds, @Param("statusValue") Integer statusValue, @Param("title") String title);

} 
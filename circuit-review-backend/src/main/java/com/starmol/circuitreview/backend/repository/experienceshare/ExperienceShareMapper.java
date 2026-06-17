package com.starmol.circuitreview.backend.repository.experienceshare;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starmol.circuitreview.backend.model.experienceshare.ExperienceShare;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 设计经验分享Mapper接口
 *
 * @author system
 * @date 2026-05-27
 */
public interface ExperienceShareMapper extends BaseMapper<ExperienceShare> {

    /**
     * 分页查询设计经验分享VO
     */
    @Select({"<script>",
            "SELECT a.*, u.name AS createUserName, b.id AS department_id, b.name AS department_name ",
            "FROM sjjyfx_experience_share a ",
            "LEFT JOIN urm_user u ON a.create_user = u.id AND a.is_delete = 0 AND u.is_delete = 0 ",
            "LEFT JOIN urm_department b ON u.department_id = b.id AND b.is_delete = 0 ",
            "WHERE a.is_delete = 0 ",
            "<if test='title!=null and title!=\"\"'>",
            " AND a.title LIKE CONCAT('%', #{title}, '%')",
            "</if>",
            "<if test='contributor!=null and contributor!=\"\"'>",
            " AND a.contributor LIKE CONCAT('%', #{contributor}, '%')",
            "</if>",
            "<if test='departmentId!=null'>",
            " AND u.department_id = #{departmentId}",
            "</if>",
            "ORDER BY a.update_date DESC",
            "</script>"
    })
    IPage<com.starmol.circuitreview.backend.bean.vo.ExperienceShareVO> getExperienceShareVOPage(
            IPage page,
            @Param("title") String title,
            @Param("contributor") String contributor,
            @Param("departmentId") Long departmentId);
}

package com.starmol.circuitreview.backend.repository.circuitreview;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starmol.circuitreview.backend.bean.vo.CircuitFileDetailVO;
import com.starmol.circuitreview.backend.constant.CircuitFileSortFieldEnum;
import com.starmol.circuitreview.backend.constant.SortDirectionEnum;
import com.starmol.circuitreview.backend.model.circuitreview.CircuitFile;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 电路图文件Mapper接口
 *
 * @author system
 * @date 2025-01-07
 */
public interface CircuitFileMapper extends BaseMapper<CircuitFile> {

    /**
     * 分页查询文件详情，包括最新的审查结果
     *
     * @param page     分页参数
     * @param userId   用户ID
     * @param fileName 文件名（可选，用于模糊查询）
     * @return 文件详情分页结果
     */
    @Select({"<script>",
            "WITH ranked_versions AS ( ",
            "         SELECT ",
            "             v.id            AS file_version_id, ",
            "             v.file_id, ",
            "             v.minio_id, ",
            "             v.file_name AS file_version_name, ",
            "             v.file_version, ",
            "             v.create_date, ",
            "             (CASE WHEN r.is_delete = 0 THEN COALESCE(r.review_time, v.create_date) ELSE v.create_date END)  AS active_time, ",
            "             r.id AS result_id, ",
            "             (CASE WHEN r.is_delete = 0 THEN r.check_points ELSE null END) AS check_points, ",
            "             (CASE WHEN r.is_delete = 0 THEN r.pass_check_points ELSE null END) AS pass_check_points, ",
            "             (CASE WHEN r.is_delete = 0 THEN r.pass_rate ELSE null END) AS pass_rate, ",
            "             (CASE WHEN r.is_delete = 0 THEN r.is_closed_loop ELSE null END) AS is_closed_loop, ",
            "             (CASE WHEN r.is_delete = 0 THEN r.review_time ELSE null END) AS review_time, ",
            "             r.is_delete, ",
            "             ROW_NUMBER() OVER ( ", // 为每个文件版本分配审核结果排名
            "                 PARTITION BY v.id ",
            "                 ORDER BY r.review_time DESC NULLS LAST ",
            "            ) AS review_rank ",
            "         FROM   dlsc_file_version v ",
            "                    LEFT JOIN dlsc_review_result r ON r.file_version_id = v.id AND r.status != 3 ",
            "         WHERE  v.is_delete  = 0 AND v.is_recycle = 0 ",
            "     ), ",
            "     latest_version_per_file AS ( ",
            "         SELECT DISTINCT ON (file_id) * ",
            "         FROM   ranked_versions ",
            "         ORDER  BY file_id, file_version DESC, review_rank ",
            "     ) ",
            "SELECT ",
            "    f.id, ",
            "    lv.file_version_id AS file_version_id, ",
            "    f.secret_level, ",
            "    lv.minio_id, ",
            "    lv.file_version, ",
            "    f.file_name, ",
            "    f.compatible_models, ",
            "    f.product_model, ",
            "    f.product_name, ",
            "    f.diagram_number, ",
            "    f.diagram_version, ",
            "    lv.file_version_name           AS file_version_name, ",
            "    f.department_id, ",
            "    d.name as department_name, ",
            "    f.owner_id, ",
            "    u.name as owner_name, ",
            "    f.is_recycle, ",
            "    f.comments, ",
            "    lv.result_id, ",
            "    lv.check_points, ",
            "    lv.pass_check_points, ",
            "    (lv.check_points - lv.pass_check_points) AS fail_check_points, ",
            "    lv.pass_rate, ",
            "    lv.is_closed_loop, ",
            "    lv.review_time, ",
            "    f.create_date, ",
            "    lv.create_date         AS last_version_create_date, ",
            "    lv.active_time         AS sort_time ",
            "FROM   dlsc_file f ",
            "LEFT JOIN latest_version_per_file lv ON lv.file_id = f.id ",
            "LEFT JOIN urm_department d ON f.department_id = d.id  ",
            "LEFT JOIN urm_user u ON f.owner_id = u.id  ",
            "WHERE f.is_recycle = 0 AND f.is_delete = 0 ",
            "<if test='departmentIds != null and departmentIds.size() > 0'> ",
            "  AND f.department_id IN ",
            "  <foreach collection='departmentIds' item='deptId' open='(' separator=',' close=')'> ",
            "    #{deptId} ",
            "  </foreach> ",
            "</if> ",
            "<if test='userId != null'> ",
            "  AND f.owner_id = #{userId} ",
            "</if> ",
            "<if test='fileName != null and fileName != \"\"'> ",
            "  AND f.file_name LIKE CONCAT('%', #{fileName}, '%') ",
            "</if> ",
            "<choose>",
            "  <when test='sortField != null'>",
            "    <choose>",
            "      <when test='sortField.name() == \"FILE_NAME\"'>",
            "        ORDER BY f.file_name ",
            "        <if test='sortDirection != null and sortDirection.name() == \"DESC\"'>DESC NULLS LAST</if>",
            "        <if test='sortDirection == null or sortDirection.name() == \"ASC\"'>ASC</if>",
            "      </when>",
            "      <when test='sortField.name() == \"DEPARTMENT_NAME\"'>",
            "        ORDER BY d.name ",
            "        <if test='sortDirection != null and sortDirection.name() == \"DESC\"'>DESC NULLS LAST</if>",
            "        <if test='sortDirection == null or sortDirection.name() == \"ASC\"'>ASC</if>",
            "      </when>",
            "      <when test='sortField.name() == \"OWNER_NAME\"'>",
            "        ORDER BY u.name ",
            "        <if test='sortDirection != null and sortDirection.name() == \"DESC\"'>DESC NULLS LAST</if>",
            "        <if test='sortDirection == null or sortDirection.name() == \"ASC\"'>ASC</if>",
            "      </when>",
            "      <when test='sortField.name() == \"CHECK_POINTS\"'>",
            "        ORDER BY lv.check_points ",
            "        <if test='sortDirection != null and sortDirection.name() == \"DESC\"'>DESC NULLS LAST</if>",
            "        <if test='sortDirection == null or sortDirection.name() == \"ASC\"'>ASC</if>",
            "      </when>",
            "      <when test='sortField.name() == \"PASS_CHECK_POINTS\"'>",
            "        ORDER BY lv.pass_check_points ",
            "        <if test='sortDirection != null and sortDirection.name() == \"DESC\"'>DESC NULLS LAST</if>",
            "        <if test='sortDirection == null or sortDirection.name() == \"ASC\"'>ASC</if>",
            "      </when>",
            "      <when test='sortField.name() == \"FAIL_CHECK_POINTS\"'>",
            "        ORDER BY (lv.check_points - lv.pass_check_points) ",
            "        <if test='sortDirection != null and sortDirection.name() == \"DESC\"'>DESC NULLS LAST</if>",
            "        <if test='sortDirection == null or sortDirection.name() == \"ASC\"'>ASC</if>",
            "      </when>",
            "      <when test='sortField.name() == \"PASS_RATE\"'>",
            "        ORDER BY lv.pass_rate ",
            "        <if test='sortDirection != null and sortDirection.name() == \"DESC\"'>DESC NULLS LAST</if>",
            "        <if test='sortDirection == null or sortDirection.name() == \"ASC\"'>ASC</if>",
            "      </when>",
            "      <when test='sortField.name() == \"REVIEW_TIME\"'>",
            "        ORDER BY lv.review_time ",
            "        <if test='sortDirection != null and sortDirection.name() == \"DESC\"'>DESC NULLS LAST</if>",
            "        <if test='sortDirection == null or sortDirection.name() == \"ASC\"'>ASC</if>",
            "      </when>",
            "      <when test='sortField.name() == \"COMPATIBLE_MODELS\"'>",
            "        ORDER BY f.compatible_models ",
            "        <if test='sortDirection != null and sortDirection.name() == \"DESC\"'>DESC NULLS LAST</if>",
            "        <if test='sortDirection == null or sortDirection.name() == \"ASC\"'>ASC</if>",
            "      </when>",
            "      <when test='sortField.name() == \"PRODUCT_MODEL\"'>",
            "        ORDER BY f.product_model ",
            "        <if test='sortDirection != null and sortDirection.name() == \"DESC\"'>DESC NULLS LAST</if>",
            "        <if test='sortDirection == null or sortDirection.name() == \"ASC\"'>ASC</if>",
            "      </when>",
            "      <when test='sortField.name() == \"PRODUCT_NAME\"'>",
            "        ORDER BY f.product_name ",
            "        <if test='sortDirection != null and sortDirection.name() == \"DESC\"'>DESC NULLS LAST</if>",
            "        <if test='sortDirection == null or sortDirection.name() == \"ASC\"'>ASC</if>",
            "      </when>",
            "      <when test='sortField.name() == \"DIAGRAM_NUMBER\"'>",
            "        ORDER BY f.diagram_number ",
            "        <if test='sortDirection != null and sortDirection.name() == \"DESC\"'>DESC NULLS LAST</if>",
            "        <if test='sortDirection == null or sortDirection.name() == \"ASC\"'>ASC</if>",
            "      </when>",
            "      <when test='sortField.name() == \"DIAGRAM_VERSION\"'>",
            "        ORDER BY f.diagram_version ",
            "        <if test='sortDirection != null and sortDirection.name() == \"DESC\"'>DESC NULLS LAST</if>",
            "        <if test='sortDirection == null or sortDirection.name() == \"ASC\"'>ASC</if>",
            "      </when>",
            "      <otherwise>",
            "        ORDER BY lv.active_time DESC NULLS LAST ",
            "      </otherwise>",
            "    </choose>",
            "  </when>",
            "  <otherwise>",
            "    ORDER BY lv.active_time DESC NULLS LAST ",
            "  </otherwise>",
            "</choose>",
            "</script>"
    })
    IPage<CircuitFileDetailVO> selectFileDetailPage(Page<CircuitFileDetailVO> page,
                                                    @Param("departmentIds") List<Long> departmentIds,
                                                    @Param("userId") Long userId,
                                                    @Param("fileName") String fileName,
                                                    @Param("sortField") CircuitFileSortFieldEnum sortField,
                                                    @Param("sortDirection") SortDirectionEnum sortDirection);

    /**
     * 查询回收站中的电路图文件，需要满足条件：
     * isRecycle=1 或 isDelete=1
     * 同时CircuitFile的id所对应的CircuitReviewResult的isDelete=0
     * 结果通过CircuitFile的createDate倒序排列
     *
     * @param page     分页参数
     * @param depId    部门ID
     * @param userId   用户ID
     * @param fileName 文件名
     * @return 电路图文件详情分页结果
     */
    @Select({"<script>",
            "SELECT ",
            "    f.id, ",
            "    f.id as file_id, ",
            "    f.file_id as minio_id, ",
            "    f.file_name, ",
            "    f.secret_level, ",
            "    f.department_id, ",
            "    d.name as department_name, ",
            "    f.owner_id, ",
            "    u.name as owner_name, ",
            "    f.is_recycle, ",
            "    f.comments, ",
            "    f.create_date, ",
            "    f.update_date ",
            "FROM   dlsc_file f ",
            "LEFT JOIN urm_department d ON f.department_id = d.id  ",
            "LEFT JOIN urm_user u ON f.owner_id = u.id  ",
            "WHERE (f.is_recycle = 1 OR f.is_delete = 1) ",
            "  AND EXISTS (",
            "    SELECT 1 FROM dlsc_review_result r ",
            "    WHERE r.file_id = f.id AND r.status = 2 AND r.is_delete = 0 ",
            "  ) ",
            "<if test='depId != null'> ",
            "  AND f.department_id = #{depId} ",
            "</if> ",
            "<if test='userId != null'> ",
            "  AND f.owner_id = #{userId} ",
            "</if> ",
            "<if test='fileName != null and fileName != \"\"'> ",
            "  AND f.file_name LIKE CONCAT('%', #{fileName}, '%') ",
            "</if> ",
            "ORDER BY f.create_date DESC ",
            "</script>"
    })
    IPage<CircuitFileDetailVO> selectRecycledCircuitFileWithReviewResult(Page<CircuitFileDetailVO> page,
                                                                         @Param("depId") Long depId,
                                                                         @Param("userId") Long userId,
                                                                         @Param("fileName") String fileName);
} 
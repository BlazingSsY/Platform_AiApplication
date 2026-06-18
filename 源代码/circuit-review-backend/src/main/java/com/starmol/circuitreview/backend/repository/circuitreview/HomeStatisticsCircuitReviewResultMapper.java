package com.starmol.circuitreview.backend.repository.circuitreview;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.starmol.circuitreview.backend.bean.vo.CircuitReviewHomeStatisticsDataItemVO;
import com.starmol.circuitreview.backend.model.circuitreview.CircuitReviewResult;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface HomeStatisticsCircuitReviewResultMapper extends BaseMapper<CircuitReviewResult> {
    /**
     * 获取最近审查的文件列表
     *
     * @param departmentIds 部门ID列表
     * @param userId        用户ID（可选，用于普通用户过滤）
     * @param limit         限制数量
     * @return 最近审查的文件列表
     */
    @Select({"<script>",
            "SELECT ",
            "    v.file_name, ",
            "    v.department_id, ",
            "    v.is_recycle, ",
            "    d.name as department_name, ",
            "    v.owner_id, ",
            "    u.name as owner_name, ",
            "    r.id as result_id, ",
            "    r.check_points, ",
            "    r.pass_check_points, ",
            "    (r.check_points - r.pass_check_points) AS fail_check_points, ",
            "    r.pass_rate, ",
            "    r.is_closed_loop, ",
            "    r.review_time, ",
            "    r.status",
            "FROM ( ",
            "    SELECT DISTINCT ON (file_version_id) ",
            "        id, ",
            "        file_id, ",
            "        file_version_id, ",
            "        check_points, ",
            "        pass_check_points, ",
            "        pass_rate, ",
            "        is_closed_loop, ",
            "        review_time, ",
            "        status ",
            "    FROM dlsc_review_result where status = 2 AND is_delete=0 ",
            "    ORDER BY file_version_id DESC, review_time DESC ",
            ") r ",
            "INNER JOIN dlsc_file_version v ON r.file_version_id = v.id ",
            "LEFT JOIN urm_department d ON v.department_id = d.id ",
            "LEFT JOIN urm_user u ON v.owner_id = u.id ",
            "WHERE v.is_delete = 0 AND v.file_name NOT LIKE 'test-%' ",
            "<if test='departmentIds != null and departmentIds.size() > 0'> ",
            "  AND v.department_id IN ",
            "  <foreach collection='departmentIds' item='deptId' open='(' separator=',' close=')'> ",
            "    #{deptId} ",
            "  </foreach> ",
            "</if> ",
            "<if test='userId != null'> ",
            "  AND v.owner_id = #{userId} ",
            "</if> ",
            "ORDER BY r.review_time DESC ",
            "LIMIT #{limit} ",
            "</script>"
    })
    List<CircuitReviewHomeStatisticsDataItemVO> getRecentlyReviewedFiles(
            @Param("departmentIds") List<Long> departmentIds,
            @Param("userId") Long userId,
            @Param("limit") Integer limit
    );

    /**
     * 获取通过率最高的文件列表
     *
     * @param departmentIds 部门ID列表
     * @param userId        用户ID（可选，用于普通用户过滤）
     * @param limit         限制数量
     * @return 通过率最高的文件列表
     */
    @Select({"<script>",
            "SELECT ",
            "    v.file_name, ",
            "    v.department_id, ",
            "    v.is_recycle, ",
            "    d.name as department_name, ",
            "    v.owner_id, ",
            "    u.name as owner_name, ",
            "    r.id as result_id, ",
            "    r.check_points, ",
            "    r.pass_check_points, ",
            "    r.pass_rate, ",
            "    r.review_time, ",
            "    r.status",
            "FROM ( ",
            "    SELECT DISTINCT ON (file_version_id) ",
            "        id, ",
            "        file_id, ",
            "        file_version_id, ",
            "        check_points, ",
            "        pass_check_points, ",
            "        pass_rate, ",
            "        review_time, ",
            "        status ",
            "    FROM dlsc_review_result where status = 2 AND is_delete=0",
            "    ORDER BY file_version_id DESC, review_time DESC ",
            ") r ",
            "INNER JOIN dlsc_file_version v ON r.file_version_id = v.id ",
            "LEFT JOIN urm_department d ON v.department_id = d.id ",
            "LEFT JOIN urm_user u ON v.owner_id = u.id ",
            "WHERE v.is_delete = 0 AND v.file_name NOT LIKE 'test-%' ",
            "  AND r.pass_rate IS NOT NULL ",
            "<if test='departmentIds != null and departmentIds.size() > 0'> ",
            "  AND v.department_id IN ",
            "  <foreach collection='departmentIds' item='deptId' open='(' separator=',' close=')'> ",
            "    #{deptId} ",
            "  </foreach> ",
            "</if> ",
            "<if test='userId != null'> ",
            "  AND v.owner_id = #{userId} ",
            "</if> ",
            "ORDER BY r.pass_rate DESC, r.review_time DESC ",
            "LIMIT #{limit} ",
            "</script>"
    })
    List<CircuitReviewHomeStatisticsDataItemVO> getHighestPassRateFiles(
            @Param("departmentIds") List<Long> departmentIds,
            @Param("userId") Long userId,
            @Param("limit") Integer limit
    );

    /**
     * 按部门统计全部文件数量
     *
     * @param departmentIds 部门ID列表
     * @param userId        用户ID（可选，用于普通用户过滤）
     * @return 部门名称和文件数量的映射
     */
    @Select({"<script>",
            "SELECT ",
            "    d.name as department_name, ",
            "    COUNT(DISTINCT f.id) as file_count ",
            "FROM dlsc_file f ",
            "LEFT JOIN urm_department d ON f.department_id = d.id ",
            "WHERE f.is_delete = 0 AND f.file_name NOT LIKE 'test-%' ",
            "<if test='departmentIds != null and departmentIds.size() > 0'> ",
            "  AND f.department_id IN ",
            "  <foreach collection='departmentIds' item='deptId' open='(' separator=',' close=')'> ",
            "    #{deptId} ",
            "  </foreach> ",
            "</if> ",
            "<if test='userId != null'> ",
            "  AND f.owner_id = #{userId} ",
            "</if> ",
            "GROUP BY d.name ",
            "ORDER BY department_name ASC ",
            "</script>"
    })
    List<Map<String, Object>> getTotalFilesCountByDepartment(
            @Param("departmentIds") List<Long> departmentIds,
            @Param("userId") Long userId
    );

    /**
     * 按部门用户统计全部文件数量
     *
     * @param departmentIds 部门ID列表
     * @return 部门用户名称和文件数量的映射
     */
    @Select({"<script>",
            "SELECT ",
            "    u.name as user_name, ",
            "    COUNT(DISTINCT f.id) as file_count ",
            "FROM dlsc_file f ",
            "LEFT JOIN urm_user u ON f.owner_id = u.id ",
            "WHERE f.is_delete = 0 AND u.is_delete = 0 AND f.file_name NOT LIKE 'test-%' ",
            "<if test='departmentIds != null and departmentIds.size() > 0'> ",
            "  AND f.department_id IN ",
            "  <foreach collection='departmentIds' item='deptId' open='(' separator=',' close=')'> ",
            "    #{deptId} ",
            "  </foreach> ",
            "</if> ",
            "GROUP BY u.name ",
            "ORDER BY user_name ASC ",
            "</script>"
    })
    List<Map<String, Object>> getTotalFilesCountByDepartmentAdmin(
            @Param("departmentIds") List<Long> departmentIds
    );

    /**
     * 普通用户按月文件数量统计
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 按月文件数量统计
     */
    @Select({"<script>",
            "SELECT ",
            "    to_char(max_v.create_date, 'YYYY-MM') AS create_month, ",
            "    COUNT(*) AS file_count ",
            "FROM dlsc_file f ",
            "LEFT JOIN (",
            "    SELECT ",
            "        file_id, ",
            "        MAX(file_version) as max_file_version ",
            "    FROM dlsc_file_version ",
            "    WHERE is_delete = 0 ",
            "    GROUP BY file_id ",
            " ) max_fv ON f.id = max_fv.file_id ",
            "LEFT JOIN dlsc_file_version max_v ON max_fv.file_id = max_v.file_id AND max_fv.max_file_version = max_v.file_version ",
            "WHERE f.is_delete = 0 AND f.owner_id = #{userId} ",
            "  AND max_v.create_date &gt;= #{startDate} ",
            "  AND max_v.create_date &lt; #{endDate} ",
            "GROUP BY create_month ",
            "ORDER BY create_month ",
            "</script>"
    })
    List<Map<String, Object>> getFilesCountByNormalUser(
            @Param("userId") Long userId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    /**
     * 普通用户按月文件审查数量统计
     *
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 按月文件数量统计
     */
    @Select({"<script>",
            "SELECT ",
            "    to_char(r.review_time, 'YYYY-MM') AS review_month, ",
            "    COUNT(*) AS file_count_reviewed ",
            "FROM  dlsc_file f ",
            "    LEFT JOIN (SELECT DISTINCT ON (file_id) ",
            "        id, ",
            "        file_id, ",
            "        file_version_id, ",
            "        check_points, ",
            "        pass_check_points, ",
            "        pass_rate, ",
            "        is_closed_loop, ",
            "        review_time, ",
            "        status ",
            "    FROM dlsc_review_result ",
            "    WHERE is_delete = 0 ",
            "    ORDER BY file_id, file_version_id DESC, review_time DESC ",
            ") r ON f.id = r.file_id ",
            "WHERE f.is_delete = 0 AND f.owner_id = #{userId} ",
            "  AND (r.review_time IS NULL ",
            "  OR (r.review_time &gt;= #{startDate} ",
            "  AND r.review_time &lt; #{endDate})) ",
            "GROUP BY review_month ",
            "ORDER BY review_month ",
            "</script>"
    })
    List<Map<String, Object>> getReviewedFilesCountByNormalUser(
            @Param("userId") Long userId,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    /**
     * 按部门统计审查文件数量
     *
     * @param departmentIds 部门ID列表
     * @param userId        用户ID（可选，用于普通用户过滤）
     * @return 部门名称和文件数量的映射
     */
    @Select({"<script>",
            "SELECT ",
            "    d.name as department_name, ",
            "    COUNT(DISTINCT f.id) as file_count ",
            "FROM dlsc_review_result r ",
            "INNER JOIN dlsc_file f ON r.file_id = f.id ",
            "LEFT JOIN dlsc_file_version v ON r.file_version_id = v.id ",
            "LEFT JOIN urm_department d ON v.department_id = d.id ",
            "WHERE r.status = 2 AND r.is_delete=0 AND f.is_delete = 0 AND v.is_delete = 0 AND f.file_name NOT LIKE 'test-%' ",
            "<if test='departmentIds != null and departmentIds.size() > 0'> ",
            "  AND f.department_id IN ",
            "  <foreach collection='departmentIds' item='deptId' open='(' separator=',' close=')'> ",
            "    #{deptId} ",
            "  </foreach> ",
            "</if> ",
            "<if test='userId != null'> ",
            "  AND f.owner_id = #{userId} ",
            "</if> ",
            "GROUP BY d.name ",
            "ORDER BY department_name ASC ",
            "</script>"
    })
    List<Map<String, Object>> getReviewedFilesCountByDepartment(
            @Param("departmentIds") List<Long> departmentIds,
            @Param("userId") Long userId
    );

    /**
     * 按部门用户统计审查文件数量
     *
     * @param departmentIds 部门ID列表
     * @return 部门名称和文件数量的映射
     */
    @Select({"<script>",
            "SELECT ",
            "    u.name as user_name, ",
            "    COUNT(DISTINCT f.id) as file_count ",
            "FROM dlsc_review_result r ",
            "INNER JOIN dlsc_file f ON r.file_id = f.id ",
            "LEFT JOIN dlsc_file_version v ON r.file_version_id = v.id ",
            "LEFT JOIN urm_user u ON f.owner_id = u.id ",
            "WHERE r.status = 2 AND r.is_delete=0 AND f.is_delete = 0 AND v.is_delete = 0 AND u.is_delete = 0 AND f.file_name NOT LIKE 'test-%' ",
            "<if test='departmentIds != null and departmentIds.size() > 0'> ",
            "  AND f.department_id IN ",
            "  <foreach collection='departmentIds' item='deptId' open='(' separator=',' close=')'> ",
            "    #{deptId} ",
            "  </foreach> ",
            "</if> ",
            "GROUP BY u.name ",
            "ORDER BY user_name ASC ",
            "</script>"
    })
    List<Map<String, Object>> getReviewedFilesCountByDepartmentAdmin(
            @Param("departmentIds") List<Long> departmentIds
    );

    /**
     * 按部门统计审核不通过的规则数量
     *
     * @param departmentIds 部门ID列表
     * @param userId        用户ID（可选，用于普通用户过滤）
     * @return 部门名称和审核不通过规则数量的映射
     */
    @Select({"<script>",
            "SELECT ",
            "    d.name as department_name, ",
            "    (SUM(r.check_points) - SUM(r.pass_check_points)) as issue_count ",
            "FROM ( ",
            "    SELECT DISTINCT ON (fv.file_id) ",
            "        r.id, ",
            "        r.file_id, ",
            "        r.file_version_id, ",
            "        r.check_points, ",
            "        r.pass_check_points, ",
            "        r.pass_rate, ",
            "        r.review_time, ",
            "        r.status ",
            "    FROM dlsc_file_version fv ",
            "    INNER JOIN dlsc_review_result r ON fv.id = r.file_version_id ",
            "    WHERE r.status = 2 AND r.is_delete = 0 AND fv.is_delete = 0 ",
            "    ORDER BY fv.file_id, fv.file_version DESC, r.review_time DESC ",
            ") r ",
            "INNER JOIN dlsc_file f ON r.file_id = f.id ",
            "LEFT JOIN dlsc_file_version v ON r.file_version_id = v.id ",
            "LEFT JOIN urm_department d ON f.department_id = d.id ",
            "LEFT JOIN urm_user u ON f.owner_id = u.id ",
            "WHERE f.is_delete = 0 AND v.is_delete = 0 AND f.file_name NOT LIKE 'test-%'  ",
            "<if test='departmentIds != null and departmentIds.size() > 0'> ",
            "  AND f.department_id IN ",
            "  <foreach collection='departmentIds' item='deptId' open='(' separator=',' close=')'> ",
            "    #{deptId} ",
            "  </foreach> ",
            "</if> ",
            "<if test='userId != null'> ",
            "  AND f.owner_id = #{userId} ",
            "</if> ",
            "GROUP BY d.name ",
            "ORDER BY department_name ASC ",
            "</script>"
    })
    List<Map<String, Object>> getReviewIssueCountByDepartment(
            @Param("departmentIds") List<Long> departmentIds,
            @Param("userId") Long userId
    );

    /**
     * 按部门用户统计审核不通过的规则数量
     *
     * @param departmentIds 部门ID列表
     * @return 部门名称和审核不通过规则数量的映射
     */
    @Select({"<script>",
            "SELECT ",
            "    u.name as user_name, ",
            "    (SUM(r.check_points) - SUM(r.pass_check_points)) as issue_count ",
            "FROM ( ",
            "    SELECT DISTINCT ON (fv.file_id) ",
            "        r.id, ",
            "        r.file_id, ",
            "        r.file_version_id, ",
            "        r.check_points, ",
            "        r.pass_check_points, ",
            "        r.pass_rate, ",
            "        r.review_time, ",
            "        r.status ",
            "    FROM dlsc_file_version fv ",
            "    INNER JOIN dlsc_review_result r ON fv.id = r.file_version_id ",
            "    WHERE r.status = 2 AND r.is_delete = 0 AND fv.is_delete = 0 ",
            "    ORDER BY fv.file_id, fv.file_version DESC, r.review_time DESC ",
            ") r ",
            "INNER JOIN dlsc_file f ON r.file_id = f.id ",
            "LEFT JOIN dlsc_file_version v ON r.file_version_id = v.id ",
            "LEFT JOIN urm_user u ON f.owner_id = u.id ",
            "WHERE f.is_delete = 0 AND v.is_delete = 0 AND u.is_delete = 0 AND f.file_name NOT LIKE 'test-%'  ",
            "<if test='departmentIds != null and departmentIds.size() > 0'> ",
            "  AND f.department_id IN ",
            "  <foreach collection='departmentIds' item='deptId' open='(' separator=',' close=')'> ",
            "    #{deptId} ",
            "  </foreach> ",
            "</if> ",
            "GROUP BY u.name ",
            "ORDER BY user_name ASC ",
            "</script>"
    })
    List<Map<String, Object>> getReviewIssueCountByDepartmentAdmin(
            @Param("departmentIds") List<Long> departmentIds
    );

    /**
     * 按用户统计审核文件通过率
     *
     * @param userId 用户ID
     * @return 文件名称和审核通过率的映射
     */
    @Select({"<script>",
            "SELECT ",
            "    f.file_name, ",
            "    r.pass_rate ",
            "FROM  dlsc_file f ",
            "    LEFT JOIN (SELECT DISTINCT ON (file_id) ",
            "        id, ",
            "        file_id, ",
            "        file_version_id, ",
            "        check_points, ",
            "        pass_check_points, ",
            "        pass_rate, ",
            "        is_closed_loop, ",
            "        review_time, ",
            "        status, ",
            "        is_delete ",
            "    FROM dlsc_review_result ",
            "    WHERE is_delete = 0 ",
            "    ORDER BY file_id, file_version_id DESC, review_time DESC ",
            ") r ON f.id = r.file_id ",
            "WHERE f.is_delete = 0 AND r.status = 2 AND r.is_delete = 0 ",
            "    AND  f.owner_id = #{userId} ",
            " ORDER BY r.pass_rate DESC ",
            "</script>"
    })
    List<Map<String, Object>> getReviewFilePassRateByNormalUser(
            @Param("userId") Long userId
    );

    /**
     * 按用户统计失败的规则数量统计
     *
     * @param userId 用户ID
     * @return 规则名称和失败数量统计的映射
     */
    @Select({"<script>",
            "SELECT (CASE WHEN rule.name IS NOT NULL THEN rule.name ELSE rule2.name END) AS rule_name, rule_count ",
            "    FROM (SELECT d.rule_id, d.rule_code, COUNT(*) AS rule_count ",
            "        FROM dlsc_review_result_detail d ",
            "        WHERE result_id IN (SELECT id ",
            "            FROM dlsc_review_result r ",
            "            WHERE file_version_id IN (SELECT id ",
            "                FROM dlsc_file_version v ",
            "                WHERE v.is_delete = 0 AND v.owner_id = #{userId}) ",
            "            AND r.status = 2 AND r.is_delete = 0) ",
            "        AND d.is_passed = 0 ",
            "        GROUP BY d.rule_id, d.rule_code) subquery ",
            "LEFT JOIN dlsc_rule rule ON subquery.rule_id = rule.id ",
            "LEFT JOIN dlsc_rule rule2 ON subquery.rule_code = rule2.code AND subquery.rule_id IS NULL AND rule.id IS NULL ",
            "</script>"
    })
    List<Map<String, Object>> getFailedRuleCountByNormalUser(
            @Param("userId") Long userId
    );/**
     * 按部门统计用户数量
     *
     * @param departmentIds 部门ID列表
     * @param userIds        用户ID列表（可选，用于普通用户过滤）
     * @return 部门名称和用户数量的映射
     */
    @Select({"<script>",
            "SELECT d.name as department_name, COUNT(DISTINCT u.id) as user_count ",
            "FROM urm_user u ",
            "LEFT JOIN urm_department d ON u.department_id = d.id ",
            "WHERE u.is_delete = 0 ",
            "<if test='departmentIds != null and departmentIds.size() > 0'> ",
            "  AND u.department_id IN ",
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
            "GROUP BY d.name ",
            "ORDER BY department_name ASC ",
            "</script>"
    })
    List<Map<String, Object>> getUserCountByDepartment(
            @Param("departmentIds") List<Long> departmentIds,
            @Param("userIds") List<Long> userIds
    );
}

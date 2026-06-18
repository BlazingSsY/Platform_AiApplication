package com.starmol.circuitreview.backend.repository.circuitreview;

import com.starmol.circuitreview.backend.bean.vo.CircuitFileDetailVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface StatisticsCircuitReviewResultMapper {

    /**
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

    /**
     * 获取所有规则信息
     */
    @Select("SELECT id, code, type FROM dlsc_rule WHERE is_delete = 0")
    List<Map<String, Object>> getAllRules();

    /**
     * 获取统计分析页面的文件详情列表，基于每个文件的最新版本
     *
     * @param departmentIds 部门ID列表
     * @param userIds 用户ID列表
     * @param startDate     开始日期
     * @param endDate       结束日期
     * @return 文件详情列表
     */
    @Select({"<script>",
            "SELECT ",
            "    r.file_version_id, ",
            "    r.file_id, ",
            "    r.file_version, ",
            "    r.file_name, ",
            "    f.compatible_models, ",
            "    f.product_model, ",
            "    f.product_name, ",
            "    f.diagram_number, ",
            "    f.diagram_version, ",
            "    r.department_id, ",
            "    d.name AS department_name, ",
            "    r.owner_id, ",
            "    u.name AS owner_name, ",
            "    r.is_recycle, ",
            "    r.comments, ",
            "    r.create_date, ",
            "    r.update_date, ",
            "    r.id AS result_id, ",
            "    r.check_points AS current_version_check_points, ",
            "    GREATEST(f.max_check_points, r.check_points) AS check_points, ",
            "    r.pass_check_points, ",
            "    r.total_fail_check_points, ",
            "    CASE ",
            "        WHEN r.total_fail_check_points > GREATEST(f.max_check_points, r.check_points) ",
            "            THEN r.closed_fail_check_points - (r.total_fail_check_points - GREATEST(f.max_check_points, r.check_points)) ",
            "        ELSE r.closed_fail_check_points ",
            "        END AS closed_fail_check_points, ",
            "    LEAST(r.total_fail_check_points, GREATEST(f.max_check_points, r.check_points)) AS fail_check_points, ",
            "    r.pass_rate, ",
            "    r.review_time, ",
            "    r.is_closed_loop, ",
            "    r.status, ",
            "    CASE ",
            "        WHEN r.is_closed_loop = 0 ",
            "            AND EXTRACT(EPOCH FROM (NOW() - f.create_date)) / 86400 > 15 ",
            "            THEN 1 ",
            "        ELSE 0 ",
            "        END AS exceed_half_month_not_closed ",
            "FROM ( ",
            "    SELECT DISTINCT ON (fv.file_id) ",
            "        r.id, ",
            "        r.file_id, ",
            "        r.file_version_id, ",
            "        r.check_points, ",
            "        r.pass_check_points, ",
            "        r.pass_rate, ",
            "        r.total_fail_check_points, ",
            "        r.closed_fail_check_points, ",
            "        r.is_closed_loop, ",
            "        r.review_time, ",
            "        r.status, ",
            "        fv.file_version, ",
            "        fv.file_name, ",
            "        fv.department_id, ",
            "        fv.owner_id, ",
            "        fv.is_recycle, ",
            "        fv.comments, ",
            "        fv.create_date, ",
            "        fv.update_date ",
            "    FROM dlsc_review_result r ",
            "    LEFT JOIN dlsc_file_version fv ON fv.id = r.file_version_id ",
            "    WHERE r.status = 2 AND r.is_delete = 0 AND fv.is_delete = 0 ",
            "    ORDER BY fv.file_id, fv.file_version DESC, r.review_time DESC ",
            ") r ",
            "LEFT JOIN dlsc_file f ON r.file_id = f.id ",
            "LEFT JOIN urm_department d ON r.department_id = d.id ",
            "LEFT JOIN urm_user u ON r.owner_id = u.id ",
            "WHERE f.is_delete = 0 AND f.file_name NOT LIKE 'test-%' ",
            "<if test='departmentIds != null and departmentIds.size() > 0'> ",
            "  AND r.department_id IN ",
            "  <foreach collection='departmentIds' item='deptId' open='(' separator=',' close=')'> ",
            "    #{deptId} ",
            "  </foreach> ",
            "</if> ",
            "<if test='userIds != null and userIds.size() > 0'> ",
            "  AND r.owner_id IN ",
            "  <foreach collection='userIds' item='userId' open='(' separator=',' close=')'> ",
            "    #{userId} ",
            "  </foreach> ",
            "</if> ",
            "<if test='startDate != null'> ",
            "  AND r.review_time &gt;= #{startDate} ",
            "</if> ",
            "<if test='endDate != null'> ",
            "  AND r.review_time &lt; #{endDate} ",
            "</if> ",
            "ORDER BY r.review_time DESC ",
            "</script>"
    })
    List<CircuitFileDetailVO> getStatisticsFileDetailsByLatestVersion(
            @Param("departmentIds") List<Long> departmentIds,
            @Param("userIds") List<Long> userIds,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    /**
     * 基于每个文件最新版本按普通用户统计图表数据
     *
     * @param userIds 用户ID列表
     * @param startDate     开始日期
     * @param endDate       结束日期
     * @return 按用户统计的数据
     */
    @Select({"<script>",
            "SELECT ",
            "    u.name as user_name, ",
            "    COUNT(DISTINCT r.file_id) as file_count, ",
            "    SUM(CASE WHEN f.is_recycle = 0 AND r.status = 2 THEN 1 ELSE 0 END) AS current_file_count_reviewed, ",
            "    SUM(CASE WHEN f.is_recycle = 0 AND (r.status IS NULL OR r.status IN (1, 3)) THEN 1 ELSE 0 END) AS current_file_count_not_reviewed, ",
            "    SUM(r.check_points) as total_rule_count, ",
            "    SUM(r.pass_check_points) as pass_rule_count, ",
            "    SUM(r.closed_fail_check_points) as closed_fail_check_points, ",
            "    (SUM(r.check_points) - SUM(r.pass_check_points)) as fail_rule_count, ",
            "    SUM(CASE WHEN r.is_closed_loop = 1 THEN 1 ELSE 0 END) as closed_loop_count, ",
            "    SUM(CASE WHEN r.is_closed_loop = 0 THEN 1 ELSE 0 END) as non_closed_loop_count, ",
            "    AVG(r.pass_rate) as average_pass_rate ",
            "FROM ( ",
            "    SELECT DISTINCT ON (fv.file_id) ",
            "        r.id, ",
            "        r.file_id, ",
            "        r.file_version_id, ",
            "        r.check_points, ",
            "        r.pass_check_points, ",
            "        r.pass_rate, ",
            "        r.closed_fail_check_points, ",
            "        r.is_closed_loop, ",
            "        r.review_time, ",
            "        r.status, ",
            "        fv.file_version, ",
            "        fv.file_name, ",
            "        fv.department_id, ",
            "        fv.owner_id, ",
            "        fv.is_recycle, ",
            "        fv.comments, ",
            "        fv.create_date, ",
            "        fv.update_date ",
            "    FROM dlsc_review_result r ",
            "    LEFT JOIN dlsc_file_version fv ON fv.id = r.file_version_id ",
            "    WHERE r.status = 2 AND r.is_delete = 0 AND fv.is_delete = 0 ",
            "    ORDER BY fv.file_id, fv.file_version DESC, r.review_time DESC ",
            ") r ",
            "LEFT JOIN dlsc_file f ON r.file_id = f.id ",
            "LEFT JOIN urm_user u ON f.owner_id = u.id ",
            "WHERE f.is_delete = 0 AND f.file_name NOT LIKE 'test-%' ",
            "<if test='userIds != null and userIds.size() > 0'> ",
            "  AND f.owner_id IN ",
            "  <foreach collection='userIds' item='userId' open='(' separator=',' close=')'> ",
            "    #{userId} ",
            "  </foreach> ",
            "</if> ",
            "<if test='startDate != null'> ",
            "  AND r.review_time &gt;= #{startDate} ",
            "</if> ",
            "<if test='endDate != null'> ",
            "  AND r.review_time &lt; #{endDate} ",
            "</if> ",
            "GROUP BY u.name ",
            "ORDER BY file_count DESC ",
            "</script>"
    })
    List<Map<String, Object>> getStatisticsByNormalUserForLatestVersion(
            @Param("userIds") List<Long> userIds,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    /**
     * 基于每个文件最新版本按部门统计图表数据
     *
     * @param departmentIds 部门ID列表
     * @param userIds 用户ID列表
     * @param startDate     开始日期
     * @param endDate       结束日期
     * @return 按部门统计的数据
     */
    @Select({"<script>",
            "SELECT ",
            "    d.name as department_name, ",
            "    COUNT(DISTINCT r.file_id) as file_count, ",
            "    SUM(CASE WHEN f.is_recycle = 0 AND r.status = 2 THEN 1 ELSE 0 END) AS current_file_count_reviewed, ",
            "    SUM(CASE WHEN f.is_recycle = 0 AND (r.status IS NULL OR r.status IN (1, 3)) THEN 1 ELSE 0 END) AS current_file_count_not_reviewed, ",
            "    SUM(r.check_points) as total_rule_count, ",
            "    SUM(r.pass_check_points) as pass_rule_count, ",
            "    SUM(r.closed_fail_check_points) as closed_fail_check_points, ",
            "    (SUM(r.check_points) - SUM(r.pass_check_points)) as fail_rule_count, ",
            "    SUM(CASE WHEN r.is_closed_loop = 1 THEN 1 ELSE 0 END) as closed_loop_count, ",
            "    SUM(CASE WHEN r.is_closed_loop = 0 THEN 1 ELSE 0 END) as non_closed_loop_count, ",
            "    AVG(r.pass_rate) as average_pass_rate ",
            "FROM ( ",
            "    SELECT DISTINCT ON (fv.file_id) ",
            "        r.id, ",
            "        r.file_id, ",
            "        r.file_version_id, ",
            "        r.check_points, ",
            "        r.pass_check_points, ",
            "        r.pass_rate, ",
            "        r.closed_fail_check_points, ",
            "        r.is_closed_loop, ",
            "        r.review_time, ",
            "        r.status, ",
            "        fv.file_version, ",
            "        fv.file_name, ",
            "        fv.department_id, ",
            "        fv.owner_id, ",
            "        fv.is_recycle, ",
            "        fv.comments, ",
            "        fv.create_date, ",
            "        fv.update_date ",
            "    FROM dlsc_review_result r ",
            "    LEFT JOIN dlsc_file_version fv ON fv.id = r.file_version_id ",
            "    WHERE r.status = 2 AND r.is_delete = 0 AND fv.is_delete = 0 ",
            "    ORDER BY fv.file_id, fv.file_version DESC, r.review_time DESC ",
            ") r ",
            "LEFT JOIN dlsc_file f ON r.file_id = f.id ",
            "LEFT JOIN urm_department d ON f.department_id = d.id ",
            "LEFT JOIN urm_user u ON f.owner_id = u.id ",
            "WHERE f.is_delete = 0 AND f.file_name NOT LIKE 'test-%' ",
            "<if test='departmentIds != null and departmentIds.size() > 0'> ",
            "  AND f.department_id IN ",
            "  <foreach collection='departmentIds' item='deptId' open='(' separator=',' close=')'> ",
            "    #{deptId} ",
            "  </foreach> ",
            "</if> ",
            "<if test='userIds != null and userIds.size() > 0'> ",
            "  AND f.owner_id IN ",
            "  <foreach collection='userIds' item='userId' open='(' separator=',' close=')'> ",
            "    #{userId} ",
            "  </foreach> ",
            "</if> ",
            "<if test='startDate != null'> ",
            "  AND r.review_time &gt;= #{startDate} ",
            "</if> ",
            "<if test='endDate != null'> ",
            "  AND r.review_time &lt; #{endDate} ",
            "</if> ",
            "GROUP BY d.name ",
            "ORDER BY d.name ",
            "</script>"
    })
    List<Map<String, Object>> getStatisticsByDepartmentForLatestVersion(
            @Param("departmentIds") List<Long> departmentIds,
            @Param("userIds") List<Long> userIds,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    /**
     * 631admin基于每个文件最新版本按用户统计图表数据
     *
     * @param departmentIds 部门ID列表
     * @param userIds 用户ID列表
     * @param startDate     开始日期
     * @param endDate       结束日期
     * @return 按用户统计的数据
     */
    @Select({"<script>",
            "SELECT ",
            "    u.name as user_name, ",
            "    COUNT(DISTINCT r.file_id) as file_count, ",
            "    SUM(CASE WHEN f.is_recycle = 0 AND r.status = 2 THEN 1 ELSE 0 END) AS current_file_count_reviewed, ",
            "    SUM(CASE WHEN f.is_recycle = 0 AND (r.status IS NULL OR r.status IN (1, 3)) THEN 1 ELSE 0 END) AS current_file_count_not_reviewed, ",
            "    SUM(r.check_points) as total_rule_count, ",
            "    SUM(r.pass_check_points) as pass_rule_count, ",
            "    SUM(r.closed_fail_check_points) as closed_fail_check_points, ",
            "    (SUM(r.check_points) - SUM(r.pass_check_points)) as fail_rule_count, ",
            "    SUM(CASE WHEN r.is_closed_loop = 1 THEN 1 ELSE 0 END) as closed_loop_count, ",
            "    SUM(CASE WHEN r.is_closed_loop = 0 THEN 1 ELSE 0 END) as non_closed_loop_count, ",
            "    AVG(r.pass_rate) as average_pass_rate ",
            "FROM ( ",
            "    SELECT DISTINCT ON (fv.file_id) ",
            "        r.id, ",
            "        r.file_id, ",
            "        r.file_version_id, ",
            "        r.check_points, ",
            "        r.pass_check_points, ",
            "        r.pass_rate, ",
            "        r.closed_fail_check_points, ",
            "        r.is_closed_loop, ",
            "        r.review_time, ",
            "        r.status, ",
            "        fv.file_version, ",
            "        fv.file_name, ",
            "        fv.department_id, ",
            "        fv.owner_id, ",
            "        fv.is_recycle, ",
            "        fv.comments, ",
            "        fv.create_date, ",
            "        fv.update_date ",
            "    FROM dlsc_review_result r ",
            "    LEFT JOIN dlsc_file_version fv ON fv.id = r.file_version_id ",
            "    WHERE r.status = 2 AND r.is_delete = 0 AND fv.is_delete = 0 ",
            "    ORDER BY fv.file_id, fv.file_version DESC, r.review_time DESC ",
            ") r ",
            "LEFT JOIN dlsc_file f ON r.file_id = f.id ",
            "LEFT JOIN urm_department d ON f.department_id = d.id ",
            "LEFT JOIN urm_user u ON f.owner_id = u.id ",
            "WHERE f.is_delete = 0 AND f.file_name NOT LIKE 'test-%' ",
            "<if test='departmentIds != null and departmentIds.size() > 0'> ",
            "  AND f.department_id IN ",
            "  <foreach collection='departmentIds' item='deptId' open='(' separator=',' close=')'> ",
            "    #{deptId} ",
            "  </foreach> ",
            "</if> ",
            "<if test='userIds != null and userIds.size() > 0'> ",
            "  AND f.owner_id IN ",
            "  <foreach collection='userIds' item='userId' open='(' separator=',' close=')'> ",
            "    #{userId} ",
            "  </foreach> ",
            "</if> ",
            "<if test='startDate != null'> ",
            "  AND r.review_time &gt;= #{startDate} ",
            "</if> ",
            "<if test='endDate != null'> ",
            "  AND r.review_time &lt; #{endDate} ",
            "</if> ",
            "GROUP BY u.name ",
            "ORDER BY u.name ",
            "</script>"
    })
    List<Map<String, Object>> getStatisticsByUserForLatestVersion(
            @Param("departmentIds") List<Long> departmentIds,
            @Param("userIds") List<Long> userIds,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    /**
     * 基于每个文件最新版本按规则类型统计图表数据
     *
     * @param departmentIds 部门ID列表
     * @param userIds 用户ID列表
     * @param startDate     开始日期
     * @param endDate       结束日期
     * @return 按规则类型统计的数据
     */
    @Select({"<script>",
            "SELECT ",
            "    (CASE WHEN rule.type IS NOT NULL THEN rule.type ELSE rule2.type END) as rule_type, ",
            "    COUNT(DISTINCT r.file_id) as file_count, ",
            "    SUM(CASE WHEN f.is_recycle = 0 AND r.status = 2 THEN 1 ELSE 0 END) AS current_file_count_reviewed, ",
            "    SUM(CASE WHEN f.is_recycle = 0 AND (r.status IS NULL OR r.status IN (1, 3)) THEN 1 ELSE 0 END) AS current_file_count_not_reviewed, ",
            "    COUNT(detail.id) as total_rule_count, ",
            "    SUM(CASE WHEN detail.is_passed = 1 THEN 1 ELSE 0 END) as pass_rule_count, ",
            "    SUM(CASE WHEN detail.is_passed = 0 THEN 1 ELSE 0 END) as fail_rule_count, ",
            "    SUM(CASE WHEN r.is_closed_loop = 1 THEN 1 ELSE 0 END) as closed_loop_count, ",
            "    SUM(CASE WHEN r.is_closed_loop = 0 THEN 1 ELSE 0 END) as non_closed_loop_count, ",
            "    SUM(r.closed_fail_check_points) as closed_fail_check_points, ",
            "    AVG(r.pass_rate) as average_pass_rate ",
            "FROM ( ",
            "    SELECT DISTINCT ON (fv.file_id) ",
            "        r.id, ",
            "        r.file_id, ",
            "        r.file_version_id, ",
            "        r.check_points, ",
            "        r.pass_check_points, ",
            "        r.pass_rate, ",
            "        r.closed_fail_check_points, ",
            "        r.is_closed_loop, ",
            "        r.review_time, ",
            "        r.status, ",
            "        fv.file_version, ",
            "        fv.file_name, ",
            "        fv.department_id, ",
            "        fv.owner_id, ",
            "        fv.is_recycle, ",
            "        fv.comments, ",
            "        fv.create_date, ",
            "        fv.update_date ",
            "    FROM dlsc_review_result r ",
            "    LEFT JOIN dlsc_file_version fv ON fv.id = r.file_version_id ",
            "    WHERE r.status = 2 AND r.is_delete = 0 AND fv.is_delete = 0 ",
            "    ORDER BY fv.file_id, fv.file_version DESC, r.review_time DESC ",
            ") r ",
            "INNER JOIN dlsc_review_result_detail detail ON r.id = detail.result_id ",
            "LEFT JOIN dlsc_rule rule ON detail.rule_id = rule.id ",
            "LEFT JOIN dlsc_rule rule2 ON detail.rule_code = rule2.code AND rule.id IS NULL ",
            "LEFT JOIN dlsc_file f ON r.file_id = f.id ",
            "LEFT JOIN urm_user u ON f.owner_id = u.id ",
            "WHERE f.is_delete = 0 AND f.file_name NOT LIKE 'test-%' ",
            "<if test='departmentIds != null and departmentIds.size() > 0'> ",
            "  AND f.department_id IN ",
            "  <foreach collection='departmentIds' item='deptId' open='(' separator=',' close=')'> ",
            "    #{deptId} ",
            "  </foreach> ",
            "</if> ",
            "<if test='userIds != null and userIds.size() > 0'> ",
            "  AND f.owner_id IN ",
            "  <foreach collection='userIds' item='userId' open='(' separator=',' close=')'> ",
            "    #{userId} ",
            "  </foreach> ",
            "</if> ",
            "<if test='startDate != null'> ",
            "  AND r.review_time &gt;= #{startDate} ",
            "</if> ",
            "<if test='endDate != null'> ",
            "  AND r.review_time &lt; #{endDate} ",
            "</if> ",
            "GROUP BY (CASE WHEN rule.type IS NOT NULL THEN rule.type ELSE rule2.type END) ",
            "ORDER BY rule_type ",
            "</script>"
    })
    List<Map<String, Object>> getStatisticsByRuleTypeForLatestVersion(
            @Param("departmentIds") List<Long> departmentIds,
            @Param("userIds") List<Long> userIds,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    /**
     * 基于每个文件最新版本一次性获取所有时间维度（日、周、月、季、年）的统计数据
     * @param departmentIds 部门ID列表
     * @param userIds 用户ID列表
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 每条记录带day、week、month、quarter、year字段的统计数据
     */
    @Select({"<script>",
            "SELECT ",
            "    to_char(r.review_time, 'YYYY-MM-DD') as review_day, ",
            "    to_char(r.review_time, 'IYYY-IW') as review_week, ",
            "    to_char(r.review_time, 'YYYY-MM') as review_month, ",
            "    to_char(r.review_time, 'YYYY') || '-Q' || EXTRACT(QUARTER FROM r.review_time) as review_quarter, ",
            "    CAST(EXTRACT(YEAR FROM r.review_time) AS INTEGER) as review_year, ",
            "    COUNT(DISTINCT r.file_id) as file_count, ",
            "    SUM(CASE WHEN f.is_recycle = 0 AND r.status = 2 THEN 1 ELSE 0 END) AS current_file_count_reviewed, ",
            "    SUM(CASE WHEN f.is_recycle = 0 AND (r.status IS NULL OR r.status IN (1, 3)) THEN 1 ELSE 0 END) AS current_file_count_not_reviewed, ",
            "    SUM(r.check_points) as total_rule_count, ",
            "    SUM(r.pass_check_points) as pass_rule_count, ",
            "    SUM(r.closed_fail_check_points) as closed_fail_check_points, ",
            "    (SUM(r.check_points) - SUM(r.pass_check_points)) as fail_rule_count, ",
            "    SUM(CASE WHEN r.is_closed_loop = 1 THEN 1 ELSE 0 END) as closed_loop_count, ",
            "    SUM(CASE WHEN r.is_closed_loop = 0 THEN 1 ELSE 0 END) as non_closed_loop_count, ",
            "    AVG(r.pass_rate) as average_pass_rate ",
            "FROM ( ",
            "    SELECT DISTINCT ON (fv.file_id) ",
            "        r.id, ",
            "        r.file_id, ",
            "        r.file_version_id, ",
            "        r.check_points, ",
            "        r.pass_check_points, ",
            "        r.pass_rate, ",
            "        r.closed_fail_check_points, ",
            "        r.is_closed_loop, ",
            "        r.review_time, ",
            "        r.status, ",
            "        fv.file_version, ",
            "        fv.file_name, ",
            "        fv.department_id, ",
            "        fv.owner_id, ",
            "        fv.is_recycle, ",
            "        fv.comments, ",
            "        fv.create_date, ",
            "        fv.update_date ",
            "    FROM dlsc_review_result r ",
            "    LEFT JOIN dlsc_file_version fv ON fv.id = r.file_version_id ",
            "    WHERE r.status = 2 AND r.is_delete = 0 AND fv.is_delete = 0 ",
            "    ORDER BY fv.file_id, fv.file_version DESC, r.review_time DESC ",
            ") r ",
            "LEFT JOIN dlsc_file f ON r.file_id = f.id ",
            "LEFT JOIN urm_user u ON f.owner_id = u.id ",
            "WHERE f.is_delete = 0 AND f.file_name NOT LIKE 'test-%' ",
            "<if test='departmentIds != null and departmentIds.size() > 0'> ",
            "  AND f.department_id IN ",
            "  <foreach collection='departmentIds' item='deptId' open='(' separator=',' close=')'> ",
            "    #{deptId} ",
            "  </foreach> ",
            "</if> ",
            "<if test='userIds != null and userIds.size() > 0'> ",
            "  AND f.owner_id IN ",
            "  <foreach collection='userIds' item='userId' open='(' separator=',' close=')'> ",
            "    #{userId} ",
            "  </foreach> ",
            "</if> ",
            "<if test='startDate != null'> ",
            "  AND r.review_time &gt;= #{startDate} ",
            "</if> ",
            "<if test='endDate != null'> ",
            "  AND r.review_time &lt; #{endDate} ",
            "</if> ",
            "GROUP BY review_day, review_week, review_month, review_quarter, review_year ",
            "ORDER BY review_day ",
            "</script>"
    })
    List<Map<String, Object>> getStatisticsByTimeDimensionsForLatestVersion(
            @Param("departmentIds") List<Long> departmentIds,
            @Param("userIds") List<Long> userIds,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    /**
     * 获取统计数据用于导出
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 按部门统计的导出数据
     */
    @Select({"<script>",
            "WITH latest_review_results AS (",
            "    SELECT DISTINCT ON (fv.file_id) ",
            "        r.file_id, ",
            "        r.file_version_id, ",
            "        GREATEST(f.max_check_points, r.check_points) AS check_points, ",
            "        r.pass_check_points, ",
            "        LEAST(r.total_fail_check_points, GREATEST(f.max_check_points, r.check_points)) AS fail_check_points, ",
            "    CASE ",
            "        WHEN r.total_fail_check_points > GREATEST(f.max_check_points, r.check_points) ",
            "            THEN r.closed_fail_check_points - (r.total_fail_check_points - GREATEST(f.max_check_points, r.check_points)) ",
            "        ELSE r.closed_fail_check_points ",
            "        END AS closed_fail_check_points, ",
            "        r.review_time, ",
            "        r.is_closed_loop ",
            "    FROM dlsc_file_version fv ",
            "    INNER JOIN dlsc_review_result r ON fv.id = r.file_version_id ",
            "    LEFT JOIN dlsc_file f ON fv.file_id = f.id ",
            "    WHERE r.status = 2 AND r.is_delete = 0 AND fv.is_delete = 0 ",
            "    ORDER BY fv.file_id, fv.file_version DESC, r.review_time DESC ",  // 按文件ID分组，取最高版本号的审查结果
            "),",
            "filtered_latest_results AS (",
            "    SELECT * ",
            "    FROM latest_review_results ",
            "    <where>",
            "        <if test='startDate != null'>",
            "            review_time >= #{startDate} ",  // 对最新审查结果应用开始日期过滤
            "        </if>",
            "        <if test='endDate != null'>",
            "            AND review_time &lt; #{endDate} ",  // 对最新审查结果应用结束日期过滤
            "        </if>",
            "    </where>",
            "),",
            "file_version_info AS (",
            "    SELECT DISTINCT ON (v.file_id) ",
            "        v.id, ",  // 添加 v.id 以便后续可以使用 DISTINCT v.id
            "        v.file_id, ",
            "        v.department_id, ",
            "        d.name as department_name,",
            "        f.create_date ",
            "    FROM dlsc_file_version v ",
            "    LEFT JOIN urm_department d ON v.department_id = d.id ",
            "    LEFT JOIN dlsc_file f ON v.file_id = f.id ",  // 左连接dlsc_file表获取create_date
            "    WHERE v.is_delete = 0 AND f.is_delete = 0 AND f.file_name NOT LIKE 'test-%' ",
            "      AND v.file_id IN (SELECT file_id FROM filtered_latest_results) ",  // 只考虑被日期过滤后的文件
            "    <if test='departmentIds != null and departmentIds.size() > 0'>",
            "      AND v.department_id IN ",
            "      <foreach collection='departmentIds' item='deptId' open='(' separator=',' close=')'>",
            "        #{deptId} ",
            "      </foreach>",
            "    </if>",
            "    ORDER BY v.file_id, v.file_version DESC ",  // 按文件ID分组，取最高版本 - 与第一个查询保持一致
            "),",
            "department_file_counts AS (",
            "    SELECT ",
            "        department_id,",
            "        COUNT(DISTINCT id) as dept_file_count ",
            "    FROM file_version_info ",
            "    GROUP BY department_id",
            "),",
            "department_review_stats AS (",
            "    SELECT ",
            "        fvi.department_id,",
            "        SUM(flr.check_points) as total_check_points,",
            "        SUM(flr.pass_check_points) as total_pass_check_points,",
            "        SUM(flr.fail_check_points) as total_fail_check_points,",
            "        SUM(flr.closed_fail_check_points) as total_closed_fail_check_points ",
            "    FROM file_version_info fvi ",
            "    INNER JOIN filtered_latest_results flr ON fvi.file_id = flr.file_id ",
            "    GROUP BY fvi.department_id",
            "),",
            "closed_loop_files AS (",
            "    SELECT ",
            "        fvi.department_id,",
            "        COUNT(fvi.id) as closed_loop_count ",  // 使用id而不是file_id保持一致
            "    FROM file_version_info fvi ",
            "    INNER JOIN filtered_latest_results flr ON fvi.file_id = flr.file_id ",
            "    WHERE flr.is_closed_loop = 1 ",  // 问题已闭环
            "    GROUP BY fvi.department_id ",
            ") ",
            "SELECT ",
            "    fvi.department_id,",
            "    fvi.department_name,",
            "    dfc.dept_file_count as file_counts,",
            "    COALESCE(drs.total_check_points, 0) as total_check_points,",
            "    COALESCE(drs.total_pass_check_points, 0) as total_pass_check_points,",
            "    COALESCE(drs.total_fail_check_points, 0) as total_fail_check_points,",
            "    COALESCE(drs.total_closed_fail_check_points, 0) as total_closed_fail_check_points,",
            "    COALESCE(clf.closed_loop_count, 0) as closed_loop_file_counts ",
            "FROM (SELECT DISTINCT department_id, department_name FROM file_version_info) fvi ",
            "LEFT JOIN department_file_counts dfc ON fvi.department_id = dfc.department_id ",
            "LEFT JOIN department_review_stats drs ON fvi.department_id = drs.department_id ",
            "LEFT JOIN closed_loop_files clf ON fvi.department_id = clf.department_id ",
            "ORDER BY fvi.department_name ",
            "</script>"
    })
    List<Map<String, Object>> getStatisticsForExport(
            @Param("departmentIds") List<Long> departmentIds,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    /**
     * 统计所有部门超过半月未关闭的数量
     * @param departmentIds 部门ID列表，用于过滤数据范围
     * @return 所有部门超过半月未关闭数量的统计
     */
    @Select({"<script>",
            "WITH latest_review_results AS (",
            "    SELECT DISTINCT ON (fv.file_id) ",
            "        r.file_id, ",
            "        r.file_version_id, ",
            "        r.check_points, ",
            "        r.pass_check_points, ",
            "        (r.check_points - r.pass_check_points) AS fail_check_points, ",
            "        r.closed_fail_check_points, ",
            "        r.review_time, ",
            "        r.is_closed_loop, ",
            "        fv.department_id ",
            "    FROM dlsc_file_version fv ",
            "    INNER JOIN dlsc_review_result r ON fv.id = r.file_version_id ",
            "    WHERE r.status = 2 AND r.is_delete = 0 AND fv.is_delete = 0 ",
            "    ORDER BY fv.file_id, fv.file_version DESC, r.review_time DESC ",
            "),",
            "file_version_info AS (",
            "    SELECT DISTINCT ON (v.file_id) ",
            "        v.id, ",
            "        v.file_id, ",
            "        v.department_id, ",
            "        d.name as department_name,",
            "        f.create_date ",
            "    FROM dlsc_file_version v ",
            "    LEFT JOIN urm_department d ON v.department_id = d.id ",
            "    LEFT JOIN dlsc_file f ON v.file_id = f.id ",
            "    WHERE v.is_delete = 0 AND f.is_delete = 0 AND f.file_name NOT LIKE 'test-%' ",
            "    <if test='departmentIds != null and departmentIds.size() > 0'>",
            "      AND v.department_id IN ",
            "      <foreach collection='departmentIds' item='deptId' open='(' separator=',' close=')'>",
            "        #{deptId} ",
            "      </foreach>",
            "    </if>",
            "    ORDER BY v.file_id, v.file_version DESC ",
            "),",
            "exceed_half_month_not_closed AS (",
            "    SELECT ",
            "        fvi.department_id,",
            "        fvi.department_name,",
            "        COUNT(fvi.id) as exceed_count ",
            "    FROM file_version_info fvi ",
            "    INNER JOIN latest_review_results lrr ON fvi.file_id = lrr.file_id ",
            "    WHERE lrr.is_closed_loop = 0 ",
            "      AND EXTRACT(EPOCH FROM (NOW() - fvi.create_date))/86400 > 15 ",
            "    GROUP BY fvi.department_id, fvi.department_name ",
            ") ",
            "SELECT ",
            "    department_id,",
            "    department_name,",
            "    exceed_count ",
            "FROM exceed_half_month_not_closed ",
            "ORDER BY department_name ",
            "</script>"
    })
    List<Map<String, Object>> getExceedHalfMonthNotClosedCount(@Param("departmentIds") List<Long> departmentIds);
}

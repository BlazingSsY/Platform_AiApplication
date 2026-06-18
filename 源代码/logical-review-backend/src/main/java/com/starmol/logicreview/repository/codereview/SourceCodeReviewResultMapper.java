package com.starmol.logicreview.repository.codereview;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.starmol.logicreview.bean.dto.SourceCodeReviewStatisticsExportDataDTO;
import com.starmol.logicreview.bean.vo.SourceCodeFileDetailVO;
import com.starmol.logicreview.bean.vo.SourceCodeReviewHomeStatisticsDataItemVO;
import com.starmol.logicreview.model.codereview.SourceCodeReviewResult;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 电路审查结果Mapper接口
 *
 * @author system
 * @date 2025-01-07
 */
public interface SourceCodeReviewResultMapper extends BaseMapper<SourceCodeReviewResult> {

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
            "    f.file_name, ",
            "    f.department_id, ",
            "    f.is_recycle, ",
            "    d.name as department_name, ",
            "    f.owner_id, ",
            "    u.name as owner_name, ",
            "    r.id as result_id, ",
            "    r.check_points, ",
            "    r.pass_check_points, ",
            "    r.pass_rate, ",
            "    r.questions, ",
            "    r.is_closed_loop, ",
            "    r.review_time, ",
            "    r.status",
            "FROM ( ",
            "    SELECT DISTINCT ON (file_id) ",
            "        id, ",
            "        file_id, ",
            "        check_points, ",
            "        pass_check_points, ",
            "        pass_rate, ",
            "        questions, ",
            "        is_closed_loop, ",
            "        review_time, ",
            "        status ",
            "    FROM ljsc_review_result where is_delete=0 ",
            "    ORDER BY file_id, review_time DESC ",
            ") r ",
            "INNER JOIN ljsc_file f ON r.file_id = f.id ",
            "LEFT JOIN urm_department d ON f.department_id = d.id ",
            "LEFT JOIN urm_user u ON f.owner_id = u.id ",
            "WHERE r.status = 2 AND f.is_delete = 0 AND f.file_name NOT LIKE 'test-%'",
            "<if test='departmentIds != null and departmentIds.size() > 0'> ",
            "  AND f.department_id IN ",
            "  <foreach collection='departmentIds' item='deptId' open='(' separator=',' close=')'> ",
            "    #{deptId} ",
            "  </foreach> ",
            "</if> ",
            "<if test='userId != null'> ",
            "  AND f.owner_id = #{userId} ",
            "</if> ",
            "ORDER BY r.review_time DESC ",
            "LIMIT #{limit} ",
            "</script>"
    })
    List<SourceCodeReviewHomeStatisticsDataItemVO> getRecentlyReviewedFiles(
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
            "    f.file_name, ",
            "    f.department_id, ",
            "    f.is_recycle, ",
            "    d.name as department_name, ",
            "    f.owner_id, ",
            "    u.name as owner_name, ",
            "    r.id as result_id, ",
            "    r.check_points, ",
            "    r.pass_check_points, ",
            "    r.pass_rate, ",
            "    r.questions, ",
            "    r.review_time, ",
            "    r.status",
            "FROM ( ",
            "    SELECT DISTINCT ON (file_id) ",
            "        id, ",
            "        file_id, ",
            "        check_points, ",
            "        pass_check_points, ",
            "        pass_rate, ",
            "        questions, ",
            "        review_time, ",
            "        status ",
            "    FROM ljsc_review_result  where is_delete=0",
            "    ORDER BY file_id, review_time DESC ",
            ") r ",
            "INNER JOIN ljsc_file f ON r.file_id = f.id ",
            "LEFT JOIN urm_department d ON f.department_id = d.id ",
            "LEFT JOIN urm_user u ON f.owner_id = u.id ",
            "WHERE r.status = 2 AND f.is_delete = 0 AND f.file_name NOT LIKE 'test-%'",
            "  AND r.pass_rate IS NOT NULL ",
            "<if test='departmentIds != null and departmentIds.size() > 0'> ",
            "  AND f.department_id IN ",
            "  <foreach collection='departmentIds' item='deptId' open='(' separator=',' close=')'> ",
            "    #{deptId} ",
            "  </foreach> ",
            "</if> ",
            "<if test='userId != null'> ",
            "  AND f.owner_id = #{userId} ",
            "</if> ",
            "ORDER BY r.pass_rate DESC, r.review_time DESC ",
            "LIMIT #{limit} ",
            "</script>"
    })
    List<SourceCodeReviewHomeStatisticsDataItemVO> getHighestPassRateFiles(
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
            "FROM ljsc_file f ",
            "LEFT JOIN urm_department d ON f.department_id = d.id ",
            "WHERE f.is_delete = 0 ",
            "<if test='departmentIds != null and departmentIds.size() > 0'> ",
            "  AND f.department_id IN ",
            "  <foreach collection='departmentIds' item='deptId' open='(' separator=',' close=')'> ",
            "    #{deptId} ",
            "  </foreach> ",
            "</if> ",
            "<if test='userId != null'> ",
            "  AND f.owner_id = #{userId} ",
            "</if> ",
            "GROUP BY d.id, d.name ",
            "ORDER BY file_count DESC ",
            "</script>"
    })
    List<Map<String, Object>> getTotalFilesCountByDepartment(
            @Param("departmentIds") List<Long> departmentIds,
            @Param("userId") Long userId
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
            "FROM ljsc_review_result r ",
            "INNER JOIN ljsc_file f ON r.file_id = f.id ",
            "LEFT JOIN urm_department d ON f.department_id = d.id ",
            "WHERE r.status = 2 AND r.is_delete=0 AND f.is_delete = 0 ",
            "<if test='departmentIds != null and departmentIds.size() > 0'> ",
            "  AND f.department_id IN ",
            "  <foreach collection='departmentIds' item='deptId' open='(' separator=',' close=')'> ",
            "    #{deptId} ",
            "  </foreach> ",
            "</if> ",
            "<if test='userId != null'> ",
            "  AND f.owner_id = #{userId} ",
            "</if> ",
            "GROUP BY d.id, d.name ",
            "ORDER BY file_count DESC ",
            "</script>"
    })
    List<Map<String, Object>> getReviewedFilesCountByDepartment(
            @Param("departmentIds") List<Long> departmentIds,
            @Param("userId") Long userId
    );

    /**
     * 按部门统计用户数量
     *
     * @param departmentIds 部门ID列表
     * @param userId        用户ID（可选，用于普通用户过滤）
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
            "<if test='userId != null'> ",
            "  AND u.id = #{userId} ",
            "</if> ",
            "GROUP BY d.id, d.name ",
            "ORDER BY user_count DESC ",
            "</script>"
    })
    List<Map<String, Object>> getUserCountByDepartment(
            @Param("departmentIds") List<Long> departmentIds,
            @Param("userId") Long userId
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
            "    SELECT DISTINCT ON (file_id) ",
            "        id, ",
            "        file_id, ",
            "        check_points, ",
            "        pass_check_points, ",
            "        pass_rate, ",
            "        review_time, ",
            "        status ",
            "    FROM ljsc_review_result ",
            "    WHERE status = 2 AND is_delete = 0 ",
            "    ORDER BY file_id, review_time DESC ",
            ") r ",
            "INNER JOIN ljsc_file f ON r.file_id = f.id ",
            "LEFT JOIN urm_department d ON f.department_id = d.id ",
            "LEFT JOIN urm_user u ON f.owner_id = u.id ",
            "WHERE r.status = 2 AND f.is_delete = 0 AND f.file_name NOT LIKE 'test-%'",
            "<if test='departmentIds != null and departmentIds.size() > 0'> ",
            "  AND f.department_id IN ",
            "  <foreach collection='departmentIds' item='deptId' open='(' separator=',' close=')'> ",
            "    #{deptId} ",
            "  </foreach> ",
            "</if> ",
            "<if test='userId != null'> ",
            "  AND f.owner_id = #{userId} ",
            "</if> ",
            "GROUP BY d.id, d.name ",
            "ORDER BY issue_count DESC ",
            "</script>"
    })
    List<Map<String, Object>> getReviewIssueCountByDepartment(
            @Param("departmentIds") List<Long> departmentIds,
            @Param("userId") Long userId
    );

    /**
     * 获取统计分析页面的文件详情列表
     *
     * @param departmentIds 部门ID列表
     * @param startDate     开始日期
     * @param endDate       结束日期
     * @return 文件详情列表
     */
    @Select({"<script>",
            "SELECT ",
            "    f.id, ",
            "    f.minio_id, ",
            "    f.file_name, ",
            "    f.compatible_models, ",
            "    f.product_model, ",
            "    f.product_name, ",
            "    f.config_name, ",
            "    f.code_file_version, ",
            "    f.department_id, ",
            "    d.name as department_name, ",
            "    f.owner_id, ",
            "    u.name as owner_name, ",
            "    f.is_recycle, ",
            "    f.comments, ",
            "    f.create_date, ",
            "    f.update_date, ",
            "    r.id as result_id, ",
            "    r.check_points, ",
            "    r.pass_check_points, ",
            "    r.pass_rate, ",
            "    r.questions, ",
            "    r.review_time, ",
            "    r.is_closed_loop, ",
            "    r.status ",
            "FROM ljsc_file f ",
            "LEFT JOIN ( ",
            "    SELECT DISTINCT ON (file_id) ",
            "        id, ",
            "        file_id, ",
            "        check_points, ",
            "        pass_check_points, ",
            "        pass_rate, ",
            "        questions, ",
            "        is_closed_loop, ",
            "        review_time, ",
            "        status ",
            "    FROM ljsc_review_result ",
            "    WHERE status = 2 AND is_delete = 0 ",
            "    ORDER BY file_id, review_time DESC ",
            ") r ON f.id = r.file_id ",
            "LEFT JOIN urm_department d ON f.department_id = d.id ",
            "LEFT JOIN urm_user u ON f.owner_id = u.id ",
            "WHERE r.status = 2 ",
            "  AND f.is_delete = 0 AND f.file_name NOT LIKE 'test-%' ",
            "<if test='departmentIds != null and departmentIds.size() > 0'> ",
            "  AND f.department_id IN ",
            "  <foreach collection='departmentIds' item='deptId' open='(' separator=',' close=')'> ",
            "    #{deptId} ",
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
    List<SourceCodeFileDetailVO> getStatisticsFileDetails(
            @Param("departmentIds") List<Long> departmentIds,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    /**
     * 按部门统计图表数据
     *
     * @param departmentIds 部门ID列表
     * @param startDate     开始日期
     * @param endDate       结束日期
     * @return 按部门统计的数据
     */
    @Select({"<script>",
            "SELECT ",
            "    d.name as department_name, ",
            "    SUM(r.check_points) as file_count, ",
            "    SUM(r.pass_check_points) as pass_file_count,  ",
            "    (SUM(r.check_points) - SUM(r.pass_check_points)) as fail_file_count, ",
            "    SUM(detail.rule_count) as rule_count,  ",
            "    SUM(CASE WHEN r.is_closed_loop = 1 THEN 1 ELSE 0 END) as closed_loop_count, ",
            "    SUM(CASE WHEN r.is_closed_loop = 0 THEN 1 ELSE 0 END) as non_closed_loop_count, ",
            "    AVG(r.pass_rate) as average_pass_rate ",
            "FROM ( ",
            "    SELECT DISTINCT ON (file_id) ",
            "        id, ",
            "        file_id, ",
            "        check_points, ",
            "        pass_check_points, ",
            "        pass_rate, ",
            "        is_closed_loop, ",
            "        review_time, ",
            "        status ",
            "    FROM ljsc_review_result ",
            "    WHERE status = 2 AND is_delete = 0 ",
            "    ORDER BY file_id, review_time DESC ",
            ") r ",
            "INNER JOIN ljsc_file f ON r.file_id = f.id ",
            "LEFT JOIN (select result_id, count(DISTINCT rule_id) rule_count FROM ljsc_review_result_detail  where is_delete = 0 group by result_id) detail ON r.id = detail.result_id ",
            "LEFT JOIN urm_department d ON f.department_id = d.id ",
            "WHERE r.status = 2 AND f.is_delete = 0 AND f.file_name NOT LIKE 'test-%' ",
            "<if test='departmentIds != null and departmentIds.size() > 0'> ",
            "  AND f.department_id IN ",
            "  <foreach collection='departmentIds' item='deptId' open='(' separator=',' close=')'> ",
            "    #{deptId} ",
            "  </foreach> ",
            "</if> ",
            "<if test='startDate != null'> ",
            "  AND r.review_time &gt;= #{startDate} ",
            "</if> ",
            "<if test='endDate != null'> ",
            "  AND r.review_time &lt; #{endDate} ",
            "</if> ",
            "GROUP BY d.id, d.name ",
            "ORDER BY file_count DESC ",
            "</script>"
    })
    List<Map<String, Object>> getStatisticsByDepartment(
            @Param("departmentIds") List<Long> departmentIds,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    /**
     * 按规则类型统计图表数据
     *
     * @param departmentIds 部门ID列表
     * @param startDate     开始日期
     * @param endDate       结束日期
     * @return 按规则类型统计的数据
     */
    @Select({"<script>",
            "SELECT ",
            "  rule.type as rule_type, ",
            "  COUNT(*) as file_count, ",
            "  SUM(CASE WHEN detail.is_passed = 1 THEN 1 ELSE 0 END) as pass_file_count, ",
            "  SUM(CASE WHEN detail.is_passed = 0 THEN 1 ELSE 0 END) as fail_file_count, ",
            "  COUNT(DISTINCT detail.result_rule) as rule_count,",
            "  SUM(CASE WHEN r.is_closed_loop = 1 THEN 1 ELSE 0 END) as closed_loop_count, ",
            "  SUM(CASE WHEN r.is_closed_loop = 0 THEN 1 ELSE 0 END) as non_closed_loop_count, ",
            "  AVG(r.pass_rate) as average_pass_rate ",
            "FROM ( ",
            "    SELECT DISTINCT ON (file_id) ",
            "        id, ",
            "        file_id, ",
            "        check_points, ",
            "        pass_check_points, ",
            "        pass_rate, ",
            "        is_closed_loop, ",
            "        review_time, ",
            "        status ",
            "    FROM ljsc_review_result ",
            "    WHERE status = 2 AND is_delete = 0 ",
            "    ORDER BY file_id, review_time DESC ",
            ") r ",
            "INNER JOIN ljsc_file f ON r.file_id = f.id ",
            "INNER JOIN (",
            " SELECT DISTINCT ON (result_id, rule_id, source_file_name)",
            " id,",
            " result_id,",
            " rule_id,",
            " CONCAT(result_id::text, rule_id::text) as result_rule,",
            " source_file_name,",
            " is_passed",
            " FROM ljsc_review_result_detail where is_delete=0 ORDER BY result_id, rule_id, source_file_name, is_passed ",
            ") detail ON r.id = detail.result_id ",
            "INNER JOIN ljsc_rule rule ON detail.rule_id = rule.id ",
            "WHERE r.status = 2 AND f.is_delete = 0 AND f.file_name NOT LIKE 'test-%' ",
            "<if test='departmentIds != null and departmentIds.size() > 0'> ",
            "  AND f.department_id IN ",
            "  <foreach collection='departmentIds' item='deptId' open='(' separator=',' close=')'> ",
            "    #{deptId} ",
            "  </foreach> ",
            "</if> ",
            "<if test='startDate != null'> ",
            "  AND r.review_time &gt;= #{startDate} ",
            "</if> ",
            "<if test='endDate != null'> ",
            "  AND r.review_time &lt; #{endDate} ",
            "</if> ",
            "GROUP BY rule.type ",
            "ORDER BY rule_type ",
            "</script>"
    })
    List<Map<String, Object>> getStatisticsByRuleType(
            @Param("departmentIds") List<Long> departmentIds,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    /**
     * 一次性获取所有时间维度（日、周、月、季、年）的统计数据
     * @param departmentIds 部门ID列表
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
            "    SUM(r.check_points) as file_count, ",
            "    SUM(r.pass_check_points) as pass_file_count,  ",
            "    (SUM(r.check_points) - SUM(r.pass_check_points)) as fail_file_count, ",
            "    SUM(detail.rule_count) as rule_count,  ",
            "    SUM(CASE WHEN r.is_closed_loop = 1 THEN 1 ELSE 0 END) as closed_loop_count, ",
            "    SUM(CASE WHEN r.is_closed_loop = 0 THEN 1 ELSE 0 END) as non_closed_loop_count, ",
            "    AVG(r.pass_rate) as average_pass_rate ",
            "FROM ( ",
            "    SELECT DISTINCT ON (file_id) ",
            "        id, ",
            "        file_id, ",
            "        check_points, ",
            "        pass_check_points, ",
            "        pass_rate, ",
            "        is_closed_loop, ",
            "        review_time, ",
            "        status ",
            "    FROM ljsc_review_result ",
            "    WHERE status = 2 AND is_delete = 0 ",
            "    ORDER BY file_id, review_time DESC ",
            ") r ",
            "INNER JOIN ljsc_file f ON r.file_id = f.id ",
            "LEFT JOIN (select result_id, count(DISTINCT rule_id) rule_count FROM ljsc_review_result_detail  where is_delete = 0 group by result_id) detail ON r.id = detail.result_id ",
            "WHERE r.status = 2 AND f.is_delete = 0 AND f.file_name NOT LIKE 'test-%' ",
            "<if test='departmentIds != null and departmentIds.size() > 0'> ",
            "  AND f.department_id IN ",
            "  <foreach collection='departmentIds' item='deptId' open='(' separator=',' close=')'> ",
            "    #{deptId} ",
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
    List<Map<String, Object>> getStatisticsByTimeDimensions(
            @Param("departmentIds") List<Long> departmentIds,
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

    /**
     * 按部门统计图表数据导出到Excel
     *
     * @param startDate     开始日期
     * @param endDate       结束日期
     * @return 按部门统计的数据
     */
    @Select({"<script>",
            "SELECT ",
            "    d.id as department_id, ",
            "    d.name as department_name, ",
            "    SUM(r.check_points) as file_count, ",
            "    SUM(r.pass_check_points) as pass_file_count,  ",
            "    (SUM(r.check_points) - SUM(r.pass_check_points)) as fail_file_count, ",
            "    COUNT(f.*) as source_file_count,  ",
            "    SUM(CASE WHEN r.is_closed_loop = 1 THEN 1 ELSE 0 END) as closed_loop_count, ",
            "    SUM(CASE WHEN r.is_closed_loop = 0 THEN 1 ELSE 0 END) as non_closed_loop_count, ",
            "    AVG(r.pass_rate) as average_pass_rate ",
            "FROM ( ",
            "    SELECT DISTINCT ON (file_id) ",
            "        id, ",
            "        file_id, ",
            "        check_points, ",
            "        pass_check_points, ",
            "        pass_rate, ",
            "        is_closed_loop, ",
            "        review_time, ",
            "        status ",
            "    FROM ljsc_review_result ",
            "    WHERE status = 2 AND is_delete = 0 ",
            "    ORDER BY file_id, review_time DESC ",
            ") r ",
            "INNER JOIN ljsc_file f ON r.file_id = f.id ",
            "LEFT JOIN urm_department d ON f.department_id = d.id ",
            "WHERE r.status = 2 AND f.is_delete = 0 AND f.file_name NOT LIKE 'test-%' ",
            "<if test='startDate != null'> ",
            "  AND r.review_time &gt;= #{startDate} ",
            "</if> ",
            "<if test='endDate != null'> ",
            "  AND r.review_time &lt; #{endDate} ",
            "</if> ",
            "GROUP BY d.id, d.name ",
            "ORDER BY file_count DESC ",
            "</script>"
    })
    List<SourceCodeReviewStatisticsExportDataDTO> getStatisticsByDepartmentForExportExcel(
            @Param("startDate") String startDate,
            @Param("endDate") String endDate
    );

}
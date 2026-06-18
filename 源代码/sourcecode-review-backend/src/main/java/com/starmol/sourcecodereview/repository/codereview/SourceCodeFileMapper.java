package com.starmol.sourcecodereview.repository.codereview;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starmol.sourcecodereview.bean.vo.SourceCodeFileDetailVO;
import com.starmol.sourcecodereview.model.codereview.SourceCodeFile;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 电路图文件Mapper接口
 *
 * @author system
 * @date 2025-01-07
 */
public interface SourceCodeFileMapper extends BaseMapper<SourceCodeFile> {

    /**
     * 分页查询文件详情，包括最新的审查结果
     *
     * @param page     分页参数
     * @param userId   用户ID
     * @param fileName 文件名（可选，用于模糊查询）
     * @return 文件详情分页结果
     */
    @Select({"<script>",
            "SELECT ",
            "    f.id, ",
            "    v.id as file_version_id, ",
            "    f.secret_level as secret_level, ",
            "    v.minio_id as minio_id, ",
            "    f.file_name as file_name, ",
            "    f.compatible_models, ",
            "    f.product_model, ",
            "    f.product_name, ",
            "    f.config_name, ",
            "    f.code_file_version, ",
            "    v.file_name as file_version_name, ",
            "    f.department_id as department_id, ",
            "    d.name as department_name, ",
            "    f.owner_id as owner_id, ",
            "    u.name as owner_name, ",
            "    f.is_recycle as is_recycle, ",
            "    f.comments, ",
            "    r.id as result_id, ",
            "    r.check_points as check_points, ",
            "    r.pass_check_points as pass_check_points, ",
            "    r.pass_rate as pass_rate, ",
            "    r.is_closed_loop as is_closed_loop, ",
            "    r.status as status, ",
            "    r.error_message as error_message, ",
            "    r.review_time as review_time, ",
            "    r.files_size as files_size, ",
            "    r.files_line as files_line, ",
            "    r.use_rule_size as use_rule_size, ",
            "    r.questions as questions ",
            "FROM dmsc_file f ",
            "LEFT JOIN (SELECT DISTINCT ON (file_id) id, file_id, minio_id, file_version, file_name ",
            "FROM dmsc_file_version",
            "WHERE is_delete = 0 ",
            "ORDER BY file_id, file_version DESC) v on f.id = v.file_id ",
            "LEFT JOIN ( ",
            "    SELECT DISTINCT ON (file_version_id) ",
            "        id, ",
            "        file_id, ",
            "        file_version_id, ",
            "        check_points, ",
            "        pass_check_points, ",
            "        pass_rate, ",
            "        is_closed_loop, ",
            "        status, ",
            "        error_message, ",
            "        review_time, ",
            "        files_size, ",
            "        files_line, ",
            "        use_rule_size, ",
            "        questions ",
            "    FROM dmsc_review_result ",
            "    WHERE status IN (1, 2, 3, 4) and is_delete = 0 ",
            "    ORDER BY file_version_id, review_time DESC ",
            ") r ON v.id = r.file_version_id ",
            "LEFT JOIN urm_department d ON f.department_id = d.id ",
            "LEFT JOIN urm_user u ON f.owner_id = u.id ",
            "WHERE f.is_delete = 0",
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
            "ORDER BY f.update_date DESC ",
            "</script>"
    })
    IPage<SourceCodeFileDetailVO> selectFileDetailPage(Page<SourceCodeFileDetailVO> page,
                                                    @Param("departmentIds") List<Long> departmentIds,
                                                    @Param("userId") Long userId,
                                                    @Param("fileName") String fileName);
} 
package com.starmol.portal.backend.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starmol.portal.backend.bean.vo.DepartmentPageVO;
import com.starmol.portal.backend.bean.vo.DepartmentTreeVO;
import com.starmol.portal.backend.bean.vo.LoginNameDepartmentVO;
import com.starmol.portal.backend.model.Department;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface DepartmentMapper extends BaseMapper<Department> {
    /**
     * @param page 翻页对象，可以作为 xml 参数直接使用，传递参数 Page 即自动分页
     */
    @Select({"<script>",
            "SELECT d.*,u.name AS createUserName FROM urm_department d LEFT JOIN urm_user u ON d.create_user = u.id AND d.is_delete = 0 AND u.is_delete = 0 where d.is_delete=0 ",
            "<if test='type!=null and type!=\"\"'>",
            " and d.type = #{type}",
            "</if>",
            "</script>"
    })
    IPage<DepartmentPageVO> getDepartmentVO(IPage<DepartmentPageVO> page, @Param("type") String type);

    /**
     * 根据用户登录名列表获取, 返回包含部门信息的用户列表
     */
    @Select({"<script>",
            "SELECT d.*,u.id AS userId,u.login_name AS loginName FROM urm_department d INNER JOIN urm_user u ON d.id=u.department_id  AND u.is_delete=0 WHERE d.is_delete= 0 ",
            "<if test='loginNameList!=null and loginNameList.size()>0'>",
            " and u.login_name in <foreach collection='loginNameList' item='loginName' open='(' separator=',' close=')'>#{loginName}</foreach>",
            "</if>",
            "</script>"
    })
    List<LoginNameDepartmentVO> getLoginNameDepartmentVO(@Param("loginNameList") List<String> loginNameList);

    /**
     * 获取部门列表并关联创建用户名（用于树形结构展示）
     * @param isOffice 是否只查询科室
     * @return 部门列表（包含createUserName字段）
     */
    @Select({"<script>",
            "SELECT d.id, d.name, d.type, d.is_editable, d.sequence, d.f_id, d.comments, d.is_office, ",
            "d.create_user, d.update_user, d.create_date, d.update_date, d.version, ",
            "u.login_name AS createUserName ",
            "FROM urm_department d LEFT JOIN urm_user u ON d.create_user = u.id AND u.is_delete = 0 WHERE d.is_delete = 0",
            "<if test='isOffice != null'>",
            " AND d.is_office = #{isOffice}",
            "</if>",
            " ORDER BY d.name ASC",
            "</script>"
    })
    List<DepartmentTreeVO> getDepartmentsWithCreateUserName(@Param("isOffice") Boolean isOffice);

    /**
     * 获取所有部门列表并关联创建用户名（用于树形结构展示，不限制isOffice）
     * @return 部门列表（包含createUserName字段）
     */
    @Select({
            "SELECT d.id, d.name, d.type, d.is_editable, d.sequence, d.f_id, d.comments, d.is_office, ",
            "d.create_user, d.update_user, d.create_date, d.update_date, d.version, ",
            "u.login_name AS createUserName ",
            "FROM urm_department d LEFT JOIN urm_user u ON d.create_user = u.id AND u.is_delete = 0 WHERE d.is_delete = 0 ",
            "ORDER BY d.name ASC"
    })
    List<DepartmentTreeVO> getAllDepartmentsWithCreateUserName();

}
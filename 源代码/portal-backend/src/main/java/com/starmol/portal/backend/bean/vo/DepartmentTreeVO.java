

package com.starmol.portal.backend.bean.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.List;

import com.starmol.portal.backend.model.Department;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用于展示部门树结构信息
 *
 * @author Yuexiaopeng
 * @date 2019/9/26
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "部门树结构VO")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DepartmentTreeVO extends Department implements Serializable {
    /**
     * 子节点部门列表
     */
    @Schema(description = "子节点列表")
    private List<DepartmentTreeVO> childList;

    @Schema(description = "创建用户名")
    private String createUserName;

    /**
     * 脱钩方法
     */
    public Department toDepartment() {
        Department department = new Department();
        try {
            BeanUtils.copyProperties(this, department);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return department;
    }

    /**
     * 对象构造方法
     */
    public static DepartmentTreeVO fromDepartment(Department department) {
        DepartmentTreeVO departmentTreeVO = new DepartmentTreeVO();
        try {
            BeanUtils.copyProperties(department, departmentTreeVO);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return departmentTreeVO;
    }
}
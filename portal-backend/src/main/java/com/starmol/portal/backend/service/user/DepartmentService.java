package com.starmol.portal.backend.service.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starmol.portal.backend.bean.vo.DepartmentPageVO;
import com.starmol.portal.backend.bean.vo.DepartmentTreeVO;
import com.starmol.portal.backend.bean.vo.LoginNameDepartmentVO;
import com.starmol.portal.backend.bean.vo.SimpleItemVO;
import com.starmol.portal.backend.model.Department;
import com.starmol.portal.backend.model.base.DeleteDTO;
import com.starmol.portal.backend.service.base.BaseCascadeService;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

/**
 * 部门逻辑层
 *
 * @author :Yuexiaopeng
 * @date :2019/12/10
 */
public interface DepartmentService extends BaseCascadeService<Department> {

    /**
     * 下载模板
     */

    void downLoadTemplate(HttpServletResponse response) throws Exception;

    Integer importDepartmentInfo(MultipartFile file) throws Exception;

    /**
     * 用事务的方式新增部门，并检父ID
     *
     * @param department 要创建的部门对象
     * @return 返回的部门对象
     */
    public Department saveAndReturnObjectWithFidCheck(Department department);


    /**
     * 根据列表removeDepartments 删除部门,如果是安全删除，如果部门下有用户，抛异常， 如果非安全删除，删除部门时同时删除子用户
     *
     * @param removeDepartments 要删除的部门列表
     * @return 是否删除成功
     */
    void removeDepartmentsWithTransaction(List<DeleteDTO> removeDepartments);


    /**
     * 用事务的方式修改部门，并检查关联关系
     *
     * @param department 要修改的部门对象
     * @return 返回的部门对象
     */
    public Department updateByIdAndReturnObjectWithFidCheck(Department department);


    /**
     * 返回树结构部门列表(只包含符合查询条件(模糊查询的部门名)节点， 如果，根节点不符合，其下的叶子节点符合，那么根节点也会返回);
     *
     * @param departmentName 用于模糊查询的部门名
     * @return 树结构部门列表
     */
    List<DepartmentTreeVO> createDepartmentTreeByDepartmentNameWithOrder(String departmentName);

    List<DepartmentTreeVO> createOnlyCurrentDepartmentAndOfficeTreeByDepartmentNameWithOrder(String departmentName);

    /**
     * 获取带有创建用户名称的部门列表
     *
     * @param page 分页参数
     * @param type 类型
     * @return 部门列表
     */
    IPage<DepartmentPageVO> getDepartmentPageWithType(IPage<DepartmentPageVO> page, String type);


    List<SimpleItemVO> getSimpleItem(String name);

    List<SimpleItemVO> getDepartmentSimpleItemAndChild(Long departmentId);
    /**
     * 获取指定部门下的所有叶子部门(包含子孙节点)
     *
     * @return 部门列表
     */
    void getLeafDepartment(Long departmentId, List<Department> results);

    List<Department> getNonEmptyDepartments(Long departmentId);

    List<LoginNameDepartmentVO> getLoginNameDepartmentVO(List<String> loginNameList);

    /**
     * 获取带有创建用户名的部门树（仅包含当前用户所在部门及科室）
     * @param departmentName 部门名称（用于模糊查询）
     * @return 部门树列表
     */
    List<DepartmentTreeVO> createOnlyCurrentDepartmentAndOfficeTreeWithCreateUserName(String departmentName);

    /**
     * 获取带有创建用户名的部门树（包含所有部门）
     * @param departmentName 部门名称（用于模糊查询）
     * @return 部门树列表
     */
    List<DepartmentTreeVO> createDepartmentTreeByDepartmentNameWithOrderAndCreateUserName(String departmentName);
}

package com.starmol.portal.backend.service.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starmol.portal.backend.bean.vo.RoleVO;
import com.starmol.portal.backend.constant.SysRoleTypeEnum;
import com.starmol.portal.backend.model.Role;
import com.starmol.portal.backend.model.base.DeleteDTO;
import com.starmol.portal.backend.service.base.BaseCascadeService;

import java.util.List;

public interface RoleService extends BaseCascadeService<Role> {

    /**
     * 根据角色id删除角色并将与和这个角色有关关联删除掉
     *
     * @param removeRoles : 要删除的角色列表
     */
    void removeRolesWithTransaction(List<DeleteDTO> removeRoles);


    /**
     * 根据权限ID获取角色列表
     *
     * @param name    角色名称
     * @param powerId 权限ID
     * @return 角色列表
     */
    List<Role> getRoleListByPowerId(String name, Long powerId);

    /**
     * 根据权限ID获取角色列表,分页显示
     *
     * @param page    分页参数
     * @param name    名称
     * @param powerId 权限ID
     * @return 角色列表
     */
    IPage<Role> getRoleListPageByPowerId(IPage<Role> page, String name, Long powerId);


    /**
     * 获取未使用指定权限的角色列表,分页显示
     *
     * @param page    分页参数
     * @param name    名称
     * @param powerId 权限ID
     * @return 角色列表
     */
    IPage<Role> getExcludedRoleListPageByPowerId(IPage<Role> page, String name, Long powerId);

    /**
     * 根据用户ID获取角色列表
     *
     * @param name   角色名称
     * @param userId 用户ID
     * @return 角色列表
     */
    List<Role> getRoleListByUserId(String name, Long userId);

    /**
     * 根据用户ID获取用户的系统角色类型
     *
     * @param userId 用户ID
     * @return 系统角色类型
     */
    SysRoleTypeEnum getSysRoleTypeByUserId(Long userId);

    /**
     * 根据用户ID获取角色列表,分页显示
     *
     * @param page   分页参数
     * @param name   名称
     * @param userId 用户ID
     * @return 角色列表
     */
    IPage<Role> getRoleListPageByUserId(IPage<Role> page, String name, Long userId);

    /**
     * 根据用户ID获取角色VO列表,分页显示
     *
     * @param page      分页参数
     * @param name      模糊查询的角色名称
     * @param userId    用户ID
     * @param sortField 排序字段
     * @return RoleVO列表
     */
    IPage<RoleVO> getRoleVoListPageByUserId(IPage<RoleVO> page, String name, Long userId, String sortField);
}

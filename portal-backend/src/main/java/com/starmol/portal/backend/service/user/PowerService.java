package com.starmol.portal.backend.service.user;

import com.starmol.portal.backend.bean.vo.PowerAliasTreeVO;
import com.starmol.portal.backend.bean.vo.PowerSelectionTreeVO;
import com.starmol.portal.backend.model.Power;
import com.starmol.portal.backend.model.base.DeleteDTO;
import com.starmol.portal.backend.service.base.BaseCascadeService;

import java.util.List;

public interface PowerService extends BaseCascadeService<Power> {

    /**
     * 用事务的方式新增权限，并检父ID
     *
     * @param power 要创建的权限对象
     * @return 返回的权限对象
     */
    Power saveAndReturnObjectWithFidCheck(Power power);


    /**
     * 用事务的方式删除权限，并检子节点,如果有子节点，报异常退出删除
     *
     * @param removePowers 要删除的权限列表
     * @return 是否删除成功
     */
    void removePowersWithTransaction(List<DeleteDTO> removePowers);


    /**
     * 用事务的方式修改权限，并检查关联关系
     *
     * @param power 要修改的权限对象
     * @return 返回的权限对象
     */
    Power updateByIdAndReturnObjectWithFidCheck(Power power);


    /**
     * 根据ID创建可选树结构(按照"sequence"排序)
     *
     * @param roleID 角色ID
     * @return 可选树结构权限列表
     */

    /**
     * 返回树结构权限列表(包含全部节点)，并将指定角色拥有的权限的节点的selected属性设置为True;
     *
     * @param roleId 角色ID
     * @return 树结构权限列表
     */
    List<PowerSelectionTreeVO> createSelectionTreeByRoleIdWithOrder(Long roleId);

    /**
     * 根据关系列表，将权限列表转换为别名树结构
     *
     * @param powers      权限列表
     * @param selectedIds 选中id列表
     * @return 别名树结构权限列表
     */
    List<PowerAliasTreeVO> convertListToAliasTree(List<Power> powers, List<Long> selectedIds);

    /**
     * 根据ID创建别名树结构(按照"sequence"排序)
     *
     * @param roleId 角色ID
     * @return 别名树结构权限列表
     */
    List<PowerAliasTreeVO> createAliasTreeByRoleIdWithOrder(Long roleId);

    /**
     * 根据角色ID列表创建别名树结构(按照"sequence"排序)
     *
     * @param roleIdList 角色ID列表
     * @return 别名树结构权限列表
     */
    List<PowerAliasTreeVO> createAliasTreeByRoleIdListWithOrder(List<Long> roleIdList);


    /**
     * 根据用户ID建别名树结构(按照"sequence"排序)
     *
     * @param userId 用户和ID
     * @return 别名树结构权限列表
     */
    List<PowerAliasTreeVO> getPowersByUserId(Long userId);
}

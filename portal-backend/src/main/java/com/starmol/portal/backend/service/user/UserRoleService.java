package com.starmol.portal.backend.service.user;

import com.starmol.portal.backend.bean.dto.UserRoleChangeDTO;
import com.starmol.portal.backend.model.UserRole;
import com.starmol.portal.backend.service.base.BaseService;

import java.util.List;

/**
 * 用户角色逻辑层
 * @author :Yuexiaopeng
 * @date :2019/12/10
 */
public interface UserRoleService extends BaseService<UserRole> {

    /**
     * 用事务的方式增加或删除用户角色，并检查操作目标是否正确
     *
     * @param userRoleChanges 修改的用户角色列表
     * @return 是否成功
     */
    Boolean updateUserRoleByChangeList(List<UserRoleChangeDTO> userRoleChanges);

    /**
     * 根据修改内容获取日志信息
     *
     * @param userRoleChanges 修改的用户角色列表
     * @return 日志信息
     */
    String getLogFromChangeList(List<UserRoleChangeDTO> userRoleChanges);

    List<UserRole> getRoleByUserId(Long userId);
}

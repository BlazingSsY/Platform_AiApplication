package com.starmol.portal.backend.service.user;

import com.starmol.portal.backend.bean.dto.RolePowerChangeDTO;
import com.starmol.portal.backend.model.RolePower;
import com.starmol.portal.backend.service.base.BaseService;

import java.util.List;

/**
 * 角色权限逻辑层
 * @author :Yuexiaopeng
 * @date :2019/12/10
 */
public interface RolePowerService extends BaseService<RolePower> {

    /**
     * 用事务的方式增加或删除角色权限，并检查操作目标是否正确
     *
     * @param rolePowerChanges 修改的角色权限列表
     * @return 是否成功
     */
    Boolean updateRolePowerByChangeList(List<RolePowerChangeDTO> rolePowerChanges);

    /**
     * 根据修改内容获取日志信息
     *
     * @param rolePowerChanges 修改的角色权限列表
     * @return 日志信息
     */
    String getLogFromChangeList(List<RolePowerChangeDTO> rolePowerChanges);
}

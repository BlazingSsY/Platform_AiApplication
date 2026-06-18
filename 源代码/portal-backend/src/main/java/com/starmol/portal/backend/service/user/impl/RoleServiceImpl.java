package com.starmol.portal.backend.service.user.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starmol.portal.backend.bean.vo.RoleVO;
import com.starmol.portal.backend.constant.SysRoleTypeEnum;
import com.starmol.portal.backend.model.Role;
import com.starmol.portal.backend.model.base.DeleteDTO;
import com.starmol.portal.backend.model.base.ServiceItem;
import com.starmol.portal.backend.repository.RoleMapper;
import com.starmol.portal.backend.service.base.impl.BaseCascadeServiceImpl;
import com.starmol.portal.backend.service.user.RolePowerService;
import com.starmol.portal.backend.service.user.RoleService;
import com.starmol.portal.backend.service.user.UserRoleService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.jsonwebtoken.lang.Collections;
import lombok.extern.slf4j.Slf4j;


/**
 * RoleService接口的实现类
 *
 * @author : Yuexiaopeng
 * @date :2019/12/10
 **/
@Service
@Slf4j
public class RoleServiceImpl extends BaseCascadeServiceImpl<RoleMapper, Role> implements RoleService {

    private final RolePowerService rolePowerService;
    private final UserRoleService userRoleService;

    public RoleServiceImpl(RolePowerService rolePowerService, UserRoleService userRoleService) {
        this.rolePowerService = rolePowerService;
        this.userRoleService = userRoleService;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void removeRolesWithTransaction(List<DeleteDTO> removeRoles) {
        removeObjectsByObjectList(removeRoles);
    }

    @Override
    public List<Role> getRoleListByPowerId(String name, Long powerId) {
        return this.getBaseMapper().getRoleListByPowerId(name, powerId);
    }

    @Override
    public IPage<Role> getRoleListPageByPowerId(IPage<Role> page, String name, Long powerId) {
        return this.getBaseMapper().getRoleListPageByPowerId(page, name, powerId);
    }

    @Override
    public IPage<Role> getExcludedRoleListPageByPowerId(IPage<Role> page, String name, Long powerId) {
        return this.getBaseMapper().getExcludedRoleListPageByPowerId(page, name, powerId);
    }

    @Override
    public List<Role> getRoleListByUserId(String name, Long userId) {
        return this.getBaseMapper().getRoleListByUserId(name, userId);
    }

    @Override
    public SysRoleTypeEnum getSysRoleTypeByUserId(Long userId) {
        List<Role> roleList = this.getBaseMapper().getRoleListByUserId(null, userId);
        if (!Collections.isEmpty(roleList)) {
            return  SysRoleTypeEnum.valOf(roleList.get(0).getName());
        }
        else {
            return null;
        }
    }

    @Override
    public IPage<Role> getRoleListPageByUserId(IPage<Role> page, String name, Long userId) {
        return this.getBaseMapper().getRoleListPageByUserId(page, name, userId);
    }

    @Override
    public IPage<RoleVO> getRoleVoListPageByUserId(IPage<RoleVO> page, String name, Long userId, String sortField) {
        return this.getBaseMapper().getRoleVOListPageByUserId(page, name, userId, sortField);
    }

    /**
     * 有附加属性，此方法必须重载
     */
    @Override
    public List<ServiceItem> getAllAdditionalAttributeSevice() {
        if (Objects.isNull(additionalAttributeServiceList)) {
            additionalAttributeServiceList = new ArrayList<ServiceItem>();
            additionalAttributeServiceList.add(new ServiceItem("role_id", rolePowerService));
            additionalAttributeServiceList.add(new ServiceItem("role_id", userRoleService));
        }
        return additionalAttributeServiceList;
    }
}

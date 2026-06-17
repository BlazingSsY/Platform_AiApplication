package com.starmol.portal.backend.service.user.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starmol.portal.backend.bean.dto.UserRoleChangeDTO;
import com.starmol.portal.backend.exception.KnowException;
import com.starmol.portal.backend.model.UserRole;
import com.starmol.portal.backend.repository.UserRoleMapper;
import com.starmol.portal.backend.service.base.impl.BaseCascadeServiceImpl;
import com.starmol.portal.backend.service.user.UserRoleService;
import com.starmol.portal.backend.utils.StringUtil;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;


/**
 * UserRoleService接口的实现类
 *
 * @author : Yuexiaopeng
 * @date :2019/12/10
 **/
@Service
@Slf4j
public class UserRoleServiceImpl extends BaseCascadeServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public Boolean updateUserRoleByChangeList(List<UserRoleChangeDTO> userRoleChanges) {
        for (UserRoleChangeDTO userRoleChange : userRoleChanges) {
            if (userRoleChange.getSelected()) {
                Long roleId = userRoleChange.getRoleId();
                Long powerId = userRoleChange.getUserId();
                if (this.count(Wrappers.<UserRole>lambdaQuery().eq(UserRole::getRoleId, roleId).eq(UserRole::getUserId, powerId)) > 0) {
                    throw new KnowException("保存异常: 相同的记录已存在，请刷新后重新提交请求");
                }
                UserRole userRole = new UserRole();
                userRole.setRoleId(roleId);
                userRole.setUserId(powerId);
                saveAndReturnObject(userRole);
            } else {
                UserRole userRole = getOne(Wrappers.<UserRole>lambdaQuery().eq(UserRole::getRoleId, userRoleChange.getRoleId()).eq(UserRole::getUserId, userRoleChange.getUserId()));
                if (Objects.nonNull(userRole)) {
                    removeByIdWithFill(userRole);
                } else {
                    throw new KnowException("保存异常: 要删除的记录已不存在，请刷新后重新提交请求");
                }
            }
        }
        return true;
    }

    @Override
    public String getLogFromChangeList(List<UserRoleChangeDTO> userRoleChanges){
        if (userRoleChanges.stream().map(UserRoleChangeDTO::getUserId).distinct().count() == 1){
            return String.format("修改用户: %s 的角色", StringUtil.getLogDescription(userRoleChanges.get(0).getUserId(),userRoleChanges.get(0).getUserName()));
        }
        else if (userRoleChanges.stream().map(UserRoleChangeDTO::getRoleId).distinct().count() == 1){
            return String.format("修改角色: %s 的用户", StringUtil.getLogDescription(userRoleChanges.get(0).getRoleId(),userRoleChanges.get(0).getRoleName()));
        }
        else{
            return "修改用户角色关系";
        }
    }

    @Override
    public List<UserRole> getRoleByUserId(Long userId) {
        final LambdaQueryWrapper<UserRole> wrapper = Wrappers.<UserRole>lambdaQuery().eq(UserRole::getUserId, userId);
        return this.list(wrapper);
    }
}

package com.starmol.portal.backend.service.user.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starmol.portal.backend.bean.dto.RolePowerChangeDTO;
import com.starmol.portal.backend.exception.KnowException;
import com.starmol.portal.backend.model.RolePower;
import com.starmol.portal.backend.repository.RolePowerMapper;
import com.starmol.portal.backend.service.base.impl.BaseCascadeServiceImpl;
import com.starmol.portal.backend.service.user.RolePowerService;
import com.starmol.portal.backend.utils.StringUtil;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RolePowerServiceImpl extends BaseCascadeServiceImpl<RolePowerMapper, RolePower> implements RolePowerService {

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public Boolean updateRolePowerByChangeList(List<RolePowerChangeDTO> rolePowerChanges) {
        for (RolePowerChangeDTO rolePowerChange : rolePowerChanges) {
            if (rolePowerChange.getSelected()) {
                Long roleId = rolePowerChange.getRoleId();
                Long powerId = rolePowerChange.getPowerId();
                if (this.count(Wrappers.<RolePower>lambdaQuery().eq(RolePower::getRoleId, roleId).eq(RolePower::getPowerId, powerId)) > 0) {
                    throw new KnowException("保存异常: 相同的记录已存在，请刷新后重新提交请求");
                }
                RolePower rolePower = new RolePower();
                rolePower.setRoleId(roleId);
                rolePower.setPowerId(powerId);
                saveAndReturnObject(rolePower);
            } else {
                RolePower rolePower = getOne(Wrappers.<RolePower>lambdaQuery().eq(RolePower::getRoleId, rolePowerChange.getRoleId()).eq(RolePower::getPowerId, rolePowerChange.getPowerId()));
                if (Objects.nonNull(rolePower)) {
                    removeByIdWithFill(rolePower);
                } else {
                    throw new KnowException("保存异常: 要删除的记录已不存在，请刷新后重新提交请求");
                }
            }
        }
        return true;
    }

    @Override
    public String getLogFromChangeList(List<RolePowerChangeDTO> rolePowerChanges) {
        if (rolePowerChanges.stream().map(RolePowerChangeDTO::getRoleId).distinct().count() == 1) {
            return String.format("修改角色: %s 的权限", StringUtil.getLogDescription(rolePowerChanges.get(0).getRoleId(), rolePowerChanges.get(0).getRoleName()));
        } else if (rolePowerChanges.stream().map(RolePowerChangeDTO::getPowerId).distinct().count() == 1) {
            return String.format("修改权限: %s 的角色", StringUtil.getLogDescription(rolePowerChanges.get(0).getPowerId(), rolePowerChanges.get(0).getPowerName()));
        } else {
            return "修改角色权限关系";
        }
    }

}

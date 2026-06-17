package com.starmol.portal.backend.service.user.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starmol.portal.backend.bean.vo.PowerAliasTreeVO;
import com.starmol.portal.backend.bean.vo.PowerSelectionTreeVO;
import com.starmol.portal.backend.exception.KnowException;
import com.starmol.portal.backend.model.Power;
import com.starmol.portal.backend.model.RolePower;
import com.starmol.portal.backend.model.UserRole;
import com.starmol.portal.backend.model.base.DeleteDTO;
import com.starmol.portal.backend.model.base.ServiceItem;
import com.starmol.portal.backend.repository.PowerMapper;
import com.starmol.portal.backend.service.base.impl.BaseCascadeServiceImpl;
import com.starmol.portal.backend.service.user.PowerService;
import com.starmol.portal.backend.service.user.RolePowerService;
import com.starmol.portal.backend.service.user.UserRoleService;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;


/**
 * PowerService接口的实现类
 *
 * @author Yuexiaopeng
 * @date 2019/11/21
 **/
@Service
@Slf4j
public class PowerServiceImpl extends BaseCascadeServiceImpl<PowerMapper, Power> implements PowerService {

    private final RolePowerService rolePowerService;
    private final UserRoleService userRoleService;

    public PowerServiceImpl(RolePowerService rolePowerService,UserRoleService userRoleService) {
        this.rolePowerService = rolePowerService;
        this.userRoleService = userRoleService;
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public Power saveAndReturnObjectWithFidCheck(Power power) {
        Long fidFieldValue = power.getFId();
        String nameFieldValue = power.getName();
        if (Objects.nonNull(fidFieldValue) && !fidFieldValue.equals(0L) && (getById(fidFieldValue) == null)) {
            throw new KnowException("保存异常: 当前节点关联的父节点已不存在，请刷新后重新提交请求");
        }
        /**如果无同级目录下不能重名的限制，请删除此判断代码 */
        if (this.count(Wrappers.<Power>lambdaQuery().eq(Power::getName, nameFieldValue).eq(Power::getFId, fidFieldValue)) > 0) {
            throw new KnowException("保存异常: 当前目录下已存在同名的节点，请刷新后重新提交请求");
        }
        return saveAndReturnObject(power);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public void removePowersWithTransaction(List<DeleteDTO> removePowers) {
        removeObjectsByObjectList(removePowers);
    }

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public Power updateByIdAndReturnObjectWithFidCheck(Power power) {
        Long idFieldValue = power.getId();
        Long fidFieldValue = power.getFId();
        String nameFieldValue = power.getName();
        if (Objects.nonNull(fidFieldValue) && !fidFieldValue.equals(0L) && (getById(fidFieldValue) == null)) {
            throw new KnowException("保存异常: 当前节点关联的父节点已不存在，请刷新后重新提交请求");
        }
        /**如果无同级目录下不能重名的限制，请删除此判断代码 */
        if (this.count(Wrappers.<Power>lambdaQuery().eq(Power::getName, nameFieldValue).eq(Power::getFId, fidFieldValue).ne(Power::getId, idFieldValue)) > 0) {
            throw new KnowException("保存异常: 当前目录下已存在同名的节点，请刷新后重新提交请求");
        }
        return this.updateByIDAndReturnObject(power);
    }

    /**
     * 有附加属性，此方法必须重载
     */
    @Override
    public List<ServiceItem> getAllAdditionalAttributeSevice() {
        if (Objects.isNull(additionalAttributeServiceList)) {
            additionalAttributeServiceList = new ArrayList<ServiceItem>();
            additionalAttributeServiceList.add(new ServiceItem("role_id", rolePowerService));
        }
        return additionalAttributeServiceList;
    }

    /******** 创建SelectionTree, 所有节点都展示，selectedIDs中包含的节点, 展示为选中状态 **************/
    private PowerSelectionTreeVO createSelectionTree(Power fPower, List<Power> powers, List<Long> selectedIds) {
        PowerSelectionTreeVO powerSelectionTreeVO = new PowerSelectionTreeVO();
        BeanUtils.copyProperties(fPower, powerSelectionTreeVO);
        if (Objects.nonNull(selectedIds) && selectedIds.contains(powerSelectionTreeVO.getId())) {
            powerSelectionTreeVO.setSelected(true);
        } else {
            powerSelectionTreeVO.setSelected(false);
        }
        List<Power> childPowerList = powers.stream().filter(power -> fPower.getId().equals(power.getFId())).collect(Collectors.toList());
        if (childPowerList.size() > 0) {
            powerSelectionTreeVO.setChildList(new ArrayList<>());
            for (Power childPower : childPowerList) {
                powerSelectionTreeVO.getChildList().add(createSelectionTree(childPower, powers, selectedIds));
            }
        } else {
            powerSelectionTreeVO.setChildList(null);
        }
        return powerSelectionTreeVO;
    }


    private List<PowerSelectionTreeVO> convertListToSelectionTree(List<Power> powers, List<Long> selectedIds) {
        List<PowerSelectionTreeVO> powerSelectionTreeVOList = new ArrayList<>();
        List<Power> topPowerList = powers.stream().filter(power -> Objects.isNull(power.getFId()) || power.getFId().equals(0L)).collect(Collectors.toList());

        for (Power topPower : topPowerList) {
            powerSelectionTreeVOList.add(createSelectionTree(topPower, powers, selectedIds));
        }
        return powerSelectionTreeVOList;
    }

    @Override
    public List<PowerSelectionTreeVO> createSelectionTreeByRoleIdWithOrder(Long roleId) {
        List<Power> powerList = list(new QueryWrapper<Power>().orderBy(true,true, "sequence"));
        List<Long> selectedIds = Objects.isNull(roleId) ? null : rolePowerService.list(Wrappers.<RolePower>lambdaQuery().eq(RolePower::getRoleId, roleId)).stream().map(m->m.getPowerId()).collect(Collectors.toList());
        return convertListToSelectionTree(powerList, selectedIds);
    }


    /******** 创建AliasTree, 别名节点都展示，selectedIDs中包含的节点，及其父，祖父节点全部展示**************/
    private PowerAliasTreeVO createAliasTree(Power fPower, List<Power> powers, List<Long> selectedIds) {
        PowerAliasTreeVO powerAliasTreeVO = null;
        if ((Objects.nonNull(selectedIds))) {
            List<Power> childPowerList = powers.stream().filter(power -> fPower.getId().equals(power.getFId())).collect(Collectors.toList());
            if (childPowerList.size() > 0) {
                for (Power childPower : childPowerList) {
                    PowerAliasTreeVO childPowerAliasTreeVO = createAliasTree(childPower, powers, selectedIds);
                    if (Objects.nonNull(childPowerAliasTreeVO)) {
                        if(Objects.isNull(powerAliasTreeVO)) {
                            powerAliasTreeVO = PowerAliasTreeVO.fromPower(fPower);
                            powerAliasTreeVO.setChildList(new ArrayList<>());
                        }
                        powerAliasTreeVO.getChildList().add(childPowerAliasTreeVO);
                    }
                }
            }
            else {
                if(selectedIds.contains(fPower.getId())) {
                    powerAliasTreeVO = PowerAliasTreeVO.fromPower(fPower);
                    powerAliasTreeVO.setSequence(fPower.getSequence());
                }
            }
        }
        return powerAliasTreeVO;
    }

    @Override
    public List<PowerAliasTreeVO> convertListToAliasTree(List<Power> powers, List<Long> selectedIds) {
        List<PowerAliasTreeVO> powerAliasTreeVos = new ArrayList<>();
        List<Power> topPowerList = powers.stream().filter(power -> Objects.isNull(power.getFId()) || power.getFId().equals(0L)).collect(Collectors.toList());
        for (Power topPower : topPowerList) {
            PowerAliasTreeVO childPowerAliasTreeVO = createAliasTree(topPower, powers, selectedIds);
            if (Objects.nonNull(childPowerAliasTreeVO)) {
                powerAliasTreeVos.add(childPowerAliasTreeVO);
            }
        }
        return powerAliasTreeVos;
    }

    @Override
    public List<PowerAliasTreeVO> createAliasTreeByRoleIdWithOrder(Long roleId) {
        List<Power> powerList = list(new QueryWrapper<Power>().orderBy(true, true, "sequence"));
        List<Long> selectedIds = Objects.isNull(roleId) ? null : rolePowerService.list(Wrappers.<RolePower>lambdaQuery().eq(RolePower::getRoleId, roleId)).stream().map(RolePower::getPowerId).collect(Collectors.toList());
        return convertListToAliasTree(powerList, selectedIds);
    }

    @Override
    public List<PowerAliasTreeVO> createAliasTreeByRoleIdListWithOrder(List<Long> roleIdList) {
        List<Power> powerList = list(new QueryWrapper<Power>().orderBy(true, true, "sequence"));
        Set<Long> selectedIdSet = new HashSet<>();
        roleIdList.forEach(roleId -> {
            List<Long> selectedIdList = Objects.isNull(roleId) ? null : rolePowerService.list(Wrappers.<RolePower>lambdaQuery().eq(RolePower::getRoleId, roleId)).stream().map(RolePower::getPowerId).collect(Collectors.toList());
            if (selectedIdList != null) {
                selectedIdSet.addAll(selectedIdList);
            }
        });
        return convertListToAliasTree(powerList, new ArrayList<>(selectedIdSet));
    }

    @Override
    public List<PowerAliasTreeVO> getPowersByUserId(Long userId) {
        List<PowerAliasTreeVO> aliasTreeByRoleIdWithOrder = Collections.emptyList();
        List<UserRole> userRoleList = userRoleService.list(Wrappers.<UserRole>lambdaQuery().eq(UserRole::getUserId, userId));
        if (Objects.nonNull(userRoleList)) {
            List<Long> roleIdList = userRoleList.stream().map(UserRole::getRoleId).collect(Collectors.toList());
            aliasTreeByRoleIdWithOrder = this.createAliasTreeByRoleIdListWithOrder(roleIdList);
        }
        return aliasTreeByRoleIdWithOrder;
    }

}

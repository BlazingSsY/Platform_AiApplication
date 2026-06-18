package com.starmol.circuitreview.backend.service.base.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.starmol.circuitreview.backend.constant.ChildOperationSettingEnum;
import com.starmol.circuitreview.backend.constant.SelfTreeOperationSettingEnum;
import com.starmol.circuitreview.backend.exception.KnowException;
import com.starmol.circuitreview.backend.model.base.CascadeServiceItem;
import com.starmol.circuitreview.backend.model.base.DeleteDTO;
import com.starmol.circuitreview.backend.model.base.ServiceItem;
import com.starmol.circuitreview.backend.service.base.BaseCascadeService;
import com.starmol.circuitreview.backend.utils.StringUtil;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import lombok.SneakyThrows;

public class BaseCascadeServiceImpl<M extends BaseMapper<T>, T> extends BaseServiceImpl<M, T>
        implements BaseCascadeService<T> {
    protected List<CascadeServiceItem> childServiceList;

    protected List<ServiceItem> additionalAttributeServiceList;

    /***自Tree表级联删除设置***/
    private SelfTreeOperationSettingEnum selfTreeOperationSetting = SelfTreeOperationSettingEnum.CHECK;

    public SelfTreeOperationSettingEnum getSelfTreeOperationSetting() {
        return selfTreeOperationSetting;
    }

    @Override
    public void setSelfTreeOperationSetting(SelfTreeOperationSettingEnum selfTreeOperationSetting) {
        this.selfTreeOperationSetting = selfTreeOperationSetting;
    }

    /**
     * 级联删除，调用入口函数: 按照注册关系和注册设置，在删除主表数据的同时，对子孙表的数据进行删除或检查，同时删除属性表的相关数据。
     *
     * @param removeObjects       要删除的对象列表
     * @param handleCascadeRemove : 是否进行级联删除操作，如果此值为false,
     *                            则不进行子对象级联删除(当级联删除的灵活度不够时,用户可调用removeByObjectsWithFill方法，然后自行完成级联删除)
     * @return 删除的主数据id列表
     */
    @SneakyThrows
    @Override
    public List<String> removeObjectsByObjectList(List<T> removeObjects, boolean handleCascadeRemove) {

        boolean isCurrentModelSelfTree = checkIfCurrentModelIsSelfTreeModel(removeObjects);
        if (isCurrentModelSelfTree) {
            /***调用自Tree表处理函数: "检查是否有子对象存在"  或者 "获取要删除子对象id列表" ***/
            if (getSelfTreeOperationSetting().equals(ChildOperationSettingEnum.CHECK)) {
                checkSelfTreeChildEmpty(removeObjects);
            } else {
                removeObjects = getSelfTreeChilds(removeObjects);
                removeObjects = removeDuplicatedChilds(removeObjects);
            }
        }

        if (handleCascadeRemove) {
            /***子表的处理***/
            for (CascadeServiceItem cascadeServiceItem : getAllChildSevice()) {
                cascadeRemoveChild(cascadeServiceItem.getService(), cascadeServiceItem.getRelationFieldName(),
                        cascadeServiceItem.getOperationSetting(), removeObjects);
            }
        }

        /***删除属性数据***/
        clearAdditionalAttribute(removeObjects);

        /***按照新的列表删除当前表的记录***/
        removeByObjectsWithFill(removeObjects);

        /***获取ids列表，这里不能用stream 因为要抛出异常***/
        List<String> refvalueList = new ArrayList<String>();
        for (T removeObject : removeObjects) {
            refvalueList.add(BeanUtils.getSimpleProperty(removeObject, "id"));
        }
        return refvalueList;
    }

    /**
     * 判断当前Service管理的表是不是自Tree结构
     *
     * @param objectsList : 主数据列表
     * @return boolean: True:是自Tree表, false:不是自Tree表
     */
    private boolean checkIfCurrentModelIsSelfTreeModel(List<T> objectsList) {
        if (objectsList.size() > 0) {
            /**判断当前Service管理的表是不是自Tree结构*/
            Field fIdField = ReflectionUtils.findField(objectsList.get(0).getClass(), "parentId");
            if (Objects.nonNull(fIdField)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 判断当前Service管理的自Tree表是否有子节点在选定节点下,如果有抛异常; 这里只做一层检查不递归(没有子节点，就没有孙子节点)
     *
     * @param selfTreeObjects : 选的节点的对象列表
     */
    protected void checkSelfTreeChildEmpty(List<T> selfTreeObjects)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        for (T selfTreeObject : selfTreeObjects) {
            Long idValue = Long.valueOf(BeanUtils.getSimpleProperty(selfTreeObject, "id"));
            long childNodeCount = count(new QueryWrapper<T>().eq("parent_id", idValue));
            if (childNodeCount > 0) {
                //String parentName = getById(idValue).getName(); /** 如果要给用户友好提示需要重载此方法，获取Name提示给用户*/
                throw new KnowException(String.format("删除失败:要删除的记录下有关联的子数据存在，请先删除关联记录！"));
            }
        }
    }

    /**
     * 获取当前Service管理的Tree表选定节点下的子孙节点id列表
     *
     * @param selfTreeObjects : 选的节点的对象列表
     * @return 包含selfTreeObjects和其下子孙节点的新列表
     */
    private List<T> getSelfTreeChilds(List<T> selfTreeObjects)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        List<T> newSelfTreeObjects = new ArrayList<T>();
        newSelfTreeObjects.addAll(selfTreeObjects);
        for (T selfTreeObject : selfTreeObjects) {
            Long idValue = Long.valueOf(BeanUtils.getSimpleProperty(selfTreeObject, "id"));
            List<T> childList = list(new QueryWrapper<T>().eq("parent_id", idValue));
            if (childList.size() > 0) {
                newSelfTreeObjects.addAll(getSelfTreeChilds(childList));
            }
        }
        return newSelfTreeObjects;
    }

    /**
     * 删除节点列表中的重复的节点
     *
     * @param selfTreeObjects : 要去重的对象列表
     * @return 去重后的新列表
     */
    private List<T> removeDuplicatedChilds(List<T> selfTreeObjects)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        List<T> newSelfTreeObjects = new ArrayList<T>();
        Set<Long> idSets = new HashSet<Long>();
        for (T selfTreeObject : selfTreeObjects) {
            Long idValue = Long.valueOf(BeanUtils.getSimpleProperty(selfTreeObject, "id"));
            if (!idSets.contains(idValue)) {
                idSets.add(idValue);
                newSelfTreeObjects.add(selfTreeObject);
            }
        }
        return newSelfTreeObjects;
    }

    /**
     * 检查removeObjectList对应数据有无关联的子数据  或 删除removeObjectList对应数据下关联的子数据，或将子数据的关系字段置空
     *
     * @param childService     : 子服务实例
     * @param refId            : 关联字段
     * @param operationSetting : 处理方式: 检查 / 置空 / 删除
     * @param removeObjectList :  主数据列表
     */
    private void cascadeRemoveChild(BaseCascadeService childService, String refId,
                                    ChildOperationSettingEnum operationSetting, List<T> removeObjectList)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        if (operationSetting.equals(ChildOperationSettingEnum.CHECK)) {
            checkChildEmpty(childService, refId, removeObjectList);
        } else if (operationSetting.equals(ChildOperationSettingEnum.SET_EMPTY)) {
            setChildRelationEmpty(childService, refId, removeObjectList);
        } else {
            removeChild(childService, refId, removeObjectList);
        }
    }

    /**
     * 检查此列表removeObjectList下关联Service有无关联的记录，如果有抛异常
     *
     * @param childService     : 子服务实例
     * @param refId            : 关联字段
     * @param removeObjectList : 主数据列表
     */
    protected void checkChildEmpty(BaseCascadeService childService, String refId, List<T> removeObjectList)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        for (T removeObject : removeObjectList) {
            Long idValue = Long.valueOf(BeanUtils.getSimpleProperty(removeObject, "id"));
            long childCount = childService.count(new QueryWrapper<>().eq(refId, idValue));
            if (childCount > 0) {
                //String parentName = getById(idValue).getName(); /** 如果要给用户友好提示需要重载此方法，获取Name提示给用户*/
                throw new KnowException(String.format("删除失败:要删除的记录下有关联的数据存在，请先删除关联记录！"));
            }
        }
    }

    /**
     * 将列表removeObjectList下关联Service的关联记录的关联段置空(即: 让关联子记录和要删除的记录失去关联)
     *
     * @param childService     : 子服务实例
     * @param refId            : 关联字段
     * @param removeObjectList : 主数据列表
     */
    private void setChildRelationEmpty(BaseCascadeService childService, String refId, List<T> removeObjectList)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        for (T removeObject : removeObjectList) {
            Long idValue = Long.valueOf(BeanUtils.getSimpleProperty(removeObject, "id"));
            List<Object> childList = childService.list(new QueryWrapper<>().eq(refId, idValue));
            for (Object child : childList) {
                BeanUtils.setProperty(child, StringUtil.underToCamel(refId), StringUtil.EMPTY_STRING);
                childService.updateByIDAndReturnObject(child);
            }
        }
    }

    /**
     * 删除列表removeObjectList下关联Service的关联记录
     *
     * @param childService     : 子服务实例
     * @param refId            : 关联字段
     * @param removeObjectList : 主数据列表
     */
    private void removeChild(BaseCascadeService childService, String refId, List<T> removeObjectList)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException {
        for (T removeObject : removeObjectList) {
            Long idValue = Long.valueOf(BeanUtils.getSimpleProperty(removeObject, "id"));
            List<Object> childList = childService.list(new QueryWrapper<>().eq(refId, idValue));
            childService.removeObjectsByObjectList(childList, true);
        }
    }

    /**
     * 删除列表removeObjectList下属性Service的关联记录
     *
     * @param removeObjectList : 主数据列表
     */
    private void clearAdditionalAttribute(List<T> removeObjectList)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        /**删除相关属性数据*/
        for (ServiceItem serviceItem : getAllAdditionalAttributeSevice()) {
            for (T removeObject : removeObjectList) {
                Long idValue = Long.valueOf(BeanUtils.getSimpleProperty(removeObject, "id"));
                List additionalAttributes =
                        serviceItem.getService().list(new QueryWrapper<>().eq(serviceItem.getRelationFieldName(), idValue));
                serviceItem.getService().removeByObjectsWithFill(additionalAttributes);
            }
        }
    }

    /**
     * 函数 removeObjectsByObjectList(List<T> removeObjects, boolean handleCascadeRemove)的封装，使其可以接收 List<DeleteDTO>参数，
     * handleCascadeRemove默认为True
     *
     * @param removeObjects : 主数据列表
     */
    @SneakyThrows
    @Override
    public List<String> removeObjectsByObjectList(List<DeleteDTO> removeObjects) {
        List<T> removeObjectList = new ArrayList<>();
        for (DeleteDTO deleteObject : removeObjects) {
            ParameterizedType ptype = (ParameterizedType) this.getClass().getGenericSuperclass();
            Class clazz = (Class<T>) ptype.getActualTypeArguments()[1];
            T object = (T) clazz.newInstance();
            org.springframework.beans.BeanUtils.copyProperties(deleteObject, object);
            removeObjectList.add(object);
        }
        return removeObjectsByObjectList(removeObjectList, true);
    }

    /** 此方法需要重载以添加向下级联记录的Service*/
    /**
     * 获取所有关联的子数据的Service列表
     */
    public List<CascadeServiceItem> getAllChildSevice() {
        if (Objects.isNull(childServiceList)) {
            childServiceList = new ArrayList<CascadeServiceItem>();
            //childServiceList.add(new CascadeServiceItem("gradeId", studentService));
        }
        return childServiceList;
    }

    /** 此方法需要重载以添加附加属性Service*/
    /**
     * 获取所有附加属性Service列表
     */
    public List<ServiceItem> getAllAdditionalAttributeSevice() {
        if (Objects.isNull(additionalAttributeServiceList)) {
            additionalAttributeServiceList = new ArrayList<ServiceItem>();
            //additionalAttributeServiceList.add(new ServiceItem("peopleId", peopleRoleService));
        }
        return additionalAttributeServiceList;
    }
}

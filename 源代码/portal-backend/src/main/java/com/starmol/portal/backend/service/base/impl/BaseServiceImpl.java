package com.starmol.portal.backend.service.base.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.starmol.portal.backend.common.UserMetaData;
import com.starmol.portal.backend.constant.SysRoleTypeEnum;
import com.starmol.portal.backend.exception.KnowException;
import com.starmol.portal.backend.model.base.DeleteDTO;
import com.starmol.portal.backend.service.base.BaseService;
import com.starmol.portal.backend.utils.HttpRequestUtil;
import com.starmol.portal.backend.utils.IdWorker;
import com.starmol.portal.backend.utils.StringUtil;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseServiceImpl<M extends BaseMapper<T>, T> extends ServiceImpl<M, T> implements BaseService<T> {
    /*******************1. 以下增加记录的方法*****************/

    protected T saveAndReturnObjectWithOperatorId(T baseObject) throws IllegalAccessException, InvocationTargetException {
        try {
            String id = BeanUtils.getProperty(baseObject, "id");
            if (StringUtils.isEmpty(id)) {
                long idValue = IdWorker.getId();
                BeanUtils.setProperty(baseObject, "id", idValue);
            }
        } catch (NoSuchMethodException e) {
            log.error("实体类没有id属性", e);
        }

        BeanUtils.setProperty(baseObject, "tenantId", getTenantId());
        BeanUtils.setProperty(baseObject, "createUser", getOperatorId());
        BeanUtils.setProperty(baseObject, "updateUser", getOperatorId());
        BeanUtils.setProperty(baseObject, "createDate", LocalDateTime.now());
        BeanUtils.setProperty(baseObject, "updateDate", LocalDateTime.now());
        if (save(baseObject)) {
            BeanUtils.setProperty(baseObject, "version", 0);
            return baseObject;
        } else {
            throw new KnowException("保存记录失败，请刷新后重新提交请求");
        }
    }

    @SneakyThrows
    @Override
    public T saveAndReturnObject(T baseObject) {
        return saveAndReturnObjectWithOperatorId(baseObject);
    }

    private void checkFieldUniqueForSave(T baseObject, String uniqueFieldName, String uniqueFieldLabel) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        String value = BeanUtils.getSimpleProperty(baseObject, StringUtil.underToCamel(uniqueFieldName));
        QueryWrapper<T> queryWrapper = new QueryWrapper<T>().eq(uniqueFieldName, value);
        if (list(queryWrapper).size() > 0) {
            if (!StringUtils.isEmpty(uniqueFieldLabel)) {
                if (uniqueFieldLabel.startsWith("&&")) {
                    throw new KnowException(String.format("主键重复: %s 的记录已经存在", uniqueFieldLabel.substring(2)));
                } else {
                    throw new KnowException(String.format("主键重复: %s 为 %s 的记录已经存在", uniqueFieldLabel, value));
                }
            } else {
                throw new KnowException(String.format("主键重复: %s 为 %s 的记录已经存在", uniqueFieldName, value));
            }
        }
    }

    private void checkCombinedFieldUniqueForSave(T baseObject, List<String> uniqueCombinedFieldNames, List<String> uniqueCombinedFieldLabels) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        List<String> exceptionInfo = new ArrayList<>();
        QueryWrapper<T> queryWrapper = new QueryWrapper<T>();
        if ((Objects.nonNull(uniqueCombinedFieldNames)) && (uniqueCombinedFieldNames.size() > 0)) {
            for (int i = 0; i < uniqueCombinedFieldNames.size(); i++) {
                String value = BeanUtils.getSimpleProperty(baseObject, StringUtil.underToCamel(uniqueCombinedFieldNames.get(i)));
                queryWrapper.eq(uniqueCombinedFieldNames.get(i), value);
                if (Objects.nonNull(uniqueCombinedFieldLabels) && (i < uniqueCombinedFieldLabels.size())) {
                    if (uniqueCombinedFieldLabels.get(i).startsWith("&&")) {
                        exceptionInfo.add(uniqueCombinedFieldLabels.get(i).substring(2));
                    } else {
                        exceptionInfo.add(String.format(" %s 为 %s ", uniqueCombinedFieldLabels.get(i), value));
                    }
                } else {
                    exceptionInfo.add(String.format(" %s 为 %s ", uniqueCombinedFieldNames.get(i), value));
                }
            }
        }

        if (list(queryWrapper).size() > 0) {
            throw new KnowException(String.format("主键重复: %s 的记录已经存在", String.join(";", exceptionInfo)));
        }
    }

    @SneakyThrows
    @Override
    public T saveAndReturnObject(T baseObject, String uniqueFieldName) {
        return saveAndReturnObject(baseObject, uniqueFieldName, null);
    }

    @SneakyThrows
    @Override
    public T saveAndReturnObject(T baseObject, String uniqueFieldName, String uniqueFieldLabel) {
        checkFieldUniqueForSave(baseObject, uniqueFieldName, uniqueFieldLabel);
        return saveAndReturnObject(baseObject);
    }

    @SneakyThrows
    @Override
    public T saveAndReturnObject(T baseObject, List<String> uniqueFieldNames) {
        return saveAndReturnObject(baseObject, uniqueFieldNames, null);
    }

    @SneakyThrows
    @Override
    public T saveAndReturnObject(T baseObject, List<String> uniqueFieldNames, List<String> uniqueFieldLabels) {
        if ((Objects.nonNull(uniqueFieldNames)) && (uniqueFieldNames.size() > 0)) {
            for (int i = 0; i < uniqueFieldNames.size(); i++) {
                if (Objects.nonNull(uniqueFieldLabels) && i < uniqueFieldLabels.size()) {
                    checkFieldUniqueForSave(baseObject, uniqueFieldNames.get(i), uniqueFieldLabels.get(i));
                } else {
                    checkFieldUniqueForSave(baseObject, uniqueFieldNames.get(i), null);
                }
            }
        }
        return saveAndReturnObject(baseObject);
    }

    @SneakyThrows
    @Override
    public T saveAndReturnObject(T baseObject, List<String> uniqueFieldNames, List<String> uniqueFieldLabels, List<List<String>> uniqueCombinedFieldNamesList, List<List<String>> uniqueCombinedFieldLabelsList) {
        if ((Objects.nonNull(uniqueFieldNames)) && (uniqueFieldNames.size() > 0)) {
            for (int i = 0; i < uniqueFieldNames.size(); i++) {
                if (Objects.nonNull(uniqueFieldLabels) && (i < uniqueFieldLabels.size())) {
                    checkFieldUniqueForSave(baseObject, uniqueFieldNames.get(i), uniqueFieldLabels.get(i));
                } else {
                    checkFieldUniqueForSave(baseObject, uniqueFieldNames.get(i), null);
                }
            }
        }
        if ((Objects.nonNull(uniqueCombinedFieldNamesList)) && (uniqueCombinedFieldNamesList.size() > 0)) {
            for (int i = 0; i < uniqueCombinedFieldNamesList.size(); i++) {
                if (Objects.nonNull(uniqueCombinedFieldLabelsList) && (i < uniqueCombinedFieldLabelsList.size())) {
                    checkCombinedFieldUniqueForSave(baseObject, uniqueCombinedFieldNamesList.get(i), uniqueCombinedFieldLabelsList.get(i));
                } else {
                    checkCombinedFieldUniqueForSave(baseObject, uniqueCombinedFieldNamesList.get(i), null);
                }
            }
        }
        return saveAndReturnObject(baseObject);
    }


    /*******************2. 以下删除记录的方法*****************/

    @SneakyThrows
    public boolean removeByIdWithFill(T entity) {
        BeanUtils.setProperty(entity, "updateUser", getOperatorId());
        if (SqlHelper.retBool(((BaseMapper) getBaseMapper()).deleteById(entity))) {
            return true;
        } else {
            throw new KnowException("删除记录失败，请刷新后重新提交请求");
        }
    }
    @SneakyThrows
    @Override
    public int removeByIdsWithFill(List<DeleteDTO> removeObject) {
        for (DeleteDTO deleteObject : removeObject) {
            ParameterizedType ptype = (ParameterizedType) this.getClass().getGenericSuperclass();
            Class clazz = (Class<T>) ptype.getActualTypeArguments()[1];
            T object = (T) clazz.newInstance();
            org.springframework.beans.BeanUtils.copyProperties(deleteObject, object);
            removeByIdWithFill(object);
        }
        return removeObject.size();
    }


    @SneakyThrows
    @Override
    public int removeByObjectsWithFill(List<T> removeObjects) {
        for (T removeObject : removeObjects) {
            removeByIdWithFill(removeObject);
        }
        return removeObjects.size();
    }

    /*******************3. 以下修改记录的方法*****************/

    T updateByIDAndReturnObjectWithOperatorId(T baseObject) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Long idValue = Long.valueOf(BeanUtils.getSimpleProperty(baseObject, "id"));
        BeanUtils.setProperty(baseObject, "updateUser", getOperatorId());
        BeanUtils.setProperty(baseObject, "updateDate", LocalDateTime.now());
        if (updateById(baseObject)) {
            return this.getById(idValue);
        } else {
            throw new KnowException("更新记录失败，请刷新后重新提交请求");
        }
    }

    @SneakyThrows
    @Override
    public T updateByIDAndReturnObject(T baseObject) {
        return updateByIDAndReturnObjectWithOperatorId(baseObject);
    }

    private void checkFieldUniqueForUpdate(T baseObject, String uniqueFieldName, String uniqueFieldLabel) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Long idValue = Long.valueOf(BeanUtils.getSimpleProperty(baseObject, "id"));
        String fieldValue = BeanUtils.getSimpleProperty(baseObject, StringUtil.underToCamel(uniqueFieldName));
        QueryWrapper<T> queryWrapper = new QueryWrapper<T>().eq(uniqueFieldName, fieldValue).ne("id", idValue);

        if (list(queryWrapper).size() > 0) {
            if (!StringUtils.isEmpty(uniqueFieldLabel)) {
                if (uniqueFieldLabel.startsWith("&&")) {
                    throw new KnowException(String.format("主键重复: %s 的记录已经存在", uniqueFieldLabel.substring(2)));
                } else {
                    throw new KnowException(String.format("主键重复: %s 为 %s 的记录已经存在", uniqueFieldLabel, fieldValue));
                }
            } else {
                throw new KnowException(String.format("主键重复: %s 为 %s 的记录已经存在", uniqueFieldName, fieldValue));
            }
        }
    }

    private void checkCombinedFieldUniqueForUpdate(T baseObject, List<String> uniqueCombinedFieldNames, List<String> uniqueCombinedFieldLabels) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        List<String> exceptionInfo = new ArrayList<>();
        QueryWrapper<T> queryWrapper = new QueryWrapper<T>();
        Long idValue = Long.valueOf(BeanUtils.getSimpleProperty(baseObject, "id"));

        if ((Objects.nonNull(uniqueCombinedFieldNames)) && (uniqueCombinedFieldNames.size() > 0)) {
            for (int i = 0; i < uniqueCombinedFieldNames.size(); i++) {
                String value = BeanUtils.getSimpleProperty(baseObject, StringUtil.underToCamel(uniqueCombinedFieldNames.get(i)));
                queryWrapper.eq(uniqueCombinedFieldNames.get(i), value);
                if (Objects.nonNull(uniqueCombinedFieldLabels) && (i < uniqueCombinedFieldLabels.size())) {
                    if (uniqueCombinedFieldLabels.get(i).startsWith("&&")) {
                        exceptionInfo.add(uniqueCombinedFieldLabels.get(i).substring(2));
                    } else {
                        exceptionInfo.add(String.format(" %s 为 %s ", uniqueCombinedFieldLabels.get(i), value));
                    }
                } else {
                    exceptionInfo.add(String.format(" %s 为 %s ", uniqueCombinedFieldNames.get(i), value));
                }
            }
        }
        queryWrapper.ne("id", idValue);
        if (list(queryWrapper).size() > 0) {
            throw new KnowException(String.format("主键重复:%s的记录已经存在", String.join(";", exceptionInfo)));
        }
    }

    @SneakyThrows
    @Override
    public T updateByIDAndReturnObject(T baseObject, String uniqueFieldName) {
        return updateByIDAndReturnObject(baseObject, uniqueFieldName, null);
    }

    @SneakyThrows
    @Override
    public T updateByIDAndReturnObject(T baseObject, String uniqueFieldName, String uniqueFieldLabel) {
        checkFieldUniqueForUpdate(baseObject, uniqueFieldName, uniqueFieldLabel);
        return updateByIDAndReturnObject(baseObject);
    }

    @SneakyThrows
    @Override
    public T updateByIDAndReturnObject(T baseObject, List<String> uniqueFieldNames) {
        return updateByIDAndReturnObject(baseObject, uniqueFieldNames, null);
    }

    @SneakyThrows
    @Override
    public T updateByIDAndReturnObject(T baseObject, List<String> uniqueFieldNames, List<String> uniqueFieldLabels) {
        if ((Objects.nonNull(uniqueFieldNames)) && (uniqueFieldNames.size() > 0)) {
            for (int i = 0; i < uniqueFieldNames.size(); i++) {
                if (Objects.nonNull(uniqueFieldLabels) && i < uniqueFieldLabels.size()) {
                    checkFieldUniqueForUpdate(baseObject, uniqueFieldNames.get(i), uniqueFieldLabels.get(i));
                } else {
                    checkFieldUniqueForUpdate(baseObject, uniqueFieldNames.get(i), null);
                }
            }
        }
        return updateByIDAndReturnObject(baseObject);
    }

    @SneakyThrows
    @Override
    public T updateByIDAndReturnObject(T baseObject, List<String> uniqueFieldNames, List<String> uniqueFieldLabels, List<List<String>> uniqueCombinedFieldNamesList, List<List<String>> uniqueCombinedFieldLabelsList) {
        if ((Objects.nonNull(uniqueFieldNames)) && (uniqueFieldNames.size() > 0)) {
            for (int i = 0; i < uniqueFieldNames.size(); i++) {
                if (Objects.nonNull(uniqueFieldLabels) && (i < uniqueFieldLabels.size())) {
                    checkFieldUniqueForUpdate(baseObject, uniqueFieldNames.get(i), uniqueFieldLabels.get(i));
                } else {
                    checkFieldUniqueForUpdate(baseObject, uniqueFieldNames.get(i), null);
                }
            }
        }

        if ((Objects.nonNull(uniqueCombinedFieldNamesList)) && (uniqueCombinedFieldNamesList.size() > 0)) {
            for (int i = 0; i < uniqueCombinedFieldNamesList.size(); i++) {
                if (Objects.nonNull(uniqueCombinedFieldLabelsList) && (i < uniqueCombinedFieldLabelsList.size())) {
                    checkCombinedFieldUniqueForUpdate(baseObject, uniqueCombinedFieldNamesList.get(i), uniqueCombinedFieldLabelsList.get(i));
                } else {
                    checkCombinedFieldUniqueForUpdate(baseObject, uniqueCombinedFieldNamesList.get(i), null);
                }
            }
        }

        return updateByIDAndReturnObject(baseObject);
    }

    @Override
    public Long getOperatorId() {
        UserMetaData user = HttpRequestUtil.getUser();
        if (user != null) {
            return user.getId();
        } else {
            return 0L;
        }
    }

    @Override
    public SysRoleTypeEnum getSysRoleType() {
        UserMetaData user = HttpRequestUtil.getUser();
        if (user != null) {
            return user.getSysRoleType();
        } else {
            return SysRoleTypeEnum.NORMAL_USER;
        }
    }

    @Override
    public String getOperatorName() {
        UserMetaData user = HttpRequestUtil.getUser();
        if (user != null) {
            return user.getLoginName();
        } else {
            return StringUtil.EMPTY_STRING;
        }
    }

    @Override
    public Long getTenantId() {
        UserMetaData user = HttpRequestUtil.getUser();
        if (user != null) {
            return user.getTenantId();
        } else {
            return 0L;
        }
    }
}

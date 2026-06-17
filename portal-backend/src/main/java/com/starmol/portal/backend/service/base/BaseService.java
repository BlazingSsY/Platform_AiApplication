package com.starmol.portal.backend.service.base;

import com.baomidou.mybatisplus.extension.service.IService;
import com.starmol.portal.backend.constant.SysRoleTypeEnum;
import com.starmol.portal.backend.model.base.DeleteDTO;

import java.util.List;

public interface BaseService<T> extends IService<T> {
    /**
     * 根据传入的对象插入记录，如果对象属性为null,插入时对应字段为默认值
     *
     * @param baseObject 要插入的对象
     * @return 插入成功后返回的对象(包含id)
     */
    T saveAndReturnObject(T baseObject);


    /**
     * 根据传入的对象插入记录,插入时对uniqueFieldName做唯一检查，如果已经存在同值记录抛出异常，如果对象属性为null,插入时对应字段为默认值
     *
     * @param baseObject      要插入的对象
     * @param uniqueFieldName 字段值唯一的字段名称,
     * @return 插入成功后返回的对象(包含id)
     */
    T saveAndReturnObject(T baseObject, String uniqueFieldName);


    /**
     * 根据传入的对象插入记录,插入时对uniqueFieldName做唯一检查，如果已经存在同值记录抛出异常，如果对象属性为null,插入时对应字段为默认值
     *
     * @param baseObject       要插入的对象
     * @param uniqueFieldName  字段值唯一的字段名称
     * @param uniqueFieldLabel 字段值唯一的字段标签, 如果此参数以&&开头，表示此参数为用户自定义的提示信息
     * @return 插入成功后返回的对象(包含id)
     */
    T saveAndReturnObject(T baseObject, String uniqueFieldName, String uniqueFieldLabel);


    /**
     * 根据传入的对象插入记录,插入时对uniqueFieldNames中的所有字段做唯一检查，如果已经存在同值记录抛出异常，如果对象属性为null,插入时对应字段为默认值
     *
     * @param baseObject       要插入的对象
     * @param uniqueFieldNames 字段值唯一的字段名称列表
     * @return 插入成功后返回的对象(包含id)
     */
    T saveAndReturnObject(T baseObject, List<String> uniqueFieldNames);


    /**
     * 根据传入的对象插入记录,插入时对uniqueFieldNames中的所有字段做唯一检查，如果已经存在同值记录抛出异常，如果对象属性为null,插入时对应字段为默认值
     *
     * @param baseObject        要插入的对象
     * @param uniqueFieldNames  字段值唯一的字段名称列表
     * @param uniqueFieldLabels 字段值唯一的字段标签列表, 如果此某一项以&&开头，表示此参数为用户自定义的提示信息
     * @return 插入成功后返回的对象(包含id)
     */
    T saveAndReturnObject(T baseObject, List<String> uniqueFieldNames, List<String> uniqueFieldLabels);

    /**
     * 根据传入的对象插入记录,插入时对uniqueFieldNames中的所有字段做唯一检查;对uniqueCombinedFieldNamesList中所有组合字段做唯一检查;如果已经存在同值记录抛出异常，如果对象属性为null,插入时对应字段为默认值
     *
     * @param baseObject                    要插入的对象
     * @param uniqueFieldNames              字段值唯一的字段名称列表
     * @param uniqueFieldLabels             字段值唯一的字段标签列表
     * @param uniqueCombinedFieldNamesList  组合字段值唯一的字段名称组合列表
     * @param uniqueCombinedFieldLabelsList 组合字段值唯一的字段标签组合列表
     * @return 插入成功后返回的对象(包含id)
     */
    T saveAndReturnObject(T baseObject, List<String> uniqueFieldNames, List<String> uniqueFieldLabels, List<List<String>> uniqueCombinedFieldNamesList, List<List<String>> uniqueCombinedFieldLabelsList);


    /**
     * 根据传入的对象更新记录，如果对象属性为null,不更新对应的字段
     *
     * @param baseObject 更新记录
     * @return 更新成功后返回的对象
     */
    T updateByIDAndReturnObject(T baseObject);


    /**
     * 根据传入的对象更新记录，更新时对uniqueFieldName做唯一检查，如果已经存在同值且ID不同的记录抛出异常，如果对象属性为null,插入时对应字段为默认值
     *
     * @param baseObject      要插入的对象
     * @param uniqueFieldName 字段值唯一的字段名称
     * @return 更新成功后返回的对象
     */
    T updateByIDAndReturnObject(T baseObject, String uniqueFieldName);


    /**
     * 根据传入的对象更新记录，更新时对uniqueFieldName做唯一检查，如果已经存在同值且ID不同的记录抛出异常，如果对象属性为null,插入时对应字段为默认值
     *
     * @param baseObject       要插入的对象
     * @param uniqueFieldName  字段值唯一的字段名称
     * @param uniqueFieldLabel 字段值唯一的字段标签,如果此参数以&&开头，表示此参数为用户自定义的提示信息
     * @return 更新成功后返回的对象
     */
    T updateByIDAndReturnObject(T baseObject, String uniqueFieldName, String uniqueFieldLabel);


    /**
     * 根据传入的对象更新记录，更新时对uniqueFieldNames中的所有字段做唯一检查，如果已经存在同值且ID不同的记录抛出异常，如果对象属性为null,插入时对应字段为默认值
     *
     * @param baseObject       要插入的对象
     * @param uniqueFieldNames 字段值唯一的字段名称列表
     * @return 更新成功后返回的对象
     */
    T updateByIDAndReturnObject(T baseObject, List<String> uniqueFieldNames);

    /**
     * 根据传入的对象更新记录，更新时对uniqueFieldNames中的所有字段做唯一检查，如果已经存在同值且ID不同的记录抛出异常，如果对象属性为null,插入时对应字段为默认值
     *
     * @param baseObject        要插入的对象
     * @param uniqueFieldNames  字段值唯一的字段名称列表
     * @param uniqueFieldLabels 字段值唯一的字段标签列表,如果此某一项以&&开头，表示此参数为用户自定义的提示信息
     * @return 更新成功后返回的对象
     */
    T updateByIDAndReturnObject(T baseObject, List<String> uniqueFieldNames, List<String> uniqueFieldLabels);


    /**
     * 根据传入的对象更新记录，更新时对uniqueFieldNames中的所有字段做唯一检查;对uniqueCombinedFieldNamesList中所有组合字段做唯一检查;如果已经存在同值且ID不同的记录抛出异常，如果对象属性为null,插入时对应字段为默认值
     *
     * @param baseObject                    要插入的对象
     * @param uniqueFieldNames              字段值唯一的字段名称列表
     * @param uniqueFieldLabels             字段值唯一的字段标签列表, 如果此某一项以&&开头，表示此参数为用户自定义的提示信息
     * @param uniqueCombinedFieldNamesList  组合字段值唯一的字段名称组合列表
     * @param uniqueCombinedFieldLabelsList 组合字段值唯一的字段标签组合列表, 如果此某一项以&&开头，表示此参数为用户自定义的提示信息
     * @return 更新成功后返回的对象
     */
    T updateByIDAndReturnObject(T baseObject, List<String> uniqueFieldNames, List<String> uniqueFieldLabels, List<List<String>> uniqueCombinedFieldNamesList, List<List<String>> uniqueCombinedFieldLabelsList);


    int removeByIdsWithFill(List<DeleteDTO> removeObject);

    /**
     * 批量删除记录
     *
     * @param removeObjects 要删除的对象列表
     * @return 删除记录数
     */
    int removeByObjectsWithFill(List<T> removeObjects);

    Long getOperatorId();

    SysRoleTypeEnum getSysRoleType();

    String getOperatorName();

    Long getTenantId();
}

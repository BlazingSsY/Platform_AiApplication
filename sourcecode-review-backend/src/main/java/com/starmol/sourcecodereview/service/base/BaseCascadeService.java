package com.starmol.sourcecodereview.service.base;

import com.starmol.sourcecodereview.constant.SelfTreeOperationSettingEnum;
import com.starmol.sourcecodereview.model.base.DeleteDTO;

import java.util.List;

public interface BaseCascadeService<T> extends BaseService<T> {

    /**
     * 修改自Tree的节点处理方式。
     *
     * @param selfTreeOperationSetting 注册方式
     */
    void setSelfTreeOperationSetting(SelfTreeOperationSettingEnum selfTreeOperationSetting);

    /**
     * 级联删除，调用入口函数: 按照注册关系和注册设置，在删除主表数据的同时，对子孙表的数据进行删除或检查，同时删除属性表的相关数据。
     *
     * @param removeObjects 要删除的对象列表
     * @return 删除的主数据id列表
     */
    List<String> removeObjectsByObjectList(List<DeleteDTO> removeObjects);


    /**
     * 级联删除，调用入口函数: 按照注册关系和注册设置，在删除主表数据的同时，对子孙表的数据进行删除或检查，同时删除属性表的相关数据。
     *
     * @param removeObjects       要删除的对象列表
     * @param handleCascadeRemove : 是否进行级联删除操作，如果此值为false, 则不进行子对象级联删除(当级联删除的灵活度不够时,用户可调用removeByObjectsWithFill方法，然后自行完成级联删除)
     * @return 删除的主数据id列表
     */
    List<String> removeObjectsByObjectList(List<T> removeObjects, boolean handleCascadeRemove);

}

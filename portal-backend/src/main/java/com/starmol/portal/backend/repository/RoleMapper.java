package com.starmol.portal.backend.repository;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starmol.portal.backend.bean.vo.RoleVO;
import com.starmol.portal.backend.model.Role;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色操作接口
 * @author :Yuexiaopeng
 * @date : 2019/12/10
 *
 **/
public interface RoleMapper extends BaseMapper<Role> {
    String selectByPowerIdAndNameSQL = "<script>"
            + "SELECT a.* FROM urm_role a INNER JOIN urm_rolepower b ON a.id=b.role_id and a.is_delete=0 and b.is_delete=0 WHERE true "
            + "<if test='name!=null and name!=\"\"'>"
            + " and a.name like concat('%',#{name},'%')"
            + "</if>"
            + "<if test='powerId!=null and powerId!=\"\"'>"
            + " and b.power_id = #{powerId}"
            + "</if>"
            + "</script>";

    String selectExcludedByPowerIdAndNameSQL = "<script>"
            + "select a.* FROM urm_role a LEFT JOIN urm_rolepower b ON a.id = b.role_id AND b.power_id= #{powerId} AND a.is_delete=0 AND b.is_delete=0 WHERE a.is_delete=0 and b.id IS NULL"
            + "<if test='name!=null and name!=\"\"'>"
            + " and a.name like concat('%',#{name},'%')"
            + "</if>"
            + "</script>";

    String selectByUserIdAndNameSQL = "<script>"
            + "SELECT a.* FROM urm_role a INNER JOIN urm_userrole b ON a.id=b.role_id and a.is_delete=0 and b.is_delete=0 WHERE true "
            + "<if test='name!=null and name!=\"\"'>"
            + " and a.name like concat('%',#{name},'%')"
            + "</if>"
            + "<if test='userId!=null'>"
            + " and b.user_id = #{userId}"
            + "</if>"
            + "</script>";

    String selectRoleVOByUserIdAndNameSQL = "<script>"
            + "select a.*, (CASE WHEN b.id IS NULL THEN 0 ELSE 1 END) selected FROM urm_role a LEFT JOIN urm_userrole b "
            + "ON a.id = b.role_id AND b.user_id=#{userId} AND a.is_delete=0 AND b.is_delete=0 WHERE a.is_delete=0 "
            + "<if test='name!=null and name!=\"\"'>"
            + " and a.name like concat('%',#{name},'%')"
            + "</if>"
            + "<if test='sortField!=null and sortField!=\"\"'>"
            + " ORDER BY ${sortField}"
            + "</if>"
            + "</script>";

    /**
     * @param name    角色名称
     * @param powerId 权限id
     */
    @Select({selectByPowerIdAndNameSQL})
    List<Role> getRoleListByPowerId(@Param("name") String name, @Param("powerId") Long powerId);

    /**
     * @param page    翻页对象，可以作为 xml 参数直接使用，传递参数 Page 即自动分页
     * @param name    角色名称
     * @param powerId 权限id
     */
    @Select({selectByPowerIdAndNameSQL})
    IPage<Role> getRoleListPageByPowerId(IPage<Role> page, @Param("name") String name, @Param("powerId") Long powerId);

    /**
     * 获取未使用指定权限的角色列表
     *
     * @param page    翻页对象，可以作为 xml 参数直接使用，传递参数 Page 即自动分页
     * @param name    角色名称
     * @param powerId 权限id
     */
    @Select({selectExcludedByPowerIdAndNameSQL})
    IPage<Role> getExcludedRoleListPageByPowerId(IPage<Role> page, @Param("name") String name, @Param("powerId") Long powerId);

    /**
     * @param name   角色名称
     * @param userId 用户id
     */
    @Select({selectByUserIdAndNameSQL})
    List<Role> getRoleListByUserId(@Param("name") String name, @Param("userId") Long userId);

    /**
     * @param page   翻页对象，可以作为 xml 参数直接使用，传递参数 Page 即自动分页
     * @param name   角色名称
     * @param userId 用户id
     */
    @Select({selectByUserIdAndNameSQL})
    IPage<Role> getRoleListPageByUserId(IPage<Role> page, @Param("name") String name, @Param("userId") Long userId);


    /**
     * @param page   翻页对象，可以作为 xml 参数直接使用，传递参数 Page 即自动分页
     * @param name   角色名称
     * @param userId 用户id
     */
    @Select({selectRoleVOByUserIdAndNameSQL})
    IPage<RoleVO> getRoleVOListPageByUserId(IPage<RoleVO> page, @Param("name") String name, @Param("userId") Long userId, @Param("sortField") String sortField);
}
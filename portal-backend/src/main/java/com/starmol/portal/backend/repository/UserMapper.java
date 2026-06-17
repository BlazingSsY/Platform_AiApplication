package com.starmol.portal.backend.repository;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starmol.portal.backend.bean.vo.UserVO;
import com.starmol.portal.backend.model.User;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {
    String selectByGroupIdAndNameSQL = "<script>"
            + "SELECT a.* FROM urm_user a INNER JOIN urm_usergroup b ON a.id=b.user_id and a.is_delete=0 and b.is_delete=0 WHERE true "
            + "<if test='name!=null and name!=\"\"'>"
            + " and a.name like concat('%',#{name},'%')"
            + "</if>"
            + "<if test='groupId!=null and groupId!=\"\"'>"
            + " and b.group_id = #{groupId}"
            + "</if>"
            + "<if test='loginName!=null and loginName!=\"\"'>"
            + "and a.login_name like concat('%',#{loginName},'%')"
            + "</if>"
            + "</script>";

    String selectExcludedByGroupIdAndNameSQL = "<script>"
            + "select a.* FROM urm_user a LEFT JOIN urm_usergroup b ON a.id = b.user_id AND b.group_id= #{groupId} AND a.is_delete=0 AND b.is_delete=0 WHERE a.is_delete=0 and b.id IS NULL"
            + "<if test='name!=null and name!=\"\"'>"
            + " and a.name like concat('%',#{name},'%')"
            + "</if>"
            + "<if test='loginName!=null and loginName!=\"\"'>"
            + "and a.login_name like concat('%',#{loginName},'%')"
            + "</if>"
            + "</script>";

    String selectByRoleIdAndNameSQL = "<script>"
            + "SELECT a.* FROM urm_user a INNER JOIN urm_userrole b ON a.id=b.user_id and a.is_delete=0 and b.is_delete=0 WHERE true "
            + "<if test='name!=null and name!=\"\"'>"
            + " and a.name like concat('%',#{name},'%')"
            + "</if>"
            + "<if test='roleId!=null and roleId!=\"\"'>"
            + " and b.role_id = #{roleId}"
            + "</if>"
            + "<if test='loginName!=null and loginName!=\"\"'>"
            + "and a.login_name like concat('%',#{loginName},'%')"
            + "</if>"
            + " order by name"
            + "</script>";

    String selectExcludedByRoleIdAndNameSQL = "<script>"
            + "select a.* FROM urm_user a LEFT JOIN urm_userrole b ON a.id = b.user_id AND b.role_id= #{roleId} AND a.is_delete=0 AND b.is_delete=0 WHERE a.is_delete=0 and b.id IS NULL"
            + "<if test='name!=null and name!=\"\"'>"
            + " and a.name like concat('%',#{name},'%')"
            + "</if>"
            + "<if test='loginName!=null and loginName!=\"\"'>"
            + "and a.login_name like concat('%',#{loginName},'%')"
            + "</if>"
            + " order by name"
            + "</script>";


    /**
     * @param page 翻页对象，可以作为 xml 参数直接使用，传递参数 Page 即自动分页
     */
    @Select({"<script>",
            "SELECT a.*,b.name as departmentName FROM urm_user a LEFT JOIN urm_department b ON a.department_id=b.id and a.is_delete=0 and b.is_delete=0 WHERE a.is_delete=0 ",
            "<if test='departmentId!=null and departmentId!=\"\"'>",
            " and a.department_id = #{departmentId}",
            "</if>",
            "<if test='departmentId==\"\"'>",
            " and (a.department_id = #{departmentId} or a.department_id IS NULL)",
            "</if>",
            "<if test='loginName!=null and loginName!=\"\"'>",
            "and a.login_name like concat('%',#{loginName},'%')",
            "</if>",
            "<if test='userName!=null and userName!=\"\"'>",
            "and a.name like concat('%',#{userName},'%')",
            "</if>",
            "<if test='email!=null and email!=\"\"'>",
            "and a.email like concat('%',#{email},'%')",
            "</if>",
            "<if test='hasRoleUserIdList!=null and hasRoleUserIdList.size()>0'>",
            " and a.id in <foreach collection='hasRoleUserIdList' item='userId' open='(' separator=',' close=')'>#{userId}</foreach>",
            "</if>",
            " order by name",
            "</script>"
    })
    IPage<UserVO> getUserVO(IPage<UserVO> page,  @Param("departmentId") Long departmentId, @Param("loginName") String loginName, @Param("userName") String userName, @Param("email") String email, @Param("hasRoleUserIdList") List<Long> hasRoleUserIdList);

    /**
     * 根据用户id列表获取用户信息（含部门名称）
     *
     * @param userIds 用户id列表
     * @return 用户VO列表（包含部门名称）
     */
    @Select({"<script>",
            "SELECT a.*, b.name as departmentName FROM urm_user a LEFT JOIN urm_department b ON a.department_id = b.id WHERE 0 = 0",
            "<if test='userIds!=null and userIds.size()>0'>",
            " AND a.id IN <foreach collection='userIds' item='id' open='(' separator=',' close=')'>#{id}</foreach>",
            "</if>",
            "</script>"
    })
    List<UserVO> getUserVOListByIds(@Param("userIds") List<Long> userIds);

    /**
     * 根据角色id获取用户信息（含部门名称）
     *
     * @param roleId 角色id
     * @return 用户VO列表（包含部门名称）
     */
    @Select({"SELECT a.*, d.name as departmentName FROM urm_user a ",
            "INNER JOIN urm_userrole b ON a.id = b.user_id ",
            "LEFT JOIN urm_department d ON a.department_id = d.id ",
            "WHERE b.role_id = #{roleId} ",
            "ORDER BY a.name"
    })
    List<UserVO> getUserVOListByRoleId(@Param("roleId") Long roleId);

    /**
     * @param name    用户名称
     * @param groupId 组id
     */
    @Select({selectByGroupIdAndNameSQL})
    List<User> getUserListByGroupId(@Param("name") String name, @Param("groupId") String groupId, @Param("loginName") String loginName);

    /**
     * @param page    翻页对象，可以作为 xml 参数直接使用，传递参数 Page 即自动分页
     * @param name    用户名称
     * @param groupId 组id
     */
    @Select({selectByGroupIdAndNameSQL})
    IPage<User> getUserListPageByGroupId(IPage<User> page, @Param("name") String name, @Param("groupId") Long groupId, @Param("loginName") String loginName);

    /**
     * 获取未使用指定组的用户列表
     *
     * @param page    翻页对象，可以作为 xml 参数直接使用，传递参数 Page 即自动分页
     * @param name    用户名称
     * @param groupId 组id
     */
    @Select({selectExcludedByGroupIdAndNameSQL})
    IPage<User> getExcludedUserListPageByGroupId(IPage<User> page, @Param("name") String name, @Param("groupId") Long groupId, @Param("loginName") String loginName);

    /**
     * @param name   用户名称
     * @param roleId 角色id
     */
    @Select({selectByRoleIdAndNameSQL})
    List<User> getUserListByRoleId(@Param("name") String name, @Param("roleId") Long roleId, @Param("loginName") String loginName);


    /**
     * @param page   翻页对象，可以作为 xml 参数直接使用，传递参数 Page 即自动分页
     * @param name   用户名称
     * @param roleId 角色id
     */
    @Select({selectByRoleIdAndNameSQL})
    IPage<User> getUserListPageByRoleId(IPage<User> page, @Param("name") String name, @Param("roleId") Long roleId, @Param("loginName") String loginName);


    /**
     * 获取未使用指定角色的用户列表
     *
     * @param page   翻页对象，可以作为 xml 参数直接使用，传递参数 Page 即自动分页
     * @param name   用户名称
     * @param roleId 角色id
     */
    @Select({selectExcludedByRoleIdAndNameSQL})
    IPage<User> getExcludedUserListPageByRoleId(IPage<User> page, @Param("name") String name, @Param("roleId") Long roleId,@Param("loginName") String loginName);
    /**
     * 获取所有用户列表(包括逻辑删除的)
     * 这是一个在XML里面写SQL的示例函数, 在本工程中无用
     */
    List<User> selectAll();

    @InterceptorIgnore(tenantLine = "true")
    User getUserByPhone(String phone);

    @InterceptorIgnore(tenantLine = "true")
    void updateByUserId(User user);
}
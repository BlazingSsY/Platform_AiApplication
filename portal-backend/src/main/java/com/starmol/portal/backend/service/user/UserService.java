package com.starmol.portal.backend.service.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starmol.portal.backend.bean.dto.*;
import com.starmol.portal.backend.bean.dto.*;
import com.starmol.portal.backend.bean.vo.UserListVO;
import com.starmol.portal.backend.bean.vo.UserLoginVO;
import com.starmol.portal.backend.bean.vo.UserVO;
import com.starmol.portal.backend.common.ResponseWrapper;
import com.starmol.portal.backend.model.User;
import com.starmol.portal.backend.model.base.DeleteDTO;
import com.starmol.portal.backend.service.base.BaseCascadeService;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

/**
 * 用户逻辑层
 *
 * @author :Yuexiaopeng
 * @date :2019/12/10
 */
public interface UserService extends BaseCascadeService<User> {
    /**
     * 下载模板
     */

    void downLoadTemplate(HttpServletResponse response) throws Exception;


    /**
     * 导入用户信息
     *
     * @param file 上传的到Excel文件
     * @return 是否导入成功
     */
    Integer importUserInfo(MultipartFile file) throws Exception;

    /**
     * 根据用户名查询用户
     *
     * @param loginName 用户登录名
     * @return 用户对象
     */
    User findByLoginName(String loginName);

    /**
     * 用事务的方式新增用户，并检查关联关系
     *
     * @param user 要创建的用户对象
     * @return 返回的用户对象
     */
    User saveAndReturnObjectWithRelationCheck(User user);


    /**
     * 用事务的方式删除用户列表
     *
     * @param removeUsers 要删除的用户列表
     */
    void removeUsersWithTransaction(List<DeleteDTO> removeUsers);

    /**
     * 用事务的方式修改用户，并检查关联关系
     *
     * @param user 要修改的用户对象
     * @return 返回的用户对象
     */
    User updateByIdAndReturnObjectWithRelationCheck(User user);

    /**
     * 用事务的方式批量修改用户所属部门，并检查关联关系
     *
     * @param relations 要修改的用户(包含id, gradeid 和 version)
     */
    void updateDepartmentRelationsByIdWithRelationCheck(List<RelationDTO> relations);

    /**
     * 根据用Id修改用户锁状态(即：启用/禁用用户)
     *
     * @param changeUserLockStatusDTO 新的用户锁状态实体
     */
    void changeUserLockStatusById(Long id, ChangeUserLockStatusDTO changeUserLockStatusDTO);

    /**
     * 清列表那个中容易字段的值
     *
     * @param userVoByPage 要修改的列表
     */
    void clearRedundantFieldValue(IPage<UserVO> userVoByPage);

    /**
     * 获取带有部门名称的用户列表
     *
     * @param page 分页参数
     * @param departmentId 部门ID
     * @param loginName 用户登陆名
     * @return 用户列表
     */
    IPage<UserVO> getUserPageWithDepartmentName(IPage<UserVO> page, Long departmentId,String loginName, String userName, String email, List<Long> hasRoleUserIdList);

    /**
     * 根据用户id列表获取用户信息（含部门名称）
     *
     * @param userIds 用户id列表
     * @return 用户VO列表（包含部门名称）
     */
    List<UserVO> getUserVOListByIds(List<Long> userIds);

    /**
     * 根据角色id获取用户信息（含部门名称）
     *
     * @param roleId 角色id
     * @return 用户VO列表（包含部门名称）
     */
    List<UserVO> getUserVOListByRoleId(Long roleId);

    /**
     * 根据组ID获取用户列表
     *
     * @param name      用户名称
     * @param groupId   组ID
     * @param loginName 用户登陆名
     * @return 用户列表
     */
    List<User> getUserListByGroupId(String name, String groupId, String loginName);

    /**
     * 根据组ID获取用户列表,分页显示
     *
     * @param page      分页对象
     * @param name      用户名称
     * @param groupId   组ID
     * @param loginName 用户登陆名
     * @return 用户列表
     */
    IPage<User> getUserListPageByGroupId(IPage<User> page, String name, Long groupId, String loginName);

    /**
     * 获取未使用指定组的用户列表,分页显示
     *
     * @param page      分页对象
     * @param name      用户名称
     * @param groupId   组ID
     * @param loginName 用户登陆名
     * @return 用户列表
     */
    IPage<User> getExcludedUserListPageByGroupId(IPage<User> page, String name, Long groupId, String loginName);

    /**
     * 根据角色ID获取用户列表
     *
     * @param name      用户名称
     * @param roleId    角色ID
     * @param loginName 用户登陆名
     * @return 用户列表
     */
    List<User> getUserListByRoleId(String name, Long roleId, String loginName);

    /**
     * 根据角色ID获取用户列表,分页显示
     *
     * @param page      分页对象
     * @param userName  用户名称
     * @param roleId    角色ID
     * @param loginName 用户登陆名
     * @return 用户列表
     */
    IPage<User> getUserListPageByRoleId(IPage<User> page, String userName, Long roleId, String loginName);

    /**
     * 根据角色ID获取未使用此角色的用户列表,分页显示
     *
     * @param page      分页对象
     * @param name      用户名称
     * @param roleId    角色ID
     * @param loginName 用户登陆名
     * @return 用户列表
     */
    IPage<User> getExcludedUserListPageByRoleId(IPage<User> page, String name, Long roleId, String loginName);

    /**
     * 根据条件完成QueryWrapper
     *
     * @param departmentId 部门id
     * @param queryWrapper 完成的QueryWrapper
     * @return 完成的QueryWrapper
     */
    QueryWrapper<User> completeQueryWrapper(Long departmentId, QueryWrapper<User> queryWrapper);

    /**
     * 根据条件获取用户列表(只包含可输出信息)
     *
     * @param departmentId 部门id
     *
     * @return 用户列表(只包含可输出信息)
     */
    List<UserListVO> listUserByFilter(Long departmentId);

    /**
     * 动态登陆验证，判断是否存在sso配置
     *
     * @param userLoginBo 用户登录时,接收、保存用户名和密码
     * @return ResponseWrapper<UserLoginVO>
     */
    ResponseWrapper<UserLoginVO> login(UserLoginDTO userLoginBo);

    /**
     * 根据User对象获取对应的UserLoginVO对象
     *
     * @param user 用户对象
     * @return UserLoginVO 对象
     */
    UserLoginVO getUserLoginVoFromUser(User user);

    /**
     * 刷新Token
     *
     * @param header 包含原Token的header
     * @return 包含Token的ResponseWrapper
     */
    ResponseWrapper<Void> refreshToken(Map<String, String> header);

    /**
     * 用户修改密码
     *
     * @param updatePasswordDTO 保存用户名的旧密码和新密码
     */
    void updatePasswordWithCheck(UpdatePasswordDTO updatePasswordDTO);

    /**
     * 管理员将用户重置为指定的密码(可在前端生成随机密码)
     * @param resetPasswordDTO 保存用户名和密码
     */
    String updatePassword(ResetPasswordDTO resetPasswordDTO);

    /**
     * 根据用户名查询用户详细信息
     *
     * @param loginName 用户登录名
     * @return 用户（包含所属组）对象
     */
    User getUserWithoutSensitiveInfo(String loginName);

    IPage<UserVO> getUserListByPage(Long pageNumber, Long pageSize, Boolean includeDepartmentName, Long departmentId, Long groupId, Long roleId, Boolean include, String loginName, String userName, String email);
}

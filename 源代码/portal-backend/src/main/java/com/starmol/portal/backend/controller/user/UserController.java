package com.starmol.portal.backend.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starmol.portal.backend.aop.Permit;
import com.starmol.portal.backend.bean.dto.*;
import com.starmol.portal.backend.bean.dto.*;
import com.starmol.portal.backend.bean.vo.UserListVO;
import com.starmol.portal.backend.bean.vo.UserLoginVO;
import com.starmol.portal.backend.bean.vo.UserVO;
import com.starmol.portal.backend.common.Permission;
import com.starmol.portal.backend.common.ResponseWrapper;
import com.starmol.portal.backend.common.UserMetaData;
import com.starmol.portal.backend.model.Department;
import com.starmol.portal.backend.model.Role;
import com.starmol.portal.backend.model.User;
import com.starmol.portal.backend.model.UserRole;
import com.starmol.portal.backend.model.base.DeleteDTO;
import com.starmol.portal.backend.service.user.*;
import com.starmol.portal.backend.service.user.*;
import com.starmol.portal.backend.utils.ExceptionUtils;
import com.starmol.portal.backend.utils.HttpRequestUtil;
import com.starmol.portal.backend.utils.StringUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Tag(name = "用户管理")
@RestController
@RequestMapping(value = "/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final LogRecordService logRecordService;
    private final DepartmentService departmentService;
    private final RoleService roleService;
    private final UserRoleService userRoleService;
    private final CaptchaService captchaService;

    @Permit(value = Permission.ALL)
    @Operation(summary = "用户导入模板下载")
    @GetMapping(value = "/export")
    public void  downloadTemplate(HttpServletResponse response) {
        try {
            userService.downLoadTemplate(response);
        } catch (Exception e) {
            log.error(String.format("export template error:%s", e.getMessage()), e);
            logRecordService.insert("用户信息导入", "用户信息模板下载失败");
        }
    }

    @Operation(summary = "用户信息信息导入")
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseWrapper<Integer> importResource(@RequestParam MultipartFile file) {
        ResponseWrapper<Integer> wrapper = new ResponseWrapper<>();
        try {
            Integer importResult = userService.importUserInfo(file);;
            wrapper.setContent(importResult).setSucc(true);
            wrapper.setMsg("导入用户成功");
            logRecordService.insert("用户信息信息导入", "用户信息信息导入成功");
        } catch (Exception e) {
            log.error(String.format("import infos error:%s", e.getMessage()), e);
            wrapper.setSucc(false);
            wrapper.setMsg(ExceptionUtils.getLocalMessage(e));
            logRecordService.insert("用户信息信息导入", "用户信息信息导入失败");
        }
        return wrapper;
    }

    /**
     * 用户登录
     */
    @Operation(summary = "创建用户", description = "创建用户")
    @PostMapping
    public ResponseWrapper<User> createUser(@RequestBody User user) {
        ResponseWrapper<User> wrapper = new ResponseWrapper<>();
        try {
            User returnUser = userService.saveAndReturnObjectWithRelationCheck(user);
            wrapper.setContent(returnUser);
            wrapper.setSucc(true);
            wrapper.setMsg("创建用户成功");
            logRecordService.insert("用户管理", String.format("创建用户: %s 成功", StringUtil.getLogDescription(returnUser.getId(), returnUser.getName())));
        } catch (Exception e) {
            wrapper.setSucc(false);
            wrapper.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Create user error:%s", e.getMessage()), e);
            logRecordService.insert("用户管理", String.format("创建用户: %s 失败", StringUtil.getLogDescription(user.getId(), user.getName())));
        }
        return wrapper;
    }

    /**
     * 根据删除对象列表安全删除用户，全部删除成功表示当前操作成功，否则回滚操作并返回操作失败
     */
    @Operation(summary = "删除用户", description = "删除用户(安全删除)")
    @PatchMapping
    public ResponseWrapper<String> deleteUserSafely(@RequestBody List<DeleteDTO> deleteObjects) {
        ResponseWrapper<String> wrapper = new ResponseWrapper<>();
        String formatIds = StringUtil.cutString(deleteObjects.stream().map(StringUtil::getLogDescription).collect(Collectors.joining(",")), 200);
        try {
            userService.removeUsersWithTransaction(deleteObjects);
            wrapper.setContent(deleteObjects.stream().map(m -> m.getId().toString()).collect(Collectors.joining(",")));
            wrapper.setSucc(true);
            wrapper.setMsg("删除用户成功");
            logRecordService.insert("用户管理", String.format("删除用户: %s 成功", formatIds));
        } catch (Exception e) {
            wrapper.setSucc(false);
            wrapper.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Delete User error:%s", e.getMessage()), e);
            logRecordService.insert("用户管理", String.format("删除用户: %s 失败", formatIds));
        }
        return wrapper;
    }

    /**
     * 根据id修改用户
     */
    @Operation(summary = "修改用户", description = "根据id修改用户")
    @Parameters(
            {
                    @Parameter(in = ParameterIn.PATH, name = "id", schema = @Schema(type = "long"), description = "用户id")
            }
    )
    @PutMapping("/{id}")
    public ResponseWrapper<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        ResponseWrapper<User> wrapper = new ResponseWrapper<>();
        try {
            user.setId(id);
            User returnUser = userService.updateByIdAndReturnObjectWithRelationCheck(user);
            wrapper.setContent(returnUser);
            wrapper.setSucc(true);
            wrapper.setMsg("修改用户成功");
            logRecordService.insert("用户管理", String.format("修改用户: %s 成功", StringUtil.getLogDescription(returnUser.getId(), returnUser.getName())));
        } catch (Exception e) {
            wrapper.setSucc(false);
            wrapper.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Update user error:%s", e.getMessage()), e);
            logRecordService.insert("用户管理", String.format("修改用户: %s 失败", StringUtil.getLogDescription(user.getId(), user.getName())));
        }
        return wrapper;
    }

    @Operation(summary = "禁用/启用用户", description = "根据id禁用/启用用户(即修改所状态)")
    @Parameters(
            {
                    @Parameter(in = ParameterIn.PATH, name = "id", schema = @Schema(type = "long"), description = "用户id")
            }
    )
    @PutMapping("lock-status/{id}")
    public ResponseWrapper<ChangeUserLockStatusDTO> lockUser(@PathVariable Long id, @RequestBody ChangeUserLockStatusDTO changeUserLockStatusDTO) {
        ResponseWrapper<ChangeUserLockStatusDTO> wrapper = new ResponseWrapper<>();
        try {
            userService.changeUserLockStatusById(id, changeUserLockStatusDTO);
            wrapper.setContent(changeUserLockStatusDTO);
            wrapper.setSucc(true);
            wrapper.setMsg((changeUserLockStatusDTO.getNewStatus()?"禁用":"启用")+"用户成功");
            logRecordService.insert("用户管理", String.format("禁用/启用用户: %s 成功", StringUtil.getLogDescription(id, changeUserLockStatusDTO.getLoginName())));
        } catch (Exception e) {
            wrapper.setSucc(false);
            wrapper.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Update user error:%s", e.getMessage()), e);
            logRecordService.insert("用户管理", String.format("禁用/启用: %s 失败", StringUtil.getLogDescription(id, changeUserLockStatusDTO.getLoginName())));
        }
        return wrapper;
    }

    /**
     * 批量修改用户隶属关系
     */
    @Operation(summary = "批量修改用户隶属关系", description = "批量修改用户隶属关系")
    @PutMapping("/department-relations")
    public ResponseWrapper<User> updateUserRelations(@RequestBody List<RelationDTO> relations) {
        String formatIds = StringUtil.cutString(relations.stream().map(StringUtil::getLogDescription).collect(Collectors.joining(",")), 200);
        ResponseWrapper<User> wrapper = new ResponseWrapper<>();
        try {
            userService.updateDepartmentRelationsByIdWithRelationCheck(relations);
            wrapper.setSucc(true);
            wrapper.setMsg("修改用户隶属关系成功");
            logRecordService.insert("用户管理", String.format("修改用户: %s 的隶属关系成功", formatIds));
        } catch (Exception e) {
            wrapper.setSucc(false);
            wrapper.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Update user error:%s", e.getMessage()), e);
            logRecordService.insert("用户管理", String.format("修改用户: %s 的隶属关系失败", formatIds));
        }
        return wrapper;
    }


    /**
     * 用户修改密码
     * @param updatePasswordDTO 保存用户名的旧密码和新密码
     * @return 请求是否成功
     */
    @Permit(value = Permission.ALL)
    @Operation(summary = "用户修改自己的登录密码", description = "用户修改自己的登录密码")
    @PostMapping("/password")
    public ResponseWrapper<Void> updatePassword(@RequestBody UpdatePasswordDTO updatePasswordDTO) {
        log.debug("Enter the function updatePassword");
        ResponseWrapper<Void> wrapper = new ResponseWrapper<>();
        try {
            userService.updatePasswordWithCheck(updatePasswordDTO);
            logRecordService.insert("用户管理", String.format("用户: %s 修改密码成功", StringUtil.getLogDescription(null, updatePasswordDTO.getLoginName())));
        } catch (Exception e) {
            wrapper.setSucc(false);
            wrapper.setMsg(ExceptionUtils.getLocalMessage(e));
            logRecordService.insert("用户管理", String.format("用户: %s 修改密码失败", StringUtil.getLogDescription(null, updatePasswordDTO.getLoginName())));
            log.error(String.format("UserController.login error:%s", e.getMessage()), e);
        }
        log.debug("Exit the function updatePassword");
        return wrapper;
    }

    /**
     * 管理员将用户重置为指定的密码(可在前端生成随机密码)
     * @param resetPasswordDTO 保存用户登录名
     * @return 请求是否成功
     */
    @Permit(value = Permission.AUTH)
    @Operation(summary = "管理员将用户密码修改为指定密码", description = "管理员将用户密码修改为指定密码")
    @PostMapping("/password-change")
    public ResponseWrapper<String> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        log.debug("Enter the function resetPassword");
        ResponseWrapper<String> wrapper = new ResponseWrapper<>();
        try {
            String randomPassword = userService.updatePassword(resetPasswordDTO);
            //将前端指定的密码(MD5后的)返回给前端
            wrapper.setContent(randomPassword);
            logRecordService.insert("用户管理", String.format("管理员修改用户: %s 密码成功", StringUtil.getLogDescription(null, resetPasswordDTO.getLoginName())));
            wrapper.setSucc(true);
        } catch (Exception e) {
            wrapper.setSucc(false);
            wrapper.setMsg(ExceptionUtils.getLocalMessage(e));
            logRecordService.insert("用户管理", String.format("管理员修改用户: %s 密码失败", StringUtil.getLogDescription(null, resetPasswordDTO.getLoginName())));
            log.error(String.format("UserController.login error:%s", e.getMessage()), e);
        }
        log.debug("Exit the function resetPassword");
        return wrapper;
    }

    /**
     * 根据类型获取用户列表
     */
    @Operation(summary = "获取用户列表", description = "根据指定类型，获取用户列表;不指定类型时获取全部用户列表")
    @Parameters(
            {
                    @Parameter(in = ParameterIn.QUERY, name = "departmentId", schema = @Schema(type = "long"), description = "部门id")
            }
    )
    @GetMapping
    public ResponseWrapper<List<UserListVO>> getUserList(@RequestParam(required = false) Long departmentId) {
        ResponseWrapper<List<UserListVO>> response = new ResponseWrapper<>();
        try {
            List<UserListVO> userList = userService.listUserByFilter(departmentId);
            response.setContent(userList);
            response.setSucc(true);
            response.setMsg("获取用户列表成功");
        } catch (Exception e) {
            log.error(String.format("Get user list error: %s", e.getMessage()), e);
            response.setSucc(false);
            response.setMsg(ExceptionUtils.getLocalMessage(e));
        }
        return response;
    }

    /**
     * 分页查询用户
     */
    @Operation(summary = "分页查询用户", description = "分页查询用户（按照部门或用户组或角色条件分页查询用户）")
    @Parameters(
            {
                    @Parameter(in = ParameterIn.QUERY, name = "pageNumber", schema = @Schema(type = "int"), description = "页码", example = "1"),
                    @Parameter(in = ParameterIn.QUERY, name = "pageSize", schema = @Schema(type = "int"), description = "每页显示条数", example = "10"),
                    @Parameter(in = ParameterIn.QUERY, name = "includeDepartmentName", schema = @Schema(type = "boolean"), description = "结果是否包含部门名称"),
                    @Parameter(in = ParameterIn.QUERY, name = "type", schema = @Schema(type = "string"), description = "用户类型"),
                    @Parameter(in = ParameterIn.QUERY, name = "departmentId", schema = @Schema(type = "long"), description = "部门id"),
                    @Parameter(in = ParameterIn.QUERY, name = "groupId", schema = @Schema(type = "long"), description = "组id"),
                    @Parameter(in = ParameterIn.QUERY, name = "roleId", schema = @Schema(type = "long"), description = "角色id"),
                    @Parameter(in = ParameterIn.QUERY, name = "include", schema = @Schema(type = "boolean"), description = "是否获取用户角色或组关联的用户"),
                    @Parameter(in = ParameterIn.QUERY, name = "loginName", schema = @Schema(type = "string"), description = "用户登录名")
            }
    )
    @GetMapping("/pages")
    public ResponseWrapper<IPage<UserVO>> getUserListByPage(@RequestParam(defaultValue = "1") Long pageNumber,
                                                            @RequestParam(defaultValue = "10") Long pageSize,
                                                            @RequestParam(required = false, defaultValue = "false") Boolean includeDepartmentName,
                                                            @RequestParam(required = false) Long departmentId,
                                                            @RequestParam(required = false) Long groupId,
                                                            @RequestParam(required = false) Long roleId,
                                                            @RequestParam(required = false) Boolean include,
                                                            @RequestParam(required = false) String loginName,
                                                            @RequestParam(required = false) String userName,
                                                            @RequestParam(required = false) String email
    ) {
        ResponseWrapper<IPage<UserVO>> wrapper = new ResponseWrapper<>();
        try {
            IPage<UserVO> userVoByPage = userService.getUserListByPage(pageNumber, pageSize, includeDepartmentName, departmentId, groupId, roleId, include, loginName, userName, email);
            log.info("Get User list by page success");
            wrapper.setContent(userVoByPage);
            wrapper.setSucc(true);
            wrapper.setMsg("分页查询用户成功");
        } catch (Exception e) {
            wrapper.setSucc(false);
            wrapper.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Get User list by page error:%s", e.getMessage()), e);
        }
        return wrapper;
    }


    /**
     * 根据id获取用户详情
     */
    @Operation(summary = "查看用户详情", description = "根据用户id获取用户详情")
    @Parameters(
            {
                    @Parameter(in = ParameterIn.PATH, name = "id", schema = @Schema(type = "string"), description = "用户id")
            }
    )
    @GetMapping("/{id}")
    public ResponseWrapper<UserVO> getUserDetail(@PathVariable String id) {
        ResponseWrapper<UserVO> response = new ResponseWrapper<>();
        try {
            User newUser = userService.getById(id);
            newUser.setPassword(null);

            UserVO userVO = UserVO.fromUser(newUser);
            Department department = departmentService.getById(newUser.getDepartmentId());
            if (department == null) {
                userVO.setDepartmentName(null);
            } else {
                userVO.setDepartmentName(department.getName());
            }

            List<Role> roleList = new ArrayList<>();
            QueryWrapper<UserRole> queryWrapper = new QueryWrapper<>();
            List<UserRole> userRoles = userRoleService.list(queryWrapper.lambda().eq(UserRole::getUserId, newUser.getId()));
            for (UserRole userRole : userRoles) {
                Role role = roleService.getById(userRole.getRoleId());
                roleList.add(role);
            }
            userVO.setRoleId(roleList);

            response.setContent(userVO);
            response.setSucc(true);
            response.setMsg("查看用户成功");
        } catch (Exception e) {
            log.error(String.format("Get user detail error: %s", e.getMessage()), e);
            response.setSucc(false);
            response.setMsg(ExceptionUtils.getLocalMessage(e));
        }
        return response;
    }

    /**
     * 用户登录
     */
    @Permit(value = Permission.ALL)
    @Operation(summary = "用户登录", description = "通过 loginId 登陆")
    @PostMapping("/login")
    public ResponseWrapper<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO, HttpServletResponse response) {
        log.debug("Enter the function login");
        ResponseWrapper<UserLoginVO> wrapper = new ResponseWrapper<>();
        try {
            // 校验输入用户输入的校验码
            // captchaService.verify(response, capId, captcha); //根据用户要求,登录时不需要校验码
            wrapper = userService.login(userLoginDTO);
            String username = "";
            Long userId = 0L;
            if (Objects.nonNull(wrapper.getContent())) {
                username = wrapper.getContent().getName();
                userId = wrapper.getContent().getId();
                logRecordService.insert("用户管理", String.format("用户: %s 登录成功", StringUtil.getLogDescription(userId, username)));
            } else {
                logRecordService.insert("用户管理", String.format("用户: %s 登录失败", StringUtil.getLogDescription(userId, userLoginDTO.getLoginName())));
            }
        } catch (Exception e) {
            wrapper.setSucc(false);
            wrapper.setMsg(ExceptionUtils.getLocalMessage(e));
            logRecordService.insert("用户管理", String.format("用户: %s 登录失败", StringUtil.getLogDescription(null, userLoginDTO.getLoginName())));
            log.error(String.format("UserController.login error:%s", e.getMessage()), e);
        }
        log.debug("Exit the function login");
        return wrapper;
    }

    /**
     * 用户登录
     */
    @Permit(value = Permission.ALL)
    @Operation(summary = "验证码用户登录", description = "通过 loginId 登陆")
    @PostMapping("/login/{captchaId}")
    public ResponseWrapper<UserLoginVO> loginWithCaptchaId(@PathVariable String captchaId, @RequestBody UserLoginDTO userLoginDTO, HttpServletResponse response) {
        log.debug("Enter the function login");
        ResponseWrapper<UserLoginVO> wrapper = new ResponseWrapper<>();
        try {
            // 校验输入用户输入的校验码
            // captchaService.verify(response, capId, captcha); //根据用户要求,登录时不需要校验码
            wrapper = userService.login(userLoginDTO);
            String username = "";
            Long userId = 0L;
            if (Objects.nonNull(wrapper.getContent())) {
                username = wrapper.getContent().getName();
                userId = wrapper.getContent().getId();
                logRecordService.insert("用户管理", String.format("用户: %s 登录成功", StringUtil.getLogDescription(userId, username)));
            } else {
                logRecordService.insert("用户管理", String.format("用户: %s 登录失败", StringUtil.getLogDescription(userId, userLoginDTO.getLoginName())));
            }
        } catch (Exception e) {
            wrapper.setSucc(false);
            wrapper.setMsg(ExceptionUtils.getLocalMessage(e));
            logRecordService.insert("用户管理", String.format("用户: %s 登录失败", StringUtil.getLogDescription(null, userLoginDTO.getLoginName())));
            log.error(String.format("UserController.login error:%s", e.getMessage()), e);
        }
        log.debug("Exit the function login");
        return wrapper;
    }


    /**
     * 重新获取token
     *
     * @param header 请求头部
     * @return 刷新的token
     */
    @Permit(value = Permission.ALL)
    @Operation(summary = "重新获取token", description = "JWT token过期后使用该API重新获取token")
    @GetMapping("/token")
    public ResponseWrapper<Void> refreshToken(@RequestHeader Map<String, String> header) {
        log.debug("Enter the function refreshToken");
        ResponseWrapper<Void> wrapper = new ResponseWrapper<>();
        try {
            wrapper = this.userService.refreshToken(header);
            wrapper.setSucc(true);
        } catch (Exception e) {
            log.error("login failed:" + e.getMessage());
            wrapper.setSucc(false);
        }
        log.debug("Exit the function refreshToken");
        return wrapper;
    }


    /**
     * 用户登录
     */
    @Operation(summary = "获取当前用户登录信息", description = "获取当前用户登录信息")
    @GetMapping("/my-login-info")
    public ResponseWrapper<UserLoginVO> getCurrentUserLoginInfo() {
        log.debug("Enter the function getCurrentUserLoginInfo");
        ResponseWrapper<UserLoginVO> response = new ResponseWrapper<>();
        try {
            UserMetaData currentUser = HttpRequestUtil.getUser();
            if (currentUser == null) {
                response.setSucc(false);
                response.setMsg("获取当前用户登录信息失败");
                log.error(String.format("UserController.getCurrentUserLoginInfo error:%s", "获取当前UniUserDTO为null"));
                return response;
            }
            response.setContent(userService.getUserLoginVoFromUser(userService.getById(currentUser.getId())));
            response.setSucc(true);
            response.setMsg("获取当前用户登录信息成功");
        } catch (Exception e) {
            response.setSucc(false);
            response.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("UserController.getCurrentUserLoginInfo error:%s", e.getMessage()), e);
        }
        log.debug("Exit the function getCurrentUserLoginInfo");
        return response;
    }


    @Operation(summary = "根据用户登录名获取用户信息", description = "根据用户登录名获取用户信息")
    @Parameters(
            {
                    @Parameter(in = ParameterIn.QUERY, name = "loginName", schema = @Schema(type = "string"), description = "登陆名称")
            }
    )
    @GetMapping("/unique-user")
    public ResponseWrapper<User> getUniqueUser(@RequestParam String loginName) {
        ResponseWrapper<User> response = new ResponseWrapper<>();
        try {
            response.setContent(userService.getUserWithoutSensitiveInfo(loginName));
            response.setSucc(true);
            response.setMsg("根据用户登录名获取用户信息成功");
        } catch (Exception e) {
            log.error(String.format("Get unique user error: %s", e.getMessage()), e);
            response.setSucc(false);
            response.setMsg(ExceptionUtils.getLocalMessage(e));
        }
        return response;
    }

    @Operation(summary = "获取用户列表", description = "获取用户列表")
    @PostMapping("/list-by-ids")
    public ResponseWrapper<List<User>> getUserListByUserIds(@RequestBody List<String> userIds) {
        ResponseWrapper<List<User>> response = new ResponseWrapper<>();
        try {
            final List<User> userList = userService.listByIds(userIds);
            response.setContent(userList);
            response.setSucc(true);
            response.setMsg("获取用户列表成功");
        } catch (Exception e) {
            log.error(String.format("Get user list error: %s", e.getMessage()), e);
            response.setSucc(false);
            response.setMsg(ExceptionUtils.getLocalMessage(e));
        }
        return response;
    }
}

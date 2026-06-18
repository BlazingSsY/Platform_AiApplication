package com.starmol.portal.backend.controller.internal;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starmol.portal.backend.aop.Permit;
import com.starmol.portal.backend.bean.vo.LoginNameDepartmentVO;
import com.starmol.portal.backend.bean.vo.UserVO;
import com.starmol.portal.backend.common.Permission;
import com.starmol.portal.backend.common.ResponseWrapper;
import com.starmol.portal.backend.model.Department;
import com.starmol.portal.backend.model.User;
import com.starmol.portal.backend.service.user.DepartmentService;
import com.starmol.portal.backend.service.user.RoleService;
import com.starmol.portal.backend.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Tag(name = "内部接口")
@RestController
@RequestMapping(value = "/v1/internal")
@RequiredArgsConstructor
public class InternalController {
    private final UserService userService;
    private final DepartmentService departmentService;
    private final RoleService roleService;

    @Permit(value = Permission.ALL)
    @Operation(summary = "根据用户id获取用户信息", description = "根据用户id获取用户信息")
    @Parameters(
            {
                    @Parameter(in = ParameterIn.QUERY, name = "id", schema = @Schema(type = "long"), description = "用户id")
            }
    )
    @GetMapping("users")
    public ResponseWrapper<User> getUserById(@RequestParam Long id) {
        User user = userService.getById(id);
        return ResponseWrapper.success(user);
    }

    @Permit(value = Permission.ALL)
    @Operation(summary = "根据部门id列表获取用户信息列表", description = "根据部门id列表获取用户信息列表")
    @PostMapping("users/department")
    public ResponseWrapper<List<User>> getUsersByDepartmentIds(@RequestBody List<Long> departmentIds) {
        List<User> users = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(departmentIds)) {
            users = userService.list(
                    Wrappers.<User>lambdaQuery()
                            .in(User::getDepartmentId, departmentIds));
        }
        return ResponseWrapper.success(users);
    }

    @Permit(value = Permission.ALL)
    @Operation(summary = "根据用户id列表获取用户信息列表", description = "根据用户id列表获取用户信息列表（含部门名称）")
    @PostMapping("users")
    public ResponseWrapper<List<UserVO>> getUsersByIds(@RequestBody List<Long> userIds) {
        List<UserVO> users = userService.getUserVOListByIds(userIds);
        return ResponseWrapper.success(users);
    }

    @Permit(value = Permission.ALL)
    @Operation(summary = "根据角色ID获取用户列表", description = "根据角色ID获取用户列表（含部门名称）")
    @PostMapping("users/role")
    public ResponseWrapper<List<UserVO>> getUsersByRoleId(@RequestBody Long roleId) {
        List<UserVO> users = userService.getUserVOListByRoleId(roleId);
        return ResponseWrapper.success(users);
    }

    @Permit(value = Permission.ALL)
    @Operation(summary = "根据部门id获取部门信息", description = "根据部门id获取部门信息")
    @Parameters(
            {
                    @Parameter(in = ParameterIn.QUERY, name = "id", schema = @Schema(type = "long"), description = "用部门id")
            }
    )
    @GetMapping("departments")
    public ResponseWrapper<Department> getDepartmentById(@RequestParam Long id) {
        Department department = departmentService.getById(id);
        return ResponseWrapper.success(department);
    }

    @Permit(value = Permission.ALL)
    @Operation(summary = "根据部门id列表获取部门信息列表", description = "根据部门id列表获取部门信息列表")
    @PostMapping("departments")
    public ResponseWrapper<List<Department>> getDepartmentsByIds(@RequestBody List<Long> departmentIds) {
        List<Department> departments = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(departmentIds)) {
            departments = departmentService.list(
                    Wrappers.<Department>lambdaQuery()
                            .in(Department::getId, departmentIds));
        }
        return ResponseWrapper.success(departments);
    }

    @Permit(value = Permission.ALL)
    @Operation(summary = "根据部门名称列表获取部门信息列表", description = "根据部门名称列表获取部门信息列表")
    @PostMapping("department-names")
    public ResponseWrapper<List<Department>> getDepartmentsByNames(@RequestBody List<String> departmentNameList) {
        if (departmentNameList == null || departmentNameList.isEmpty()) {
            return ResponseWrapper.success(new ArrayList<>());
        }
        
        LambdaQueryWrapper<Department> queryWrapper = new LambdaQueryWrapper<>();
        for (int i = 0; i < departmentNameList.size(); i++) {
            if (i == 0) {
                queryWrapper.like(Department::getName, departmentNameList.get(i));
            } else {
                queryWrapper.or().like(Department::getName, departmentNameList.get(i));
            }
        }
        
        List<Department> departments = departmentService.list(queryWrapper);
        return ResponseWrapper.success(departments);
    }

    @Permit(value = Permission.ALL)
    @Operation(summary = "根据部门id获取部门信息列表", description = "根据部门id获取部门信息列表")
    @Parameters(
            {
                    @Parameter(in = ParameterIn.QUERY, name = "id", schema = @Schema(type = "long"), description = "用部门id")
            }
    )
    @GetMapping("department-list")
    public ResponseWrapper<List<Department>> getDepartmentsById(@RequestParam Long id) {
        List<Department> departments = List.of(departmentService.getById(id));
        return ResponseWrapper.success(departments);
    }

    @Permit(value = Permission.ALL)
    @Operation(summary = "根据部门id获取部门及子部门信息列表", description = "根据部门id获取部门及子部门信息列表")
    @Parameters(
            {
                    @Parameter(in = ParameterIn.QUERY, name = "id", schema = @Schema(type = "long"), description = "用部门id")
            }
    )
    @GetMapping("departments-and-children")
    public ResponseWrapper<List<Department>> getDepartmentsByIdOrFId(@RequestParam Long id) {
        List<Department> departments = departmentService.list(
                Wrappers.<Department>lambdaQuery()
                        .eq(Department::getId, id)
                        .or()
                        .eq(Department::getFId, id)
        );
        return ResponseWrapper.success(departments);
    }

    @Permit(value = Permission.ALL)
    @Operation(summary = "根据用户登录名列表获取部门信息列表", description = "根据用户登录名列表获取部门信息列表")
    @PostMapping("login-name-departments")
    public ResponseWrapper<List<LoginNameDepartmentVO>> getDepartmentsByLoginNames(@RequestBody List<String> loginNameList) {
        if (loginNameList == null || loginNameList.isEmpty()) {
            return ResponseWrapper.success(new ArrayList<>());
        }

        List<LoginNameDepartmentVO> loginNameDepartmentVOS = departmentService.getLoginNameDepartmentVO(loginNameList);
        return ResponseWrapper.success(loginNameDepartmentVOS);
    }

}

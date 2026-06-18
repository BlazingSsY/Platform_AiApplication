package com.starmol.portal.backend.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starmol.portal.backend.bean.vo.RoleVO;
import com.starmol.portal.backend.common.ResponseWrapper;
import com.starmol.portal.backend.model.Role;
import com.starmol.portal.backend.model.base.DeleteDTO;
import com.starmol.portal.backend.service.user.LogRecordService;
import com.starmol.portal.backend.service.user.RoleService;
import com.starmol.portal.backend.utils.ExceptionUtils;
import com.starmol.portal.backend.utils.StringUtil;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "角色管理")
@RestController
@RequestMapping(value = "/v1/roles")
public class RoleController {
    private final RoleService roleService;
    private final LogRecordService logRecordService;

    public RoleController(RoleService roleService, LogRecordService logRecordService) {
        this.roleService = roleService;
        this.logRecordService = logRecordService;
    }

    /**
     * 创建角色(只创建角色, 不更新权限记录)
     */
    @Operation(summary = "创建角色", description = "创建角色")
    @PostMapping
    public ResponseWrapper<Role> createRole(@RequestBody Role role) {
        ResponseWrapper<Role> wrapper = new ResponseWrapper<>();
        try {
            Role returnRole = roleService.saveAndReturnObject(role, "name", "角色名");
            //Role returnRole = roleService.saveAndReturnObject(role);//不要求角色名称不能重复的保存方式
            wrapper.setContent(returnRole);
            wrapper.setSucc(true);
            wrapper.setMsg("创建角色成功");
            logRecordService.insert("角色管理", String.format("创建角色: %s 成功", StringUtil.getLogDescription(returnRole.getId(), returnRole.getName())));
        } catch (Exception e) {
            wrapper.setSucc(false);
            wrapper.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Create Role error:%s", e.getMessage()), e);
            logRecordService.insert("角色管理", String.format("创建角色: %s 成功", StringUtil.getLogDescription(role.getId(), role.getName())));
        }
        return wrapper;
    }

    /**
     * 根据删除对象列表安全删除角色，同时把这个角色有关关联删除掉，全部删除成功表示当前操作成功，否则回滚操作并返回操作失败
     */
    @Operation(summary = "删除角色", description = "删除角色(安全删除)")
    @PatchMapping
    public ResponseWrapper<String> removeRoleSafely(@RequestBody List<DeleteDTO> removeObjects) {
        ResponseWrapper<String> wrapper = new ResponseWrapper<>();
        String formatIds = StringUtil.cutString(removeObjects.stream().map(StringUtil::getLogDescription).collect(Collectors.joining(",")), 200);
        try {
            roleService.removeRolesWithTransaction(removeObjects);
            wrapper.setContent(removeObjects.stream().map(m -> m.getId().toString()).collect(Collectors.joining(",")));
            wrapper.setSucc(true);
            wrapper.setMsg("删除角色成功");
            logRecordService.insert("角色管理", String.format("删除角色: %s 成功", formatIds));
        } catch (Exception e) {
            wrapper.setSucc(false);
            wrapper.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Remove Role error:%s", e.getMessage()), e);
            logRecordService.insert("角色管理", String.format("删除角色: %s 失败", formatIds));
        }
        return wrapper;
    }


    /**
     * 根据id删除角色
     */
    @Operation(summary = "删除角色", description = "根据id删除角色(不安全删除)")
    @Parameters(
            {
                    @Parameter(in = ParameterIn.PATH, name = "id", schema = @Schema(type = "long"), description = "角色id")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseWrapper<Long> deleteRole(@PathVariable Long id) {
        ResponseWrapper<Long> response = new ResponseWrapper<>();
        try {
            if (roleService.removeById(id)) {
                response.setContent(id);
                response.setMsg("删除角色成功");
                response.setSucc(true);
                logRecordService.insert("角色管理", String.format("删除ID为: %s 的角色成功", id));
            } else {
                response.setMsg("删除角色失败，请刷新后重新删除");
                response.setSucc(false);
                logRecordService.insert("角色管理", String.format("删除ID为: %s 的角色失败", id));
            }
        } catch (Exception e) {
            response.setSucc(false);
            response.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Remove Role error: %s", e.getMessage()), e);
            logRecordService.insert("角色管理", String.format("删除ID为: %s 的角色失败", id));
        }
        return response;
    }


    /**
     * 根据id列表批量删除角色,删除成功一个即表示当前操作成功
     */
    @Operation(summary = "批量删除角色", description = "根据id列表批量删除角色(不安全删除)")
    @Parameters(
            {
                    @Parameter(in = ParameterIn.QUERY, name = "ids", schema = @Schema(type = "array"), description = "角色id数组")
            }
    )
    @DeleteMapping
    public ResponseWrapper<String> deleteRoles(@RequestParam List<String> ids) {
        ResponseWrapper<String> response = new ResponseWrapper<>();
        String formatIds = StringUtil.cutString(ids.stream().map(String::valueOf).collect(Collectors.joining(",")), 200);
        try {
            if (roleService.removeByIds(ids)) {
                response.setContent(String.join(",", ids));
                response.setMsg("批量删除角色成功");
                response.setSucc(true);
                logRecordService.insert("角色管理", String.format("批量删除ID为: %s 的角色成功", formatIds));
            } else {
                response.setMsg("删除角色失败，请刷新后重新删除");
                response.setSucc(false);
                logRecordService.insert("角色管理", String.format("删除ID为: %s 的角色失败", formatIds));
            }
        } catch (Exception e) {
            response.setSucc(false);
            response.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Remove role error: %s", e.getMessage()), e);
            logRecordService.insert("角色管理", String.format("批量删除ID为: %s 的角色失败", formatIds));
        }
        return response;
    }

    /**
     * 根据id修改角色
     */
    @Operation(summary = "修改角色", description = "根据id修改角色")
    @Parameters(
            {
                    @Parameter(in = ParameterIn.PATH, name = "id", schema = @Schema(type = "string"), description = "角色id")
            }
    )
    @PutMapping("/{id}")
    public ResponseWrapper<Role> updateRole(@PathVariable Long id, @RequestBody Role role) {
        ResponseWrapper<Role> wrapper = new ResponseWrapper<>();
        try {
            role.setId(id);
            Role returnRole = roleService.updateByIDAndReturnObject(role, "name", "角色名");
            //Role returnRole = roleService.updateByIDAndReturnObject(role); //不要求角色名称不能重复的保存方式
            wrapper.setContent(returnRole);
            wrapper.setSucc(true);
            wrapper.setMsg("修改角色成功");
            logRecordService.insert("角色管理", String.format("修改角色: %s 成功", StringUtil.getLogDescription(returnRole.getId(), returnRole.getName())));
        } catch (Exception e) {
            wrapper.setSucc(false);
            wrapper.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Update role error:%s", e.getMessage()), e);
            logRecordService.insert("角色管理", String.format("修改角色: %s 失败", StringUtil.getLogDescription(role.getId(), role.getName())));
        }
        return wrapper;
    }


    /**
     * 根据条件获取角色列表
     */
    @Operation(summary = "获取角色列表", description = "根据指定条件，获取角色列表;不指定条件时获取全部角色列表")
    @Parameters(
            {
                    @Parameter(in = ParameterIn.QUERY, name = "name", schema = @Schema(type = "string"), description = "角色名称"),
                    @Parameter(in = ParameterIn.QUERY, name = "userId", schema = @Schema(type = "long"), description = "用户id")
            }
    )
    @GetMapping
    public ResponseWrapper<List<Role>> getRoleList(@RequestParam(required = false) String name, @RequestParam(required = false) Long userId) {
        ResponseWrapper<List<Role>> response = new ResponseWrapper<>();
        try {
            List<Role> roleList;
            if (Objects.isNull(userId)) {
                QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
                if (!StringUtils.isBlank(name)) {
                    queryWrapper.lambda().like(Role::getName, name);
                }
                roleList = roleService.list(queryWrapper);
            } else {
                roleList = roleService.getRoleListByUserId(name, userId);
            }
            response.setContent(roleList);
            response.setSucc(true);
            response.setMsg("获取角色列表成功");
        } catch (Exception e) {
            log.error(String.format("Get role list error: %s", e.getMessage()), e);
            response.setSucc(false);
            response.setMsg(ExceptionUtils.getLocalMessage(e));
        }
        return response;
    }

    /**
     * 分页查询角色
     */
    @Operation(summary = "分页查询角色", description = "根据条件分页查询角色")
    @Parameters(
            {
                    @Parameter(in = ParameterIn.QUERY, name = "pageNumber", schema = @Schema(type = "int"), description = "页码", example = "1"),
                    @Parameter(in = ParameterIn.QUERY, name = "pageSize", schema = @Schema(type = "int"), description = "每页显示条数", example = "10"),
                    @Parameter(in = ParameterIn.QUERY, name = "name", schema = @Schema(type = "string"), description = "角色名称"),
                    @Parameter(in = ParameterIn.QUERY, name = "powerId", schema = @Schema(type = "string"), description = "权限id"),
                    @Parameter(in = ParameterIn.QUERY, name = "include", schema = @Schema(type = "boolean"), description = "获取是否关联角色的权限")
            }
    )
    @GetMapping("/pages")
    public ResponseWrapper<IPage<Role>> getRoleListByPage(@RequestParam(defaultValue = "1") Long pageNumber,
                                                          @RequestParam(defaultValue = "10") Long pageSize,
                                                          @RequestParam(required = false) String name,
                                                          @RequestParam(required = false) Long powerId,
                                                          @RequestParam(required = false, defaultValue = "true") Boolean include) {
        ResponseWrapper<IPage<Role>> wrapper = new ResponseWrapper<>();
        try {
            IPage<Role> rolesByPage;
            if (include) {
                if (Objects.isNull(powerId)) {
                    QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
                    if (!StringUtils.isBlank(name)) {
                        queryWrapper.lambda().like(Role::getName, name);
                    }
                    rolesByPage = roleService.page(new Page<>(pageNumber, pageSize), queryWrapper);

                } else {
                    rolesByPage = roleService.getRoleListPageByPowerId(new Page<>(pageNumber, pageSize), name, powerId);
                }
            } else {
                if (Objects.isNull(powerId)) {
                    QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
                    if (!StringUtils.isBlank(name)) {
                        queryWrapper.lambda().like(Role::getName, name);
                    }
                    rolesByPage = roleService.page(new Page<>(pageNumber, pageSize), queryWrapper);
                } else {
                    rolesByPage = roleService.getExcludedRoleListPageByPowerId(new Page<>(pageNumber, pageSize), name, powerId);
                }
            }
            log.info("Get role list by page success");
            wrapper.setContent(rolesByPage);
            wrapper.setSucc(true);
            wrapper.setMsg("分页查询角色成功");
        } catch (Exception e) {
            wrapper.setSucc(false);
            wrapper.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Get role list by page error:%s", e.getMessage()), e);
        }
        return wrapper;
    }

    /**
     * 根据角色id获取角色详情
     */
    @Operation(summary = "查看角色详情", description = "根据角色id获取角色详情")
    @Parameters(
            {
                    @Parameter(in = ParameterIn.PATH, name = "id", schema = @Schema(type = "long"), description = "角色id")
            }
    )
    @GetMapping("/{id}")
    public ResponseWrapper<Role> getUserRoleDetail(@PathVariable Long id) {
        ResponseWrapper<Role> response = new ResponseWrapper<>();
        try {
            Role role = roleService.getById(id);
            response.setContent(role);
            response.setSucc(true);
            response.setMsg("查看角色成功");
        } catch (Exception e) {
            log.error(String.format("Get role detail error: %s", e.getMessage()), e);
            response.setSucc(false);
            response.setMsg(ExceptionUtils.getLocalMessage(e));
        }
        return response;
    }

    /**
     * 根据条件获可选角色列表,并分页显示
     */
    @Operation(summary = "获取可选角色列表,并分页显示", description = "根据指定条件，获取可选角色列表;不指定条件时获取全部角色列表,并分页显示")
    @Parameters(
            {
                    @Parameter(in = ParameterIn.QUERY, name = "pageNumber", schema = @Schema(type = "int"), description = "页码", example = "1"),
                    @Parameter(in = ParameterIn.QUERY, name = "pageSize", schema = @Schema(type = "int"), description = "每页显示条数", example = "10"),
                    @Parameter(in = ParameterIn.QUERY, name = "name", schema = @Schema(type = "string"), description = "角色名称"),
                    @Parameter(in = ParameterIn.QUERY, name = "userId", schema = @Schema(type = "long"), description = "用户id"),
                    @Parameter(in = ParameterIn.QUERY, name = "sortField", schema = @Schema(type = "string"), description = "排序字段")
            }
    )
    @GetMapping("/select-roles/pages")
    public ResponseWrapper<IPage<RoleVO>> getRoleListVoByPage(@RequestParam(defaultValue = "1") Long pageNumber,
                                                              @RequestParam(defaultValue = "10") Long pageSize,
                                                              @RequestParam(required = false) String name,
                                                              @RequestParam(required = false) Long userId,
                                                              @RequestParam(required = false) String sortField) {
        ResponseWrapper<IPage<RoleVO>> response = new ResponseWrapper<>();
        try {
            IPage<RoleVO> roleByPage = roleService.getRoleVoListPageByUserId(new Page<>(pageNumber, pageSize), name, userId, sortField);
            response.setContent(roleByPage);
            response.setSucc(true);
            response.setMsg("获取可选权限树成功");
        } catch (Exception e) {
            log.error(String.format("Get power list error: %s", e.getMessage()), e);
            response.setSucc(false);
            response.setMsg(ExceptionUtils.getLocalMessage(e));
        }
        return response;
    }

}

package com.starmol.portal.backend.controller.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starmol.portal.backend.aop.Permit;
import com.starmol.portal.backend.bean.vo.DepartmentPageVO;
import com.starmol.portal.backend.bean.vo.DepartmentTreeVO;
import com.starmol.portal.backend.common.Permission;
import com.starmol.portal.backend.common.ResponseWrapper;
import com.starmol.portal.backend.model.Department;
import com.starmol.portal.backend.model.base.DeleteDTO;
import com.starmol.portal.backend.service.user.DepartmentService;
import com.starmol.portal.backend.service.user.LogRecordService;
import com.starmol.portal.backend.service.user.UserService;
import com.starmol.portal.backend.utils.ExceptionUtils;
import com.starmol.portal.backend.utils.StringUtil;
import com.starmol.portal.backend.bean.vo.SimpleItemVO;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * 部门管理控制器(删除父对象时，将子对象关系字段置空或删除子对象)
 *
 * @author :Yuexiaopeng
 * @date :2019/12/10
 */
@Slf4j
@Tag(name = "部门管理")
@RestController
@RequestMapping(value = "/v1/departments")
public class DepartmentController {
    private final DepartmentService departmentService;
    private final LogRecordService logRecordService;
    private final UserService userService;

    public DepartmentController(DepartmentService departmentService, LogRecordService logRecordService, UserService userService) {
        this.departmentService = departmentService;
        this.logRecordService = logRecordService;
        this.userService = userService;
    }

    @Permit(value = Permission.ALL)
    @Operation(summary = "部门导入模板下载")
    @GetMapping(value = "/export")
    public void  downloadTemplate(HttpServletResponse response) {
        try {
            departmentService.downLoadTemplate(response);
        } catch (Exception e) {
            log.error(String.format("export template error:%s", e.getMessage()), e);
            logRecordService.insert("部门信息导入", "部门信息模板下载失败");
        }
    }

    @Operation(summary = "部门信息信息导入")
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseWrapper<Integer> importResource(@RequestParam MultipartFile file) {
        ResponseWrapper<Integer> wrapper = new ResponseWrapper<>();
        try {
            Integer importResult = departmentService.importDepartmentInfo(file);;
            wrapper.setContent(importResult).setSucc(true);
            wrapper.setMsg("导入部门成功");
            logRecordService.insert("部门信息信息导入", "部门信息信息导入成功");
        } catch (Exception e) {
            log.error(String.format("import infos error:%s", e.getMessage()), e);
            wrapper.setSucc(false);
            wrapper.setMsg(ExceptionUtils.getLocalMessage(e));
            logRecordService.insert("部门信息信息导入", "部门信息信息导入失败");
        }
        return wrapper;
    }


    /**
     * 创建部门(只创建部门, 不更新用户记录)
     */
    @Operation(summary = "创建部门", description = "创建部门")
    @PostMapping
    public ResponseWrapper<Department> createDepartment(@RequestBody Department department) {
        ResponseWrapper<Department> wrapper = new ResponseWrapper<>();
        try {
            Department returnDepartment = departmentService.saveAndReturnObjectWithFidCheck(department);
            wrapper.setContent(returnDepartment);
            wrapper.setSucc(true);
            wrapper.setMsg("创建部门成功");
            logRecordService.insert("部门管理", String.format("创建部门: %s 成功", StringUtil.getLogDescription(returnDepartment.getId(), returnDepartment.getName())));
        } catch (Exception e) {
            wrapper.setSucc(false);
            wrapper.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Create Department error:%s", e.getMessage()), e);
            logRecordService.insert("部门管理", String.format("创建部门: %s 失败", StringUtil.getLogDescription(department.getId(), department.getName())));
        }
        return wrapper;
    }


    /**
     * 根据删除对象列表安全删除部门，同时将子对象关联关系置空，全部删除成功表示当前操作成功，否则回滚操作并返回操作失败
     */
    @Operation(summary = "删除部门", description = "删除部门(安全删除)")
    @PatchMapping
    public ResponseWrapper<String> removeDepartmentSafely(@RequestBody List<DeleteDTO> removeObjects) {
        ResponseWrapper<String> wrapper = new ResponseWrapper<>();
        String formatIds = StringUtil.cutString(removeObjects.stream().map(StringUtil::getLogDescription).collect(Collectors.joining(",")), 200);
        try {
            departmentService.removeDepartmentsWithTransaction(removeObjects);
            wrapper.setContent(removeObjects.stream().map(m -> m.getId().toString()).collect(Collectors.joining(",")));
            wrapper.setSucc(true);
            wrapper.setMsg("删除部门成功");
            logRecordService.insert("部门管理", String.format("删除部门: %s 成功", formatIds));
        } catch (Exception e) {
            wrapper.setSucc(false);
            wrapper.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Remove Department error:%s", e.getMessage()), e);
            logRecordService.insert("部门管理", String.format("删除部门: %s 失败", formatIds));
        }
        return wrapper;
    }

    /**
     * 根据id修改部门
     */
    @Operation(summary = "修改部门", description = "根据id修改部门")
    @Parameters(
            {
                    @Parameter(in = ParameterIn.PATH, name = "id", schema = @Schema(type = "long"), description = "部门id")
            }
    )
    @PutMapping("/{id}")
    public ResponseWrapper<Department> updateDepartment(@PathVariable Long id, @RequestBody Department department) {
        ResponseWrapper<Department> wrapper = new ResponseWrapper<>();
        try {
            department.setId(id);
            Department returnDepartment = departmentService.updateByIdAndReturnObjectWithFidCheck(department);
            wrapper.setContent(returnDepartment);
            wrapper.setSucc(true);
            wrapper.setMsg("修改部门成功");
            logRecordService.insert("部门管理", String.format("修改部门: %s 成功", StringUtil.getLogDescription(returnDepartment.getId(), returnDepartment.getName())));
        } catch (Exception e) {
            wrapper.setSucc(false);
            wrapper.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Update department error:%s", e.getMessage()), e);
            logRecordService.insert("部门管理", String.format("修改部门: %s 失败", StringUtil.getLogDescription(department.getId(), department.getName())));
        }
        return wrapper;
    }

    /**
     * 根据类型获取部门列表
     */
    @Operation(summary = "获取简单部门列表", description = "根据指定类型，获取简单部门列表;不指定类型时获取全部部门列表")
    @Parameters(
            {
                    @Parameter(in = ParameterIn.QUERY, name = "type", schema = @Schema(type = "string"), description = "部门类型")
            }
    )
    @GetMapping("/simple-items")
    public ResponseWrapper<List<SimpleItemVO>> getDepartmentList(@RequestParam(required = false) String type) {
        ResponseWrapper<List<SimpleItemVO>> response = new ResponseWrapper<>();
        try {
            List<SimpleItemVO>  departmentList = departmentService.getSimpleItem(type);
            response.setContent(departmentList);
            response.setSucc(true);
            response.setMsg("获取部门列表成功");
        } catch (Exception e) {
            log.error(String.format("Get department list error: %s", e.getMessage()), e);
            response.setSucc(false);
            response.setMsg(ExceptionUtils.getLocalMessage(e));
        }
        return response;
    }


    /**
     * 根据类型获取部门列表
     */
    @Operation(summary = "获取简单部门列表", description = "根据指定类型，获取简单部门列表;不指定类型时获取全部部门列表")
    @Parameters(
            {
                    @Parameter(in = ParameterIn.QUERY, name = "departmentId", schema = @Schema(type = "long"), description = "部门id")
            }
    )
    @GetMapping("/child-simple-items")
    public ResponseWrapper<List<SimpleItemVO>> getChildDepartmentList(@RequestParam(required = false) Long departmentId) {
        ResponseWrapper<List<SimpleItemVO>> response = new ResponseWrapper<>();
        try {
            List<SimpleItemVO>  departmentList = departmentService.getDepartmentSimpleItemAndChild(departmentId);
            response.setContent(departmentList);
            response.setSucc(true);
            response.setMsg("获取部门列表成功");
        } catch (Exception e) {
            log.error(String.format("Get department list error: %s", e.getMessage()), e);
            response.setSucc(false);
            response.setMsg(ExceptionUtils.getLocalMessage(e));
        }
        return response;
    }

    /**
     * 分页查询部门
     */
    @Operation(summary = "分页查询部门", description = "分页查询部门")
    @Parameters(
            {
                    @Parameter(in = ParameterIn.QUERY, name = "pageNumber", schema = @Schema(type = "int"), description = "页码", example = "1"),
                    @Parameter(in = ParameterIn.QUERY, name = "pageSize", schema = @Schema(type = "int"), description = "每页显示条数", example = "10"),
                    @Parameter(in = ParameterIn.QUERY, name = "name", schema = @Schema(type = "string"), description = "部门名称"),
            }
    )
    @GetMapping("/pages")
    public ResponseWrapper<IPage<DepartmentPageVO>> getDepartmentListByPage(@RequestParam(defaultValue = "1") Long pageNumber,
                                                                            @RequestParam(defaultValue = "10") Long pageSize,
                                                                            @RequestParam(required = false) String name) {
        ResponseWrapper<IPage<DepartmentPageVO>> wrapper = new ResponseWrapper<>();
        try {
            IPage<DepartmentPageVO> departmentByPage = departmentService.getDepartmentPageWithType(new Page<>(pageNumber, pageSize), name);
            log.info("Get department list by page success");
            wrapper.setContent(departmentByPage);
            wrapper.setSucc(true);
            wrapper.setMsg("分页查询部门成功");
        } catch (Exception e) {
            wrapper.setSucc(false);
            wrapper.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Get department list by page error:%s", e.getMessage()), e);
        }
        return wrapper;
    }

    /**
     * 根据部门id获取部门详情
     */
    @Operation(summary = "查看部门详情", description = "根据部门id获取部门详情")
    @Parameters(
            {
                    @Parameter(in = ParameterIn.PATH, name = "id", schema = @Schema(type = "long"), description = "部门id")
            }
    )
    @GetMapping("/{id}")
    public ResponseWrapper<Department> getUserDepartmentDetail(@PathVariable Long id) {
        ResponseWrapper<Department> response = new ResponseWrapper<>();
        try {
            Department department = departmentService.getById(id);
            response.setContent(department);
            response.setSucc(true);
            response.setMsg("查看部门成功");
        } catch (Exception e) {
            log.error(String.format("Get department detail error: %s", e.getMessage()), e);
            response.setSucc(false);
            response.setMsg(ExceptionUtils.getLocalMessage(e));
        }
        return response;
    }

    /**
     * 根据名称获取部门树
     */
    @Operation(summary = "获取部门树", description = "获取树形结构部门列表")
    @Parameters(
            {
                    @Parameter(in = ParameterIn.QUERY, name = "name", schema = @Schema(type = "string"), description = "部门名称")
            }
    )
    @GetMapping("/tree")
    public ResponseWrapper<List<DepartmentTreeVO>> getDepartmentTree(@RequestParam(required = false) String name) {
        ResponseWrapper<List<DepartmentTreeVO>> response = new ResponseWrapper<>();
        try {
            List<DepartmentTreeVO> departmentTreeList = departmentService.createDepartmentTreeByDepartmentNameWithOrderAndCreateUserName(name);
            response.setContent(departmentTreeList);
            response.setSucc(true);
            response.setMsg("获取部门列表成功");
        } catch (Exception e) {
            log.error(String.format("Get department list error: %s", e.getMessage()), e);
            response.setSucc(false);
            response.setMsg(ExceptionUtils.getLocalMessage(e));
        }
        return response;
    }


    /**
     * 获取当前用户所在部门及子部门的树
     */
    @Operation(summary = "获取当前用户所在部门及子部门的树", description = "获取当前用户所在部门及子部门的树列表")
    @Parameters(
            {
                    @Parameter(in = ParameterIn.QUERY, name = "name", schema = @Schema(type = "string"), description = "部门名称")
            }
    )
    @GetMapping("/current-user-tree")
    public ResponseWrapper<List<DepartmentTreeVO>> getCurrentUseDepartmentTree(@RequestParam(required = false) String name) {
        ResponseWrapper<List<DepartmentTreeVO>> response = new ResponseWrapper<>();
        try {
            List<DepartmentTreeVO> departmentTreeList = departmentService.createOnlyCurrentDepartmentAndOfficeTreeWithCreateUserName(name);
            response.setContent(departmentTreeList);
            response.setSucc(true);
            response.setMsg("获取部门列表成功");
        } catch (Exception e) {
            log.error(String.format("Get department list error: %s", e.getMessage()), e);
            response.setSucc(false);
            response.setMsg(ExceptionUtils.getLocalMessage(e));
        }
        return response;
    }


}

package com.starmol.sourcecodereview.service.feign;

import com.starmol.sourcecodereview.aop.Permit;
import com.starmol.sourcecodereview.bean.dto.DepartmentDTO;
import com.starmol.sourcecodereview.bean.dto.UserDTO;
import com.starmol.sourcecodereview.bean.vo.LoginNameDepartmentVO;
import com.starmol.sourcecodereview.common.Permission;
import com.starmol.sourcecodereview.common.ResponseWrapper;
import com.starmol.sourcecodereview.constant.SysRoleTypeEnum;
import com.starmol.sourcecodereview.model.User;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;

@FeignClient(name = "userServiceClient", url = "${user.service.url}")
public interface UserServiceClient {

    @GetMapping("/portal/v1/internal/users")
    ResponseWrapper<UserDTO> getUserById(@RequestParam("id") Long id);

    @PostMapping("/portal/v1/internal/users/department")
    ResponseWrapper<List<User>> getUsersByDepartmentsIds(@RequestBody List<Long> departmentIds);

    @GetMapping("/portal/v1/internal/departments")
    ResponseWrapper<DepartmentDTO> getDepartmentById(@RequestParam("id") Long id);

    @PostMapping("/portal/v1/internal/departments")
    ResponseWrapper<List<DepartmentDTO>> getDepartmentsByIds(@RequestBody List<Long> departmentIds);

    @GetMapping("/portal/v1/internal/department-list")
    ResponseWrapper<List<DepartmentDTO>> getDepartmentsById(@RequestParam("id") Long id);

    @GetMapping("/portal/v1/internal/departments-and-children")
    ResponseWrapper<List<DepartmentDTO>> getDepartmentsByIdOrFId(@RequestParam("id") Long id);

    @PostMapping("/portal/v1/internal/login-name-departments")
    ResponseWrapper<List<LoginNameDepartmentVO>> getDepartmentsByLoginNames(@RequestBody List<String> loginNameList);
}

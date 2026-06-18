package com.starmol.circuitreview.backend.service.circuitreview.feign;

import com.starmol.circuitreview.backend.bean.dto.DepartmentDTO;
import com.starmol.circuitreview.backend.bean.dto.UserDTO;
import com.starmol.circuitreview.backend.common.ResponseWrapper;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "userServiceClient", url = "${user.service.url}")
public interface UserServiceClient {

    @GetMapping("/portal/v1/internal/users")
    ResponseWrapper<UserDTO> getUserById(@RequestParam("id") Long id);

    @PostMapping("/portal/v1/internal/users")
    ResponseWrapper<List<UserDTO>> getUsersByIds(@RequestBody List<Long> userIds);

    @PostMapping("/portal/v1/internal/users/department")
    ResponseWrapper<List<UserDTO>> getUsersByDepartmentIds(@RequestBody List<Long> departmentIds);

    @GetMapping("/portal/v1/internal/departments")
    ResponseWrapper<DepartmentDTO> getDepartmentById(@RequestParam("id") Long id);

    @PostMapping("/portal/v1/internal/departments")
    ResponseWrapper<List<DepartmentDTO>> getDepartmentsByIds(@RequestBody List<Long> departmentIds);

    @PostMapping("/portal/v1/internal/department-names")
    ResponseWrapper<List<DepartmentDTO>> getDepartmentsByNames(@RequestBody List<String> departmentNameList);

    @GetMapping("/portal/v1/internal/department-list")
    ResponseWrapper<List<DepartmentDTO>> getDepartmentsById(@RequestParam("id") Long id);

    @GetMapping("/portal/v1/internal/departments-and-children")
    ResponseWrapper<List<DepartmentDTO>> getDepartmentsByIdOrFId(@RequestParam("id") Long id);
}

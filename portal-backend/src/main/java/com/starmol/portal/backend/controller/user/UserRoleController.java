package com.starmol.portal.backend.controller.user;


import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import com.starmol.portal.backend.bean.dto.UserRoleChangeDTO;
import com.starmol.portal.backend.common.ResponseWrapper;
import com.starmol.portal.backend.service.user.LogRecordService;
import com.starmol.portal.backend.service.user.UserRoleService;
import com.starmol.portal.backend.utils.ExceptionUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户角色管理控制器
 *
 * @author :Yuexiaopeng
 * @date :2019/12/10
 */
@Slf4j
@Tag(name = "用户角色管理")
@RestController
@RequestMapping(value = "/v1/userroles")
public class UserRoleController {
    private final UserRoleService userRoleService;
    private final LogRecordService logRecordService;

    public UserRoleController(UserRoleService userRoleService, LogRecordService logRecordService) {
        this.userRoleService = userRoleService;
        this.logRecordService = logRecordService;
    }

    /**
     * 根据修改列表修改用户角色
     */
    @Operation(summary = "修改用户角色", description = "根据修改列表,修改用户角色")
    @PutMapping
    public ResponseWrapper<Void> updateUserRoles(@RequestBody List<UserRoleChangeDTO> userRoleChanges) {
        String logContent = userRoleService.getLogFromChangeList(userRoleChanges);
        ResponseWrapper<Void> wrapper = new ResponseWrapper<>();
        try {
            userRoleService.updateUserRoleByChangeList(userRoleChanges);
            wrapper.setSucc(true);
            wrapper.setMsg("修改用户角色成功");
            logRecordService.insert("用户角色管理", String.format("%s 成功", logContent));
        } catch (Exception e) {
            wrapper.setSucc(false);
            wrapper.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Update userRole error:%s", e.getMessage()), e);
            logRecordService.insert("用户角色管理", String.format("%s 失败", logContent));
        }
        return wrapper;
    }

}

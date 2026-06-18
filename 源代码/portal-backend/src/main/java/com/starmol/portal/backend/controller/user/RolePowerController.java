package com.starmol.portal.backend.controller.user;

import com.starmol.portal.backend.bean.dto.RolePowerChangeDTO;
import com.starmol.portal.backend.common.ResponseWrapper;
import com.starmol.portal.backend.service.user.LogRecordService;
import com.starmol.portal.backend.service.user.RolePowerService;
import com.starmol.portal.backend.utils.ExceptionUtils;

import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "角色权限管理")
@RestController
@RequestMapping(value = "/v1/role-powers")
public class RolePowerController {
    private final RolePowerService rolePowerService;
    private final LogRecordService logRecordService;

    public RolePowerController(RolePowerService rolePowerService, LogRecordService logRecordService) {
        this.rolePowerService = rolePowerService;
        this.logRecordService = logRecordService;
    }

    /**
     * 根据修改列表修改角色权限
     */
    @Operation(summary = "修改角色权限", description = "根据修改列表,修改角色权限")
    @PutMapping
    public ResponseWrapper<Void> updateRolePowers(@RequestBody List<RolePowerChangeDTO> rolePowerChanges) {
        String logContent = rolePowerService.getLogFromChangeList(rolePowerChanges);
        ResponseWrapper<Void> wrapper = new ResponseWrapper<>();
        try {
            rolePowerService.updateRolePowerByChangeList(rolePowerChanges);
            wrapper.setSucc(true);
            wrapper.setMsg("修改角色权限成功");
            logRecordService.insert("角色权限管理", String.format("%s 成功", logContent));
        } catch (Exception e) {
            wrapper.setSucc(false);
            wrapper.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Update rolePower error:%s", e.getMessage()), e);
            logRecordService.insert("角色权限管理", String.format("%s 失败", logContent));
        }
        return wrapper;
    }

}

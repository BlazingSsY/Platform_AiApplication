package com.starmol.portal.backend.controller.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starmol.portal.backend.bean.vo.PowerSelectionTreeVO;
import com.starmol.portal.backend.common.ResponseWrapper;
import com.starmol.portal.backend.model.Power;
import com.starmol.portal.backend.model.base.DeleteDTO;
import com.starmol.portal.backend.service.user.LogRecordService;
import com.starmol.portal.backend.service.user.PowerService;
import com.starmol.portal.backend.utils.ExceptionUtils;
import com.starmol.portal.backend.utils.StringUtil;

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

/**
 * @author Yuexiaopneg
 * @version 1.0
 * @date 2019/11/20 权限管理控制器
 */
@Slf4j
@Tag(name = "权限管理")
@RestController
@RequestMapping(value = "/v1/powers")
public class PowerController {
    private final PowerService powerService;
    private final LogRecordService logRecordService;

    public PowerController(PowerService powerService, LogRecordService logRecordService) {
        this.powerService = powerService;
        this.logRecordService = logRecordService;
    }

    /**
     * 创建权限
     */
    @Operation(summary = "创建权限", description = "创建权限")
    @PostMapping
    public ResponseWrapper<Power> createPower(@RequestBody Power power) {
        ResponseWrapper<Power> wrapper = new ResponseWrapper<>();
        try {
            Power returnPower = powerService.saveAndReturnObjectWithFidCheck(power);
            wrapper.setContent(returnPower);
            wrapper.setSucc(true);
            wrapper.setMsg("创建权限成功");
            logRecordService.insert("权限管理", String.format("创建权限: %s 成功", StringUtil.getLogDescription(returnPower.getId(), returnPower.getName())));
        } catch (Exception e) {
            wrapper.setSucc(false);
            wrapper.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Create Power error:%s", e.getMessage()), e);
            logRecordService.insert("权限管理", String.format("创建权限: %s 失败", StringUtil.getLogDescription(power.getId(), power.getName())));
        }
        return wrapper;
    }

    /**
     * 根据删除对象列表安全删除权限，全部删除成功表示当前操作成功，否则回滚操作并返回操作失败
     */
    @Operation(summary = "删除权限", description = "删除权限(安全删除)")
    @PatchMapping
    public ResponseWrapper<String> deletePowerSafely(@RequestBody List<DeleteDTO> deleteObjects) {
        ResponseWrapper<String> wrapper = new ResponseWrapper<>();
        String formatIds = StringUtil.cutString(deleteObjects.stream().map(StringUtil::getLogDescription).collect(Collectors.joining(",")), 200);
        try {
            powerService.removePowersWithTransaction(deleteObjects);
            wrapper.setContent(deleteObjects.stream().map(m -> m.getId().toString()).collect(Collectors.joining(",")));
            wrapper.setSucc(true);
            wrapper.setMsg("删除权限成功");
            logRecordService.insert("权限管理", String.format("删除权限: %s 成功", formatIds));
        } catch (Exception e) {
            wrapper.setSucc(false);
            wrapper.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Delete Power error:%s", e.getMessage()), e);
            logRecordService.insert("权限管理", String.format("删除权限: %s 失败", formatIds));
        }
        return wrapper;
    }

    /**
     * 根据id删除权限
     */
    @Operation(summary = "删除权限", description = "根据id删除权限(不安全删除)")
    @Parameters(
            {
                    @Parameter(in = ParameterIn.PATH, name = "id", schema = @Schema(type = "long"), description = "权限id")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseWrapper<Long> deletePower(@PathVariable Long id) {
        ResponseWrapper<Long> response = new ResponseWrapper<>();
        try {
            if (powerService.removeById(id)) {
                response.setContent(id);
                response.setMsg("删除权限成功");
                response.setSucc(true);
                logRecordService.insert("权限管理", String.format("删除ID为: %s 的权限成功", id));
            } else {
                response.setMsg("删除权限失败，请刷新后重新删除");
                response.setSucc(false);
                logRecordService.insert("权限管理", String.format("删除ID为: %s 的权限失败", id));
            }
        } catch (Exception e) {
            response.setSucc(false);
            response.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Delete power error: %s", e.getMessage()), e);
            logRecordService.insert("权限管理", String.format("删除ID为: %s 的权限失败", id));
        }
        return response;
    }

    /**
     * 根据id列表批量删除权限,删除成功一个即表示当前操作成功
     */
    @Operation(summary = "批量删除权限", description = "根据id列表批量删除权限(不安全删除)")
    @Parameters(
            {
                    @Parameter(in = ParameterIn.QUERY, name = "ids", schema = @Schema(type = "array"), description = "权限id数组")
            }
    )
    @DeleteMapping
    public ResponseWrapper<String> deletePowers(@RequestParam List<String> ids) {
        ResponseWrapper<String> response = new ResponseWrapper<>();
        String formatIds = StringUtil.cutString(ids.stream().map(String::valueOf).collect(Collectors.joining(",")), 200);
        try {
            if (powerService.removeByIds(ids)) {
                response.setContent(String.join(",", ids));
                response.setMsg("批量删除权限成功");
                response.setSucc(true);
                logRecordService.insert("权限管理", String.format("批量删除ID为: %s 的权限成功", formatIds));
            } else {
                response.setMsg("删除权限失败，请刷新后重新删除");
                response.setSucc(false);
                logRecordService.insert("权限管理", String.format("删除ID为: %s 的权限失败", formatIds));
            }
        } catch (Exception e) {
            response.setSucc(false);
            response.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Delete power error: %s", e.getMessage()), e);
            logRecordService.insert("权限管理", String.format("批量删除ID为: %s 的权限失败", formatIds));
        }
        return response;
    }


    /**
     * 根据id修改权限
     */
    @Operation(summary = "修改权限", description = "根据id修改权限")
    @Parameters(
            {
                    @Parameter(in = ParameterIn.PATH, name = "id", schema = @Schema(type = "long"), description = "权限id")
            }
    )
    @PutMapping("/{id}")
    public ResponseWrapper<Power> updatePower(@PathVariable Long id, @RequestBody Power power) {
        ResponseWrapper<Power> wrapper = new ResponseWrapper<>();
        try {
            power.setId(id);
            Power returnPower = powerService.updateByIdAndReturnObjectWithFidCheck(power);
            wrapper.setContent(returnPower);
            wrapper.setSucc(true);
            wrapper.setMsg("修改权限成功");
            logRecordService.insert("权限管理", String.format("修改权限: %s 成功", StringUtil.getLogDescription(returnPower.getId(), returnPower.getName())));
        } catch (Exception e) {
            wrapper.setSucc(false);
            wrapper.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Update power error:%s", e.getMessage()), e);
            logRecordService.insert("权限管理", String.format("修改权限: %s 失败", StringUtil.getLogDescription(power.getId(), power.getName())));
        }
        return wrapper;
    }

    /**
     * 根据名称获取权限列表
     */
    @Operation(summary = "获取权限列表", description = "根据名称，获取权限列表;不指定名称时获取全部权限列表")
    @Parameters(
            {
                    @Parameter(in = ParameterIn.QUERY, name = "name", schema = @Schema(type = "string"), description = "权限名称")
            }
    )
    @GetMapping
    public ResponseWrapper<List<Power>> getPowerList(@RequestParam(required = false) String name) {
        ResponseWrapper<List<Power>> response = new ResponseWrapper<>();
        try {
            List<Power> powerList;
            if (Objects.isNull(name) || name.isEmpty()) {
                powerList = powerService.list();
            } else {
                powerList = powerService.list(Wrappers.<Power>lambdaQuery().like(Power::getName, name));
            }
            response.setContent(powerList);
            response.setSucc(true);
            response.setMsg("获取权限列表成功");
        } catch (Exception e) {
            log.error(String.format("Get power list error: %s", e.getMessage()), e);
            response.setSucc(false);
            response.setMsg(ExceptionUtils.getLocalMessage(e));
        }
        return response;
    }

    /**
     * 分页查询权限
     */
    @Operation(summary = "分页查询权限", description = "分页查询权限")
    @Parameters(
            {
                    @Parameter(in = ParameterIn.QUERY, name = "pageNumber", schema = @Schema(type = "int"), description = "页码", example = "1"),
                    @Parameter(in = ParameterIn.QUERY, name = "pageSize", schema = @Schema(type = "int"), description = "每页显示条数", example = "10"),
                    @Parameter(in = ParameterIn.QUERY, name = "name", schema = @Schema(type = "string"), description = "权限名称")
            }
    )
    @GetMapping("/pages")
    public ResponseWrapper<IPage<Power>> getPowerListByPage(@RequestParam(defaultValue = "1") Long pageNumber,
                                                            @RequestParam(defaultValue = "10") Long pageSize,
                                                            @RequestParam(required = false) String name) {
        ResponseWrapper<IPage<Power>> wrapper = new ResponseWrapper<>();
        try {
            IPage<Power> powersByPage;
            if (Objects.isNull(name) || name.isEmpty()) {
                powersByPage = powerService.page(new Page<>(pageNumber, pageSize));
            } else {
                powersByPage = powerService.page(new Page<>(pageNumber, pageSize), Wrappers.<Power>lambdaQuery().like(Power::getName, name));
            }
            log.info("Get power list by page success");
            wrapper.setContent(powersByPage);
            wrapper.setSucc(true);
            wrapper.setMsg("分页查询权限成功");
        } catch (Exception e) {
            wrapper.setSucc(false);
            wrapper.setMsg(ExceptionUtils.getLocalMessage(e));
            log.error(String.format("Get power list by page error:%s", e.getMessage()), e);
        }
        return wrapper;
    }

    /**
     * 根据id获取权限详情
     */
    @Operation(summary = "查看权限详情", description = "根据权限id获取权限详情")
    @Parameters(
            {
                    @Parameter(in = ParameterIn.PATH, name = "id", schema = @Schema(type = "long"), description = "权限id")
            }
    )
    @GetMapping("/{id}")
    public ResponseWrapper<Power> getUserPowerDetail(@PathVariable Long id) {
        ResponseWrapper<Power> response = new ResponseWrapper<>();
        try {
            Power power = powerService.getById(id);
            response.setContent(power);
            response.setSucc(true);
            response.setMsg("查看权限成功");
        } catch (Exception e) {
            log.error(String.format("Get power detail error: %s", e.getMessage()), e);
            response.setSucc(false);
            response.setMsg(ExceptionUtils.getLocalMessage(e));
        }
        return response;
    }

    /**
     * 根据角色id获取权限列表
     */
    @Operation(summary = "获取可选权限树", description = "根据角色ID获取可选树形结构权限列表")
    @Parameters(
            {
                    @Parameter(in = ParameterIn.QUERY, name = "roleId", schema = @Schema(type = "long"), description = "角色id")
            }
    )
    @GetMapping("/select-tree")
    public ResponseWrapper<List<PowerSelectionTreeVO>> getPowerTree(@RequestParam(required = false) Long roleId) {
        ResponseWrapper<List<PowerSelectionTreeVO>> response = new ResponseWrapper<>();
        try {
            List<PowerSelectionTreeVO> powerTreeList = powerService.createSelectionTreeByRoleIdWithOrder(roleId);
            response.setContent(powerTreeList);
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

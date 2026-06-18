package com.starmol.portal.backend.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.portal.backend.model.base.IdBaseModel;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 实体类，用于访问数据库，属性和表的字段对应
 *
 * @author Yuexiaopeng
 * @date 2019/11/19
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "角色权限模型")
@TableName(value = "urm_rolepower")
public class RolePower extends IdBaseModel implements Serializable {
    private static final long serialVersionUID = 5760034272814953522L;

    @Schema(description = "角色ID")
    @TableField(value = "role_id")
    private Long roleId;

    @Schema(description = "权限ID")
    @TableField(value = "power_id")
    private Long powerId;

}

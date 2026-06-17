package com.starmol.portal.backend.model;

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
@Schema(description = "角色模型")
@TableName(value = "urm_role")
public class Role extends IdBaseModel implements Serializable {
    private static final long serialVersionUID = 5760034272814953522L;

    @Schema(description = "角色名称")
    private String name;

    @Schema(description = "角色类型")
    private String type;

    @Schema(description = "可编辑状态(1: 可编辑;0:不可编辑)")
    private Integer isEditable;

    @Schema(description = "备注")
    private String comments;
}

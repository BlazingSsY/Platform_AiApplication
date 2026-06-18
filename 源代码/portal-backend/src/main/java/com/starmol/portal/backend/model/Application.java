package com.starmol.portal.backend.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.portal.backend.model.base.IdBaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Application实体类，用于访问数据库，属性和表的字段对应
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "应用模型")
@TableName(value = "pot_application")
public class Application extends IdBaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "应用名称")
    private String name;

    @Schema(description = "应用英文名称")
    private String engName;

    @Schema(description = "所属板块")
    private String module;

    @Schema(description = "应用图标")
    private String icon;

    @Schema(description = "应用高亮图标")
    private String hoverIcon;

    @Schema(description = "应用图片")
    private String image;

    @Schema(description = "应用说明")
    private String description;

    @Schema(description = "序号")
    private Integer sequence;

    @Schema(description = "状态(0: 未上线;1: 已上线;2: 已下线)")
    private Integer status;

    @Schema(description = "应用入口地址")
    private String url;

    @Schema(description = "备注")
    private String comments;
}
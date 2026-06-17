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
 * Information实体类，用于访问数据库，属性和表的字段对应
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "资讯模型")
@TableName(value = "pot_information")
public class Information extends IdBaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "资讯标题")
    private String title;

    @Schema(description = "资讯摘要")
    private String summary;

    @Schema(description = "资讯类型")
    private Integer type;

    @Schema(description = "资讯图片")
    private String image;

    @Schema(description = "资讯内容")
    private String content;

    @Schema(description = "状态(0: 未发布;1: 已发布;2: 已下线)")
    private Integer status;

    @Schema(description = "备注")
    private String comments;
}
package com.starmol.portal.backend.bean.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "应用数据传输对象")
public class ApplicationDTO {

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
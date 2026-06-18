package com.starmol.portal.backend.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "应用视图对象")
public class ApplicationVO {

    @Schema(description = "应用ID")
    private Long id;

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

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createDate;

    @Schema(description = "最后更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateDate;
}
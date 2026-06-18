package com.starmol.portal.backend.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "资讯视图对象")
public class InformationVO {

    @Schema(description = "资讯ID")
    private Long id;

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

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createDate;

    @Schema(description = "最后更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateDate;
}
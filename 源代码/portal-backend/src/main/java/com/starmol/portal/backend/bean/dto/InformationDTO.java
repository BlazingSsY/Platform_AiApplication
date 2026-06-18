package com.starmol.portal.backend.bean.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "资讯数据传输对象")
public class InformationDTO {

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

    @Schema(description = "附件列表")
    private List<InfoAppendFileDTO> appendFileList;

    @Schema(description = "备注")
    private String comments;
}
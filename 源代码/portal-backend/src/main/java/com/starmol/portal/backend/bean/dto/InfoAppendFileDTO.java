package com.starmol.portal.backend.bean.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "资讯附件文件数据传输对象")
public class InfoAppendFileDTO {

    @Schema(description = "外键,关联到主记录表(pot_information)")
    private Long fId;

    @Schema(description = "文件在Minio上的ID")
    private String fileId;

    @Schema(description = "文件的原始名称")
    private String fileName;

    @Schema(description = "备注")
    private String comments;
}
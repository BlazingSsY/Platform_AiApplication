package com.starmol.portal.backend.bean.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "文件模型VO")
@Accessors(chain = true)
public class FileVO implements Serializable {
    private static final long serialVersionUID = 8495418862356311001L;

    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "文件在Minio上的ID")
    private String fileId;

    @Schema(description = "文件的原始名称")
    private String fileName;

    @Schema(description = "服务器上文件保存的路径")
    private String filePath;

    @Schema(description = "服务器上文件保存的名称")
    private String fileSaveName;
}
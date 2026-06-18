package com.starmol.circuitreview.backend.bean.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.circuitreview.backend.constant.FileSecretLevelEnum;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "文件模型DTO")
@Accessors(chain = true)
public class CircuitFileDTO implements Serializable {
    private static final long serialVersionUID = 8475418762356391102L;

    @Schema(description = "文件在Minio上的ID")
    private String fileId;

    @Schema(description = "文件的原始名称")
    private String fileName;

    @Schema(description = "密级")
    private FileSecretLevelEnum secretLevel;
}
package com.starmol.sourcecodereview.bean.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.sourcecodereview.constant.FileSecretLevelEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "代码文件模型DTO")
@Accessors(chain = true)
public class SourceCodeFileDTO implements Serializable {
    private static final long serialVersionUID = 8475418762356391103L;

    @Schema(description = "文件在Minio上的ID")
    private String fileId;

    @Schema(description = "文件的原始名称")
    private String fileName;

    @Schema(description = "密级")
    private FileSecretLevelEnum secretLevel;
} 
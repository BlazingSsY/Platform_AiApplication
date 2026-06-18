package com.starmol.circuitreview.backend.bean.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "附加模型DTO")
@Accessors(chain = true)
public class AppendFileDTO implements Serializable {
    private static final long serialVersionUID = 8475418762356391102L;

    @Schema(description = "主记录ID,计算使用")
    private Long fId;

    @Schema(description = "唯一id")
    private Long id;

    @Schema(description = "文件ID(文件上传后返回的ID)")
    private String fileId;

    @Schema(description = "文件名称")
    private String fileName;

}
package com.starmol.logicreview.bean.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "附件模型VO")
@Accessors(chain = true)
public class AppendFileVO implements Serializable {
    private static final long serialVersionUID = 8495418862356311001L;

    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "文件ID")
    private String fileId;

    @Schema(description = "文件名称")
    private String fileName;
}
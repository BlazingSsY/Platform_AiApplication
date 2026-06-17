package com.starmol.portal.backend.model.base;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "删除操作DTO")
public class DeleteDTO implements Serializable {
    @Schema(description = "唯一id")
    private Long id;

    @Schema(description = "名称(或其它标记)")
    private String label;

    @Schema(description = "版本号")
    private Integer version;
}
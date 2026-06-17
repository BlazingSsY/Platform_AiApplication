package com.starmol.logicreview.model.base;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Bean的基础类")
public class BaseBean implements Serializable {
    @Schema(description = "唯一id")
    private Long id;

    @Schema(description = "版本号")
    private Integer version;
}
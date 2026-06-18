package com.starmol.portal.backend.bean.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "状态变更数据传输对象")
public class StatusChangeDTO {

    @Schema(description = "对象Id")
    private Long id;

    @Schema(description = "新的状态值")
    private Integer status;
}
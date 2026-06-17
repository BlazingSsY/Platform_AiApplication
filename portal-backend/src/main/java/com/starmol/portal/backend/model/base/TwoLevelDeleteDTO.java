package com.starmol.portal.backend.model.base;

import java.io.Serializable;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "两级删除操作DTO")
public class TwoLevelDeleteDTO extends DeleteDTO implements Serializable {
    @Schema(description = "下级别对象列表")
    private List<DeleteDTO> childDeleteObjects;
}
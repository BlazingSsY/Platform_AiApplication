package com.starmol.logicreview.model.base;

import java.io.Serializable;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "删除操作类型DTO")
public class DeleteTypeDTO implements Serializable {
    @Schema(description = "子数据类型")
    private String childType;
    @Schema(description = "下级别对象列表")
    private List<MultiTypeLevelDeleteDTO> childDeleteObjects;
}
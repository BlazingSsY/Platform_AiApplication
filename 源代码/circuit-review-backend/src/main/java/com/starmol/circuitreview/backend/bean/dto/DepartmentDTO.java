package com.starmol.circuitreview.backend.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DepartmentDTO {
    @Schema(description = "唯一id")
    private Long id;

    @Schema(description = "部门名称")
    private String name;

    @Schema(description = "部门类型")
    private String type;

    @Schema(description = "是否可编辑")
    private Boolean  isEditable;


    @Schema(description = "同级别显示顺序")
    private Integer sequence;

    @Schema(description = "父节点ID")
    private Long  fId;

    @Schema(description = "部门描述")
    private String comments;

    @Schema(description = "是否已删除", hidden = true)
    private Integer isDelete;

    private Integer version;

    @Schema(description = "创建人")
    private Long createUser;

    @Schema(description = "更新人")
    private Long updateUser;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createDate;

    @Schema(description = "最后更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateDate;
}

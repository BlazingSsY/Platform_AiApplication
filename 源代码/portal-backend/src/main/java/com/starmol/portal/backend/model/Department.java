package com.starmol.portal.backend.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.portal.backend.model.base.IdBaseModel;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "部门模型")
@TableName(value = "urm_department")
@Accessors(chain = true)
public class Department extends IdBaseModel implements Serializable {
    private static final long serialVersionUID = 8495418862356391002L;

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

    @Schema(description = "是否科室")
    private Boolean  isOffice;
}

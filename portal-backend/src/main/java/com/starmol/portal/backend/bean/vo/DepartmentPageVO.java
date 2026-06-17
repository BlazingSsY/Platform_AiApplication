package com.starmol.portal.backend.bean.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "部门分页查询VO")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DepartmentPageVO implements Serializable {

    private static final long serialVersionUID = 5460085666807901556L;

    private Long id;

    @Schema(description = "部门名称")
    private String name;

    @Schema(description = "部门描述")
    private String comments;

    @Schema(description = "同级别显示顺序")
    private Integer sequence;

    @Schema(description = "创建人")
    private String createUserName;

    @Schema(description = "是否科室")
    private Boolean  isOffice;

}

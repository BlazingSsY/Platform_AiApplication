
package com.starmol.portal.backend.bean.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.portal.backend.model.Department;
import com.starmol.portal.backend.model.Role;
import com.starmol.portal.backend.model.User;
import com.starmol.portal.backend.model.base.IdBaseModel;

import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *用于返回包含部门和用户登陆名的部门列表
 * @author : Yuexiaopeng
 * @date : 2019/12/10
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "用户名部门VO")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginNameDepartmentVO extends IdBaseModel implements Serializable {
    private static final long serialVersionUID = 558480186846371889L;

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

    @Schema(description = "用户ID")
    private Long  userId;

    /**登录名，不可更改*/
    @Schema(description = "用户登录名")
    private String loginName;

}

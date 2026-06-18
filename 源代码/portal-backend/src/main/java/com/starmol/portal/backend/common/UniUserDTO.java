package com.starmol.portal.backend.common;

import java.io.Serializable;

import com.starmol.portal.backend.constant.SysRoleTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class UniUserDTO implements Serializable, UserMetaData {
    private static final long serialVersionUID = 558480186846371899L;

    /**用户id，不可更改*/
    @Schema(description =  "唯一id")
    private long id;
    @Schema(description =  "租户id")
    private long tenantId;
    @Schema(description =  "部门id")
    private long departmentId;
    @Schema(description =  "部门名称")
    private String departmentName;
    /**登录名，不可更改*/
    @Schema(description =  "用户登录名")
    private String loginName;
    /**用户名*/
    @Schema(description =  "用户名(昵称)")
    private String name;
    /** 用户类型 */
    @Schema(description =  "用户类型")
    private Integer type;

    private Long roleId;

    private SysRoleTypeEnum sysRoleType;

    /** 保存原始token */
    @Schema(description =  "token")
    private String token;

}
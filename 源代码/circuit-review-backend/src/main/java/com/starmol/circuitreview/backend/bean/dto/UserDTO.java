package com.starmol.circuitreview.backend.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDTO {
    @Schema(description = "唯一id")
    private Long id;

    @Schema(description = "部门id")
    private Long departmentId;

    @Schema(description = "部门名称")
    private String departmentName;

    /**登录名，不可更改*/
    @Schema(description = "用户登录名")
    private String loginName;
    /**密码*/
    @Schema(description = "密码")
    private String password;
    /**用户名*/
    @Schema(description = "用户名")
    private String name;
    /**电话*/
    @Schema(description = "电话")
    private String telephone;
    /**头像*/
    @Schema(description = "头像")
    private String profile;
    /**工号*/
    @Schema(description = "工号")
    private String workNo;
    /**
     * 用户是否被锁，被锁用户不能正常使用
     */
    @Schema(description = "是否被锁")
    private Boolean locked;
    /**
     * 下次登录是否需要修改密码
     */
    @Schema(description = "下次登录是否需要修改密码")
    private Boolean needChangePassword;

    @Schema(description = "是否可修改")
    private Boolean isEditable;
    /**
     * 描述
     */
    @Schema(description = "描述")
    private String comments;

    @Schema(description = "邮箱")
    private String email;

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

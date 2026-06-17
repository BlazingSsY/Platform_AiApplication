package com.starmol.logicreview.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.logicreview.constant.PersonSecretLevelEnum;
import com.starmol.logicreview.model.base.IdBaseModel;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 *  User实体类，用于访问数据库，属性和表的字段对应
 *
 * @author Yuexiaopeng
 * @date 2019/1/22
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "用户模型")
@TableName(value = "urm_user")
public class User extends IdBaseModel implements Serializable {
    private static final long serialVersionUID = 558480186846371899L;
    @Schema(description = "部门id")
    @TableField(value = "department_id")
    private Long departmentId;

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

    @Schema(description = "密级")
    private PersonSecretLevelEnum secretLevel;
    /**
     * 描述
     */
    @Schema(description = "描述")
    private String comments;

    @Schema(description = "邮箱")
    private String email;

}
package com.starmol.portal.backend.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户列表VO")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserListVO implements Serializable {
    private static final long serialVersionUID = 558480186846381899L;

    private Long id;

    @Schema(description = "部门id")
    private Long departmentId;

    /**
     * 登录名，不可更改
     */
    @Schema(description = "用户登录名")
    private String loginName;
    /**
     * 用户名
     */
    @Schema(description = "用户名")
    private String name;
    /**
     * 生日
     */
    @Schema(description = "生日")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date birthday;
    /**
     * 省
     */
    @Schema(description = "省")
    private String province;
    /**
     * 市
     */
    @Schema(description = "市")
    private String city;
    /**
     * 县
     */
    @Schema(description = "县")
    private String county;
    /**
     * 详细地址
     */
    @Schema(description = "详细地址")
    private String address;
    /**
     * 从事行业
     */
    @Schema(description = "从事行业")
    private String engage;
    /**
     * 工号
     */
    @Schema(description = "工号")
    private String workNo;
    /**
     * 邮箱
     */
    @Schema(description = "邮箱")
    private String email;
    /**
     * 性别
     */
    @Schema(description = "性别")
    private String sex;
    /**
     * 电话
     */
    @Schema(description = "电话")
    private String telephone;
    /**
     * 用户类型
     */
    @Schema(description = "用户类型")
    private Integer type;
    /**
     * 描述
     */
    @Schema(description = "描述")
    private String comments;
    /**
     * 用户是否被锁，被锁用户不能正常使用
     */
    @Schema(description = "是否被锁")
    private Boolean locked;
}

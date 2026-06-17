package com.starmol.portal.backend.bean.bo;


import com.alibaba.excel.annotation.ExcelProperty;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: Yuexiaopeng
 * @description 用户导入
 * @create: 2025-08-19
 **/

@Data
@Schema(description = "导入用户")
public class ImportUserBO implements Serializable {
    private static final long serialVersionUID = -225863381103524707L;

    @ExcelProperty("用户名称")
    private String name;

    @ExcelProperty("用户登录名")
    private String loginName;

    @ExcelProperty("所属部门")
    private String departmentName;

    @ExcelProperty("邮箱")
    private String email;

    @ExcelProperty("手机号")
    private String telephone;
}

package com.starmol.portal.backend.bean.vo;

import com.alibaba.excel.annotation.ExcelProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(description = "用户导入VO")
@EqualsAndHashCode
public class UserImportVO {

    @ExcelProperty(value = "一级")
    private String layer1;

    @ExcelProperty(value = "二级")
    private String layer2;

    @ExcelProperty(value = "三级")
    private String layer3;

    @ExcelProperty(value = "四级")
    private String layer4;

    @ExcelProperty(value = "五级")
    private String layer5;

    @ExcelProperty(value = "六级")
    private String layer6;

    @ExcelProperty(value = "姓名")
    private String name;

    @ExcelProperty(value = "工号")
    private String workNo;

    @ExcelProperty(value = "电话")
    private String telephone;

    @ExcelProperty(value = "角色")
    private String roleName;

}

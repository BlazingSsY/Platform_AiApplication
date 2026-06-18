package com.starmol.portal.backend.bean.bo;


import com.alibaba.excel.annotation.ExcelProperty;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: Yuexiaopeng
 * @description 部门导入
 * @create: 2025-08-19
 **/

@Data
@Schema(description = "导入部门")
public class ImportDepartmentBO implements Serializable {
    private static final long serialVersionUID = -225863381113524707L;

    @ExcelProperty("部门名称")
    private String name;
}

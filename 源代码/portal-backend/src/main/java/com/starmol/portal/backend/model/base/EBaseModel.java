package com.starmol.portal.backend.model.base;

import com.baomidou.mybatisplus.annotation.TableField;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import static com.baomidou.mybatisplus.annotation.FieldFill.INSERT;
import static com.baomidou.mybatisplus.annotation.FieldFill.INSERT_UPDATE;

@Data
public class EBaseModel extends BaseModel {
    @Schema(description = "创建用户名称")
    @TableField(fill = INSERT)
    private String createUserName;

    @Schema(description = "更新用户名称")
    @TableField(fill = INSERT_UPDATE)
    private String updateUserName;
}
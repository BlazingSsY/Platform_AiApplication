package com.starmol.portal.backend.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.portal.backend.model.base.IdBaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * InfoAppendFile实体类，用于访问数据库，属性和表的字段对应
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "资讯附件文件模型")
@TableName(value = "pot_info_append_file")
public class InfoAppendFile extends IdBaseModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "外键,关联到主记录表(pot_information)")
    @TableField(value = "f_id")
    private Long fId;

    @Schema(description = "文件在Minio上的ID")
    @TableField(value = "file_id")
    private String fileId;

    @Schema(description = "文件的原始名称")
    @TableField(value = "file_name")
    private String fileName;

    @Schema(description = "备注")
    private String comments;
}
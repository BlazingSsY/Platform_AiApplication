package com.starmol.circuitreview.backend.bean.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.circuitreview.backend.constant.FileSecretLevelEnum;
import com.starmol.circuitreview.backend.model.base.BaseBean;

import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import static com.baomidou.mybatisplus.annotation.FieldFill.INSERT;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "工具文件VO")
@Accessors(chain = true)
public class ToolFileVO extends BaseBean implements Serializable {

    private static final long serialVersionUID = 8475418762156391102L;

    @Schema(description = "文件在Minio上的ID")
    private String fileId;

    @Schema(description = "文件的原始名称")
    private String fileName;

    @Schema(description = "工具名称")
    private String toolName;

    @Schema(description = "备注")
    private String comments;

    @Schema(description = "上传时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createDate;
}
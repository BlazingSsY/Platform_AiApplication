package com.starmol.circuitreview.backend.bean.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.circuitreview.backend.constant.FileSecretLevelEnum;
import com.starmol.circuitreview.backend.model.base.BaseBean;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import static com.baomidou.mybatisplus.annotation.FieldFill.INSERT;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "电路图文件VO")
@Accessors(chain = true)
public class CircuitFileVersionVO extends BaseBean implements Serializable {

    @Serial
    private static final long serialVersionUID = 8475418762156391102L;

    @Schema(description = "所属文件ID,关联到文件表")
    private Long fileId;

    @Schema(description = "Minio上的ID")
    private String minioId;

    @Schema(description = "文件版本序号")
    private Integer fileVersion;

    @Schema(description = "文件版本名称")
    private String fileName;

    @Schema(description = "文件上传时的原始名称")
    private String fileOriginName;

    @Schema(description = "密级")
    private FileSecretLevelEnum secretLevel;

    @Schema(description = "机构id")
    private Long departmentId;

    @Schema(description = "机构名称")
    private String departmentName;

    @Schema(description = "隶属人编号")
    private Long ownerId;

    @Schema(description = "隶属人")
    private String ownerName;

    @Schema(description = "是否移入文件回收站(1:是; 0:否)")
    private Integer isRecycle;

    @Schema(description = "备注")
    private String comments;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createDate;
}

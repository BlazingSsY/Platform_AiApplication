package com.starmol.sourcecodereview.model.codereview;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.sourcecodereview.constant.FileSecretLevelEnum;
import com.starmol.sourcecodereview.model.base.IdBaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "dmsc_file_version", autoResultMap = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "代码文件版本模型")
@ToString(callSuper = true)
@Accessors(chain = true)
public class SourceCodeFileVersion extends IdBaseModel implements Serializable {

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

    @Schema(description = "隶属人编号")
    private Long ownerId;

    @Schema(description = "是否移入文件回收站(1:是; 0:否)")
    private Integer isRecycle;

    @Schema(description = "备注")
    private String comments;
}

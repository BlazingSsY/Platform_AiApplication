package com.starmol.circuitreview.backend.model.suggestion;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.circuitreview.backend.model.base.IdBaseModel;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "附件模型")
@TableName(value = "yjfk_append_file")
@Accessors(chain = true)
public class AppendFile extends IdBaseModel implements Serializable {
    private static final long serialVersionUID = 8495418862356311001L;

    @Schema(description = "主记录ID")
    private Long fId;

    @Schema(description = "附件类型(区分当前记录是主表那个属性的附件)")
    private String type;

    @Schema(description = "文件ID(文件在Minio上的ID)")
    private String fileId;

    @Schema(description = "文件名称")
    private String fileName;

    @Schema(description = "备注")
    private String comments;

}
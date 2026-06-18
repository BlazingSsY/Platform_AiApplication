package com.starmol.sourcecodereview.model.codereview;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.sourcecodereview.model.base.IdBaseModel;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "dmsc_tool_file", autoResultMap = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "工具文件模型")
@ToString(callSuper = true)
@Accessors(chain = true)
public class ToolFile extends IdBaseModel implements Serializable {
    
    @Schema(description = "文件在Minio上的ID")
    private String fileId;
    
    @Schema(description = "文件的原始名称")
    private String fileName;
    
    @Schema(description = "工具名称")
    private String toolName;

    @Schema(description = "备注")
    private String comments;
}

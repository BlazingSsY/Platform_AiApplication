package com.starmol.logicreview.model.codereview;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.logicreview.constant.FileSecretLevelEnum;
import com.starmol.logicreview.model.base.IdBaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "ljsc_file", autoResultMap = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "代码文件模型")
@ToString(callSuper = true)
@Accessors(chain = true)
public class SourceCodeFile extends IdBaseModel implements Serializable {
    
    @Schema(description = "文件在Minio上的ID")
    private String minioId;
    
    @Schema(description = "文件的原始名称")
    private String fileName;
    
    @Schema(description = "服务器上文件保存的路径")
    private String filePath;
    
    @Schema(description = "服务器上文件保存的名称")
    private String fileSaveName;

    @Schema(description = "配套机型")
    private String compatibleModels;

    @Schema(description = "产品型号")
    private String productModel;

    @Schema(description = "产品名称")
    private String productName;

    @Schema(description = "配置项名称")
    private String configName;

    @Schema(description = "代码文件版本")
    private String codeFileVersion;

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

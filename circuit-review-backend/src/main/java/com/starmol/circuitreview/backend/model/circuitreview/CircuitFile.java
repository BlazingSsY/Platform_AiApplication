package com.starmol.circuitreview.backend.model.circuitreview;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.circuitreview.backend.constant.FileSecretLevelEnum;
import com.starmol.circuitreview.backend.model.base.IdBaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "dlsc_file", autoResultMap = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "电路图文件模型")
@ToString(callSuper = true)
@Accessors(chain = true)
public class CircuitFile extends IdBaseModel implements Serializable {
    
    @Schema(description = "文件在Minio上的ID")
    private String fileId;
    
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

    @Schema(description = "电路原理图号")
    private String diagramNumber;

    @Schema(description = "版本")
    private String diagramVersion;

    @Schema(description = "密级")
    private FileSecretLevelEnum secretLevel;
    
    @Schema(description = "机构id")
    private Long departmentId;

    @Schema(description = "隶属人编号")
    private Long ownerId;

    @Schema(description = "问题闭环状态(0:未闭环;1:已闭环)")
    private Integer isClosedLoop;

    @Schema(description = "问题闭环文件版本id")
    private Long closedLoopFileVersionId;

    @Schema(description = "问题闭环文件版本序号")
    private Integer closedLoopFileVersion;

    @Schema(description = "问题闭环审查结果id")
    private Long closedLoopResultId;

    @Schema(description = "最新文件版本id")
    private Long latestFileVersionId;

    @Schema(description = "最新文件版本序号")
    private Integer latestFileVersion;

    @Schema(description = "是否移入文件回收站(1:是; 0:否)")
    private Integer isRecycle;

    @Schema(description = "最大审查点数")
    private Integer maxCheckPoints;
    
    @Schema(description = "备注")
    private String comments;
}

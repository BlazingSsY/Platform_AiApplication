package com.starmol.sourcecodereview.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.sourcecodereview.constant.ReviewStatusEnum;
import com.starmol.sourcecodereview.constant.FileSecretLevelEnum;
import com.starmol.sourcecodereview.model.base.BaseBean;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "代码文件详情VO")
@Accessors(chain = true)
public class SourceCodeFileDetailVO extends BaseBean implements Serializable {

    @Schema(description = "外键,关联到文件版本表")
    private Long fileVersionId;

    @Schema(description = "文件在Minio上的ID")
    private String minioId;

    @Schema(description = "文件的原始名称")
    private String fileName;

    @Schema(description = "文件版本名称")
    private String fileVersionName;

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

    @Schema(description = "机构名称")
    private String departmentName;

    @Schema(description = "隶属人编号")
    private Long ownerId;

    @Schema(description = "隶属人姓名")
    private String ownerName;

    @Schema(description = "是否移入文件回收站(1:是; 0:否)")
    private Integer isRecycle;

    @Schema(description = "备注")
    private String comments;

    @Schema(description = "文件审查结果id")
    private Long resultId;

    @Schema(description = "检查点数量")
    private Integer checkPoints;

    @Schema(description = "通过的检查点数量")
    private Integer passCheckPoints;

    @Schema(description = "通过率")
    private BigDecimal passRate;

    @Schema(description = "问题闭环状态(0:未闭环;1:已闭环)")
    private Integer isClosedLoop;

    @Schema(description = "审查状态")
    private ReviewStatusEnum status;

    @Schema(description = "错误信息")
    private String errorMessage;

    @Schema(description = "开始审查的时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime reviewTime;

    @Schema(description =  "文件数")
    private Integer filesSize;

    @Schema(description =  "文件行数")
    private Integer filesLine;

    @Schema(description =  "使用规则数")
    private Integer useRuleSize;

    @Schema(description =  "问题数量")
    private Integer questions;

} 
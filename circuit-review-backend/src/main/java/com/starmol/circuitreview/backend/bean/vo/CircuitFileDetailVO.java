package com.starmol.circuitreview.backend.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.circuitreview.backend.constant.FileSecretLevelEnum;
import com.starmol.circuitreview.backend.model.base.BaseBean;
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
@Schema(description = "电路图文件详情VO")
@Accessors(chain = true)
public class CircuitFileDetailVO extends BaseBean implements Serializable {

    @Schema(description = "外键,关联到文件表")
    private Long fileId;

    @Schema(description = "外键,关联到文件版本表")
    private Long fileVersionId;

    @Schema(description = "文件版本序号")
    private Integer fileVersion;

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

    @Schema(description = "电路原理图号")
    private String diagramNumber;

    @Schema(description = "版本")
    private String diagramVersion;

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

    @Schema(description = "当前版本审查点数")
    private Integer currentVersionCheckPoints;

    @Schema(description = "最大审查点数")
    private Integer checkPoints;

    @Schema(description = "通过的检查点数量")
    private Integer passCheckPoints;

    @Schema(description = "不通过的检查点数量")
    private Integer failCheckPoints;

    @Schema(description = "所有的问题点数")
    private Integer totalFailCheckPoints = 0;

    @Schema(description = "关闭的问题点数")
    private Integer closedFailCheckPoints = 0;

    @Schema(description = "通过率")
    private BigDecimal passRate;

    @Schema(description = "问题闭环状态(0:未闭环;1:已闭环)")
    private Integer isClosedLoop;

    @Schema(description = "超半月末关闭(0:否;1:是)")
    private Integer exceedHalfMonthNotClosed;

    @Schema(description = "开始审查的时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime reviewTime;

    @Schema(description = "文件创建的时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createDate;

    @Schema(description = "最后文件版本创建的时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime lastVersionCreateDate;

    @Schema(description = "默认排序时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime sortTime;
}

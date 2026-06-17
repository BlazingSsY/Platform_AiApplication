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
import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "电路图文件审核详情VO")
@Accessors(chain = true)
public class CircuitFileAuditVO extends BaseBean implements Serializable {
    @Schema(description = "外键,关联到文件表")
    private Long fileId;

    @Schema(description = "文件的原始名称")
    private String fileName;

    @Schema(description = "外键,关联到文件版本表")
    private Long fileVersionId;

    @Schema(description = "文件版本名称")
    private String fileVersionName;

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

    @Schema(description = "审查结果审核记录id")
    private Long resultAuditId;

    @Schema(description = "外键,关联到审查结果表")
    private Long resultId;

    @Schema(description = "审核提交时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime auditSubmitTime;

    @Schema(description = "问题审核状态(0:待审核;1:已审核)")
    private Integer isAuditFinished;

    @Schema(description = "管理员问题审核状态(0:待审核;1:已审核)")
    private Integer isAdminAuditFinished;

    @Schema(description = "专家问题审核状态(0:待审核;1:已审核)")
    private Integer isExpertAuditFinished;

    @Schema(description = "是否移入文件回收站(1:是; 0:否)")
    private Integer isRecycle;

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

    private List<CircuitReviewResultAuditVO> resultAuditVOList;
}

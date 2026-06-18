package com.starmol.circuitreview.backend.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "电路审查结果审核VO")
@JsonIgnoreProperties(ignoreUnknown = true)
@Accessors(chain = true)
public class CircuitReviewResultAuditVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "外键,关联到文件表")
    private Long fileId;

    @Schema(description = "文件的原始名称")
    private String fileName;

    @Schema(description = "外键,关联到文件版本表")
    private Long fileVersionId;

    @Schema(description = "文件版本名称")
    private String fileVersionName;

    @Schema(description = "机构id")
    private Long departmentId;

    @Schema(description = "机构名称")
    private String departmentName;

    @Schema(description = "隶属人编号")
    private Long ownerId;

    @Schema(description = "隶属人姓名")
    private String ownerName;

    @Schema(description = "外键,关联到审查结果表")
    private Long resultId;

    @Schema(description = "问题审核状态(0:待审核;1:已审核)")
    private Integer isAuditFinished;

    @Schema(description = "管理员问题审核状态(0:待审核;1:已审核)")
    private Integer isAdminAuditFinished;

    @Schema(description = "专家问题审核状态(0:待审核;1:已审核)")
    private Integer isExpertAuditFinished;

    @Schema(description = "开始审查的时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime reviewTime;

    @Schema(description = "开始审核的时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime auditTime;
}
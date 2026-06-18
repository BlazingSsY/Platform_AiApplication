package com.starmol.logicreview.bean.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.logicreview.model.base.BaseBean;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "代码文件审核详情VO")
@Accessors(chain = true)
public class SourceCodeFileAuditVO extends BaseBean implements Serializable {
    @Schema(description = "外键,关联到文件版本表")
    private Long fileVersionId;

    @Schema(description = "外键,关联到审查结果表")
    private Long reviewResultId;

    @Schema(description = "文件的原始名称")
    private String fileName;

    @Schema(description = "机构id")
    private Long departmentId;

    @Schema(description = "机构名称")
    private String departmentName;

    @Schema(description = "隶属人编号")
    private Long ownerId;

    @Schema(description = "隶属人姓名")
    private String ownerName;

    @Schema(description = "审核状态(1:未复核;2:复核中;3:已复核)")
    private Integer recheckStatus;

    @Schema(description = "审核提交时间")
    private String time;

}

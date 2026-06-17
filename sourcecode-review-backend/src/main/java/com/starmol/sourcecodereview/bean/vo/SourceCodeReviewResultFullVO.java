package com.starmol.sourcecodereview.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.sourcecodereview.constant.ReviewStatusEnum;
import com.starmol.sourcecodereview.model.base.BaseBean;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "代码审查结果完整VO")
@Accessors(chain = true)
public class SourceCodeReviewResultFullVO extends BaseBean implements Serializable {

    @Serial
    private static final long serialVersionUID = 8475418762156391112L;

    @Schema(description = "文件ID")
    private Long sourceCodeFileId;

    @Schema(description = "文件版本ID")
    private Long sourceCodeFileVersionId;

    @Schema(description = "审查状态")
    private ReviewStatusEnum status;

    @Schema(description = "审查人ID")
    private Long reviewerId;

    @Schema(description = "审查时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime reviewDate;

    @Schema(description = "审查意见")
    private String reviewComments;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createDate;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateDate;

    @Schema(description = "文件信息")
    private SourceCodeFileVO sourceCodeFile;

    @Schema(description = "文件版本信息")
    private SourceCodeFileVersionVO sourceCodeFileVersion;

    @Schema(description = "审查结果详情列表")
    private List<SourceCodeReviewResultDetailVO> reviewResultDetails;
} 
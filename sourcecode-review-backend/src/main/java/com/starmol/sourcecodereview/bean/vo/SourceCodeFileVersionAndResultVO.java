package com.starmol.sourcecodereview.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.sourcecodereview.constant.FileSecretLevelEnum;
import com.starmol.sourcecodereview.model.base.BaseBean;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "代码文件版本和结果VO")
@Accessors(chain = true)
public class SourceCodeFileVersionAndResultVO extends BaseBean implements Serializable {

    @Serial
    private static final long serialVersionUID = 8475418762156391109L;

    @Schema(description = "所属文件ID,关联到文件表")
    private String fileId;

    @Schema(description = "文件版本ID")
    private Long fileVersionId;

    @Schema(description = "文件版本序号")
    private Integer fileVersion;

    @Schema(description = "文件版本名称")
    private String fileName;

    @Schema(description = "文件名称")
    private String name;

    @Schema(description = "密级")
    private FileSecretLevelEnum secretLevel;

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

    @Schema(description = "开始审查的时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime reviewTime;
} 
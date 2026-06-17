package com.starmol.sourcecodereview.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.sourcecodereview.constant.ReviewStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "代码审查主页统计数据项VO")
@Accessors(chain = true)
public class SourceCodeReviewHomeStatisticsDataItemVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8475418762156391119L;
    @Schema(description = "文件的原始名称")
    private String fileName;

    @Schema(description = "部门ID")
    private Long departmentId;

    @Schema(description = "部门名称")
    private String departmentName;

    @Schema(description = "隶属人Id")
    private Long ownerId;

    @Schema(description = "隶属人名称")
    private String ownerName;

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

    @Schema(description = "状态(0:未审查;1:正在审查;2:审查完成)")
    private ReviewStatusEnum status;

    @Schema(description = "是否移入文件回收站(1:是; 0:否)")
    private Integer isRecycle;
} 
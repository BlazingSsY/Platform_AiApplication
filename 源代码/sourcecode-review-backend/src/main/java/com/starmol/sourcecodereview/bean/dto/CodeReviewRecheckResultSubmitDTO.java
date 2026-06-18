package com.starmol.sourcecodereview.bean.dto;


import java.io.Serializable;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author: Yuexiaopeng
 * @create: 2025-08-19
 **/

@Data
@Schema(description = "代码审查结果复审评审提交DTO")
public class CodeReviewRecheckResultSubmitDTO implements Serializable {
    private static final long serialVersionUID = 221813112113524707L;

    @Schema(description = "文件版本Id")
    private Long fileVersionId;

    @Schema(description = "问题Ids")
    private List<String> questionIds;

    @Schema(description = "版本")
    private String version;

    @Schema(description = "拒绝理由")
    private String rejectReason;

    @Schema(description = "审查结论")
    private Integer recheckResultStatus;
}

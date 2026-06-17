package com.starmol.logicreview.bean.bo;


import java.io.Serializable;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author: Yuexiaopeng
 * @description 存放调用第三方服务的结果
 * @create: 2025-08-19
 **/

@Data
@Schema(description = "存放调用第三方服务请求数据")
public class CodeReviewRecheckResultSubmitBO implements Serializable {
    private static final long serialVersionUID = 221863112113524707L;

    @Schema(description = "审查Id")
    private String reviewId;

    @Schema(description = "问题Ids")
    private List<String> questionIds;

    @Schema(description = "版本")
    private String version;

    @Schema(description = "拒绝理由")
    private String rejectReason;

    @Schema(description = "审查结论")
    private Integer recheckResultStatus;
}

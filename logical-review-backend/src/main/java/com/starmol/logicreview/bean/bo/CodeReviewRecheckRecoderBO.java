package com.starmol.logicreview.bean.bo;


import java.io.Serializable;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: Yuexiaopeng
 * @description 存放调用第三方服务的结果
 * @create: 2025-08-19
 **/

@Data
@Schema(description = "存放调用第三方服务请求数据")
public class CodeReviewRecheckRecoderBO implements Serializable {
    private static final long serialVersionUID = 221863112103524707L;

    @Schema(description = "reviewID")
    private String reviewId;

    @Schema(description = "状态")
    private Integer recheckStatus;

    @Schema(description = "用户")
    private String submitUserId;

    @Schema(description = "时间")
    private String time;

}

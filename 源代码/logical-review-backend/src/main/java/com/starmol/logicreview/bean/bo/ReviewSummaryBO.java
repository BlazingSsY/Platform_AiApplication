package com.starmol.logicreview.bean.bo;


import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author: Yuexiaopeng
 * @description 存放调用第三方服务的结果
 * @create: 2025-08-19
 **/

@Data
@Schema(description = "获取汇总结果请求")
@Accessors(chain = true)
public class ReviewSummaryBO implements Serializable {
    private static final long serialVersionUID = 221163311103514707L;

    @Schema(description =  "审查ID")
    private String reviewId;

}

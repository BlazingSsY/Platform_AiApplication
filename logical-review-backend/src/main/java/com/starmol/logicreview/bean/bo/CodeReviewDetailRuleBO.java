package com.starmol.logicreview.bean.bo;


import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: Yuexiaopeng
 * @description 存放调用第三方服务的结果
 * @create: 2025-08-19
 **/

@Data
@Schema(description = "存放调用第三方服务(获取所有规则)的结果")
public class CodeReviewDetailRuleBO implements Serializable {
    private static final long serialVersionUID = 221853112103524707L;

    @Schema(description = "规则id")
    private String id;

    @Schema(description = "规则解释")
    private String explain;

    @Schema(description = "遂循准则的示例内容")
    private String correctExample;

    @Schema(description = "违反准则的示例内容")
    private String errorExample;
}

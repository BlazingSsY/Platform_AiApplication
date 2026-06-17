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
@Schema(description = "存放调用第三方服务(获取所有规则)的结果")
public class CodeReviewDetailRuleListBO implements Serializable {
    private static final long serialVersionUID = 221763312103524707L;

    @Schema(description =  "规则集")
    private List<CodeReviewDetailRuleBO> rules;
}

package com.starmol.sourcecodereview.bean.bo;


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
public class CodeReviewRuleBO implements Serializable {
    private static final long serialVersionUID = 221863112103524707L;

    @Schema(description = "规则id")
    private String id;

    @Schema(description = "规则描述")
    private String desc;

    @Schema(description = "语言(JAVA/C_PLUS/C)")
    private List<String> language;

    @Schema(description = "选中状态(0:未选中;1:选中)")
    private Integer selectStatus;

    @Schema(description = "规则类型")
    private String ruleType;

    @Schema(description = "规则来源")
    private String ruleSource;

    @Schema(description = "机理说明")
    private String explain;

    @Schema(description = "默认必选(1:必选;0:非必选)")
    private Integer mustSelect;
}

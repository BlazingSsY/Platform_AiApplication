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
@Schema(description = "存放调用第三方服务的结果(文件结果)")
public class CodeReCheckFileResultBO implements Serializable {
    private static final long serialVersionUID = -221863381103524707L;

    @Schema(description = "文件名")
    private String fileName;

    @Schema(description =  "规则id")
    private String ruleId;

    @Schema(description =  "规则描述")
    private String rule;

    @Schema(description =  "规则来源")
    private String ruleSource;

    @Schema(description =  "状态(1:表示没有违反规则的;2:表示有违反规则的;3:表示正在审校中)")
    private Integer status;

    @Schema(description =  "代码语言(JAVA/C_PLUS/C)")
    private String  language;

    @Schema(description =  "机理说明")
    private String  explain;

    @Schema(description =  "代码列表")
    private List<CodeRecheckCodeResultBO> codes;
}

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
@Schema(description = "存放调用第三方服务的结果(汇总结果)")
public class CodeReviewResultBO implements Serializable {
    private static final long serialVersionUID = -221863311103524707L;

    @Schema(description =  "状态(1:表示审校中正在进行中;2:表示审校中完成)")
    private Integer status;

    @Schema(description =  "状态(1:表示审校中正在进行中;2:表示审校中完成)")
    private Integer duration;

    @Schema(description =  "文件数")
    private Integer filesSize;

    @Schema(description =  "通过文件数")
    private Integer passFileNum;

    @Schema(description =  "文件行数")
    private Integer filesLine;

    @Schema(description =  "使用规则数")
    private Integer useRuleSize;

    @Schema(description =  "问题数量")
    private Integer questions;

    @Schema(description =  "是否通过")
    private Integer reviewStatus;

    @Schema(description =  "文件列表")
    private List<CodeReviewFileResultBO> filesResult;
}

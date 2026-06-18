package com.starmol.sourcecodereview.bean.vo;


import com.starmol.sourcecodereview.bean.bo.CodeReviewFileResultBO;

import java.io.Serializable;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: Yuexiaopeng
 * @description 存放根据版本获取的审查结果
 * @create: 2025-08-19
 **/

@Data
@Schema(description = "存放根据版本获取的审查结果")
public class CodeReviewResultVO implements Serializable {
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

    @Schema(description =  "结果详情列表")
    private List<SourceCodeReviewResultDetailVO> detailVOList;
}

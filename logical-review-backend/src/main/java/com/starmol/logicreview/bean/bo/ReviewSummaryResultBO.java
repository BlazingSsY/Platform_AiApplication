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
@Schema(description = "获取汇总结果")
@Accessors(chain = true)
public class ReviewSummaryResultBO implements Serializable {
    private static final long serialVersionUID = 221163311103514707L;

    @Schema(description =  "文件数")
    private Integer filesSize;

    @Schema(description =  "文件行数")
    private Integer filesLine;

    @Schema(description =  "使用规则数")
    private Integer useRuleSize;

    @Schema(description =  "问题数量")
    private Integer questions;

}

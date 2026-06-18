package com.starmol.circuitreview.backend.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class RegenerateCircuitReviewResultRequestVO {
    @Schema(description = "重跑结果参数。0: 只重跑包含标记为废弃规则的结果；1: 重跑全部结果")
    private Integer targetParam;

    @Schema(description = "重跑单位名称列表。只重跑指定单位的结果")
    private List<String> departments;


}

package com.starmol.logicreview.bean.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class RuleSelectVO {
    @Schema(description = "规则数")
    private Integer total;

    @Schema(description = "选中规则数")
    private Integer selectSize;





}


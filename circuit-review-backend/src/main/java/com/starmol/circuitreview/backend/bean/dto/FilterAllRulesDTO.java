package com.starmol.circuitreview.backend.bean.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "获取所有Rule查询条件DTO")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FilterAllRulesDTO implements Serializable {
    private static final long serialVersionUID = 5760044272814953521L;

    @Schema(description = "当前页数")
    private Long pageNum;

    @Schema(description = "每页记录数")
    private Long pageSize;

    @Schema(description = "条件")
    private RuleFilterDTO filter;

}
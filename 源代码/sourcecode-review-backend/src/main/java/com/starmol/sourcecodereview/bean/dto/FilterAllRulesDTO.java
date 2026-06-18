package com.starmol.sourcecodereview.bean.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Schema(description = "获取所有Rule查询条件DTO")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Accessors(chain = true)
public class FilterAllRulesDTO implements Serializable {
    private static final long serialVersionUID = 5760044272814953521L;

    @Schema(description = "条件")
    private RuleFilterDTO filter;

}
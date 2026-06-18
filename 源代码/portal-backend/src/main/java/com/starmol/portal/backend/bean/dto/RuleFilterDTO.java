package com.starmol.portal.backend.bean.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "获取所有Rule查询条件DTO")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RuleFilterDTO implements Serializable {
    private static final long serialVersionUID = 5760144272814953521L;

    @Schema(description = "语言类型")
    private List<String> language;

    @Schema(description = "选中状态")
    private List<String> selectStatus;

    @Schema(description = "规则类型筛")
    private List<String> ruleType;

    @Schema(description = "文本搜索内容")
    private String desc;
}
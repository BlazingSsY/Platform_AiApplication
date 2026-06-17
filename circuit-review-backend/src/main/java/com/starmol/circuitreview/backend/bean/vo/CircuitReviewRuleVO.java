package com.starmol.circuitreview.backend.bean.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.circuitreview.backend.constant.RuleTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "电路审查规则VO")
@Accessors(chain = true)
public class CircuitReviewRuleVO implements Serializable {
    
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "规则类型")
    private RuleTypeEnum type;

    @Schema(description = "规则类型文字")
    private String typeStr;

    @Schema(description = "规则编号")
    private String code;

    @Schema(description = "规则名称")
    private String name;

    @Schema(description = "备注")
    private String comments;
}
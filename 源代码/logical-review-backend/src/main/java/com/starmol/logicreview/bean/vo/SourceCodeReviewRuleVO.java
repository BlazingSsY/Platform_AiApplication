package com.starmol.logicreview.bean.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.logicreview.constant.RuleTypeEnum;
import com.starmol.logicreview.model.base.BaseBean;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "代码审查规则VO")
@Accessors(chain = true)
public class SourceCodeReviewRuleVO extends BaseBean implements Serializable {

    @Serial
    private static final long serialVersionUID = 8475418762156391107L;


    @Schema(description = "规则类型")
    private RuleTypeEnum type;

    @Schema(description = "规则名称")
    private String name;

    @Schema(description = "规则编号")
    private String code;

    @Schema(description = "备注")
    private String comments;
} 
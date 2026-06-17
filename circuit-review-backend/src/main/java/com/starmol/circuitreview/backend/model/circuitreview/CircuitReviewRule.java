package com.starmol.circuitreview.backend.model.circuitreview;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.circuitreview.backend.constant.RuleTypeEnum;
import com.starmol.circuitreview.backend.model.base.IdBaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "dlsc_rule", autoResultMap = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "电路审查规则模型")
@ToString(callSuper = true)
@Accessors(chain = true)
public class CircuitReviewRule extends IdBaseModel implements Serializable {

    @Schema(description = "规则编号")
    private String code;

    @Schema(description = "规则名称")
    private String name;

    @Schema(description = "规则类型")
    private RuleTypeEnum type;

    /**
     * 是否废弃状态 1: 已废弃  0: 正常
     */
    @Schema(description = "是否废弃")
    private Integer isDeprecate;
    
    @Schema(description = "备注")
    private String comments;
}

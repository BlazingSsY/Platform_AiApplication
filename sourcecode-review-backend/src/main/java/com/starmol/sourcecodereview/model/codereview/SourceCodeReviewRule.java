package com.starmol.sourcecodereview.model.codereview;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.sourcecodereview.model.base.IdBaseModel;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "dmsc_rule", autoResultMap = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "代码审查规则模型")
@ToString(callSuper = true)
@Accessors(chain = true)
public class SourceCodeReviewRule extends IdBaseModel implements Serializable {
    
    @Schema(description = "规则名称")
    private String name;

    @Schema(description = "规则编号")
    private String code;

    @Schema(description = "规则类型")
    private String type;
    
    @Schema(description = "备注")
    private String comments;

    @Schema(description = "机理说明")
    private String explain;
}

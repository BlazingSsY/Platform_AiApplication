package com.starmol.circuitreview.backend.model.circuitreview;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.circuitreview.backend.model.base.IdBaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "dlsc_naming_convention", autoResultMap = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "设计命名规范模型")
@ToString(callSuper = true)
@Accessors(chain = true)
public class NamingConvention extends IdBaseModel implements Serializable {
    @Schema(description = "标题")
    private String title;

    @Schema(description = "内容")
    private String content;
}

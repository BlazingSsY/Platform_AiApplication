package com.starmol.circuitreview.backend.bean.vo;

import com.starmol.circuitreview.backend.model.base.BaseBean;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "命名规范视图对象")
public class NamingConventionVO extends BaseBean implements Serializable {
    
    @Schema(description = "标题")
    private String title;

    @Schema(description = "内容")
    private String content;
}
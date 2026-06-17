package com.starmol.logicreview.bean.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serial;
import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "源代码内容VO")
@Accessors(chain = true)
public class SourceCodeContentVO  implements Serializable {
    @Serial
    private static final long serialVersionUID = 8475418762156391103L;

    @Schema(description = "代码内容")
    private String sourceCode;

    @Schema(description = "all:表示完整代码;part:表示局部代码")
    private String dataType;

    @Schema(description =  "偏移量")
    private String offset;

    @Schema(description = "整个文件代码总行数")
    private Integer lines;
} 
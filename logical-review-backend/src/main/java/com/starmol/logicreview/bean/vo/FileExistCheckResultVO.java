package com.starmol.logicreview.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FileExistCheckResultVO {
    private Boolean exist;
    private Long fileId;
    @Schema(description = "是否已粉碎,但存在审查结果表")
    private Boolean pulverized = false;
}

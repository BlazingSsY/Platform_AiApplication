package com.starmol.logicreview.bean.dto;


import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author: Yuexiaopeng
 * @description 存放调用第三方服务的结果
 * @create: 2025-08-19
 **/

@Data
@Schema(description = "获取文件源代码请求")
@Accessors(chain = true)
public class SourceCodeDTO implements Serializable {
    private static final long serialVersionUID = 221163312103514707L;

    @Schema(description =  "文件名")
    private String fileName;

    @Schema(description =  "审查结果ID")
    private Long resultId;


}

package com.starmol.logicreview.bean.bo;


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
public class SourceCodeBO implements Serializable {
    private static final long serialVersionUID = 221163312103514707L;

    @Schema(description =  "文件名")
    private String fileName;

    @Schema(description =  "偏移量")
    private String offset;

    @Schema(description =  "审查ID")
    private String reviewId;


}

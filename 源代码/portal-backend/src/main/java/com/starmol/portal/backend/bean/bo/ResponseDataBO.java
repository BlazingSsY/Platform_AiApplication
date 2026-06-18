package com.starmol.portal.backend.bean.bo;


import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: Yuexiaopeng
 * @description 存放调用第三方服务的结果
 * @create: 2025-08-19
 **/

@Data
@Schema(description = "存放调用第三方服务的结果")
public class ResponseDataBO<T> implements Serializable {
    private static final long serialVersionUID = -225863381103524707L;

    @Schema(description =  "响应状态码")
    private Integer code;

    @Schema(description = "错误提示信息")
    private String message;

    @Schema(description =  "X-Trace-Id")
    private String debugId;

    @Schema(description =  "时间戳")
    Long timestamp;

    @Schema(description =  "具体响应数据")
    private T data;
}

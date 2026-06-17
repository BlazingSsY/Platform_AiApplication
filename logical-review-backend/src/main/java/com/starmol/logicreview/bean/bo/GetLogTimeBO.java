package com.starmol.logicreview.bean.bo;


import java.io.Serializable;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author: Yuexiaopeng
 * @description 存放调用第三方服务的结果
 * @create: 2025-08-19
 **/

@Data
@Schema(description = "获取日志的请求")
@Accessors(chain = true)
public class GetLogTimeBO implements Serializable {
    private static final long serialVersionUID = 221163311103514707L;

    @Schema(description =  "开始时间(格式:'yyyyMMdd hh24:mm:ss')")
    private String begin;

    @Schema(description =  "结束时间(格式:'yyyyMMdd hh24:mm:ss')")
    private String end;
}

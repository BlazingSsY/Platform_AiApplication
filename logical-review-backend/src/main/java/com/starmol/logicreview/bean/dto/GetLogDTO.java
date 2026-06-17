package com.starmol.logicreview.bean.dto;


import com.starmol.logicreview.bean.bo.GetLogTimeBO;

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
public class GetLogDTO implements Serializable {
    private static final long serialVersionUID = 221163311103514707L;

    @Schema(description =  "搜索关键字")
    private List<String> keyWord;

    @Schema(description =  "起始时间和结束时间")
    private GetLogTimeBO time;

    @Schema(description =  "显示行数(默认:1000)")
    private Integer num;
}

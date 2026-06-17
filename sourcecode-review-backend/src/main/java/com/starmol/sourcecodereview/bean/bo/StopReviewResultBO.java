package com.starmol.sourcecodereview.bean.bo;


import java.io.Serializable;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: Yuexiaopeng
 * @description 存放调用第三方服务的结果
 * @create: 2025-08-19
 **/

@Data
@Schema(description = "停止审查的结果")
public class StopReviewResultBO implements Serializable {
    private static final long serialVersionUID = 221163311103524707L;

    @Schema(description =  "状态(1.停止成功 0.停止失败)")
    private Integer stopStatus;

}

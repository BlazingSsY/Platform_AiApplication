package com.starmol.sourcecodereview.bean.bo;


import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: Yuexiaopeng
 * @description 存放调用第三方服务的结果
 * @create: 2025-08-19
 **/

@Data
@Schema(description = "存放调用第三方服务的结果(汇总结果)")
public class PulverizeFileResultBO implements Serializable {
    private static final long serialVersionUID = -221263312103524707L;

    @Schema(description =  "1:可粉碎;2:不可粉碎")
    private String delete;

    @Schema(description =  "delete=1时填充粉碎成功;delete=2时填充不可粉碎原闪")
    private String desc;
}

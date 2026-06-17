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
@Schema(description = "存放调用第三方服务的结果(汇总结果)")
public class CodeRecheckResultBO implements Serializable {
    private static final long serialVersionUID = -221263311103524707L;

    @Schema(description =  "版本号")
    private String version;

    @Schema(description =  "审查时间")
    private String recheckTime;

    @Schema(description =  "文件列表")
    private List<CodeReCheckFileResultBO> filesResult;
}

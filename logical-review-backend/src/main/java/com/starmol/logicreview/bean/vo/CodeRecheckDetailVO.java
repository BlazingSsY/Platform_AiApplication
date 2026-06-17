package com.starmol.logicreview.bean.vo;


import java.io.Serializable;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: Yuexiaopeng
 * @description 存放根据版本获取的审查结果
 * @create: 2025-08-19
 **/

@Data
@Schema(description = "存放复核详情")
public class CodeRecheckDetailVO implements Serializable {
    private static final long serialVersionUID = -221863311103124707L;

    @Schema(description =  "版本号")
    private String version;

    @Schema(description =  "审查时间")
    private String recheckTime;

    @Schema(description =  "结果详情列表")
    private List<CodeRecheckDetailCodesVO> detailVOList;
}

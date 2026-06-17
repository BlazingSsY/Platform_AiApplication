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
@Schema(description = "存放调用第三方服务的结果(代码结果)")
public class CodeRecheckCodeResultBO implements Serializable {
    private static final long serialVersionUID = -221163381103524707L;

    @Schema(description = "问题Id")
    private String questionId;

    @Schema(description = "报错代码")
    private String code;

    @Schema(description =  "行号:多行是5-10这样展示（表示5到10行），单行只有一个数字")
    private String lineNumber;

    @Schema(description =  "错误原因")
    private String errorReason;

    @Schema(description =  "修改建议")
    private String modifySuggest;

    @Schema(description = "审查结论")
    private String recheckConclusion;

    @Schema(description = "问题描述")
    private String questionDesc;

    @Schema(description = "复核结果状态(1:通过;2:未通过)")
    private Integer recheckResultStatus;

    @Schema(description = "拒绝理由")
    private String rejectReason;

    @Schema(description = "复核处理用户ID")
    private String recheckUserId;
}

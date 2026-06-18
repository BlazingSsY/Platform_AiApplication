/*
 * STARMOL Confidential
 * OCO Source Materials
 * STARMOL
 * © Copyright STARMOL Advanced Technology Ltd. 2022-2023
 * The source code for this program is not published or otherwise divested of its trade secrets, irrespective of STARMOL has been deposited with the P.R.China Copyright Office
 */

package com.starmol.sourcecodereview.bean.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.sourcecodereview.constant.SuggestionStatusEnum;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "修改反馈意见状态的DTO")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChangeSuggestionStatusDTO {
    @Schema(description = "反馈意见ID")
    private Long id;
    /**登录名，不可更改*/
    @Schema(description = "标题(可不传)")
    private String title;
    /**新密码*/
    @Schema(description = "新的反馈意见状态")
    private SuggestionStatusEnum newStatus;

    @Schema(description = "版本号")
    private Integer version;
}

package com.starmol.portal.backend.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

import com.starmol.portal.backend.model.base.BaseModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "SSO应用配置表")
@TableName(value = "oauth_client")
@Accessors(chain = true)
public class OauthClient extends BaseModel implements Serializable {
    private static final long serialVersionUID = 558480186846371899L;

    @Schema(description = "应用id，关联到应用表")
    private Long applicationId;

    @Schema(description = "客户应用名称")
    private String clientName;

    @Schema(description = "客户应用ID")
    private String clientId;

    @Schema(description = "客户应用秘钥")
    private String clientSecret;

    @Schema(description = "客户应用跳转URL")
    private String clientUrl;

    @Schema(description = "客户应用描述")
    private String clientDesc;

    @Schema(description = "客户应用logo")
    private String logoUrl;

    @Schema(description = "排序值，越小越靠前")
    private Integer ranking;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "是否启动, 1正常，2禁用")
    private Integer stateEnum;
}

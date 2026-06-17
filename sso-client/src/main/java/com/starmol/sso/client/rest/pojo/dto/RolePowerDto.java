package com.starmol.sso.client.rest.pojo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class RolePowerDto {

    @JsonProperty("role_id")
    private String roleId;

    @JsonProperty("api_info")
    private List<ApiInfo> apiInfo;

    @Setter
    @Getter
    public static class ApiInfo {

        @JsonProperty("api_uri")
        String apiUri;

        String method;
    }
}
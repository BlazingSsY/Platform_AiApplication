package com.starmol.sso.server.pojo.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class RolePowerDto {
    private String roleId;
    private List<ApiInfo> apiInfo;

    @Setter
    @Getter
    public static class ApiInfo {
        String apiUri;
        String method;
    }
}
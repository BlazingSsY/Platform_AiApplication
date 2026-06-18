package com.starmol.sso.client.rest.pojo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class UserRoleDto {
    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("role_id")
    private String roleId;
}
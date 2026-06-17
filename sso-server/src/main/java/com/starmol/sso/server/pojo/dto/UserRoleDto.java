package com.starmol.sso.server.pojo.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class UserRoleDto {
    private String userId;
    private String roleId;
}
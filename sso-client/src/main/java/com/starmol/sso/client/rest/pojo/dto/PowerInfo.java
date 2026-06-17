package com.starmol.sso.client.rest.pojo.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class PowerInfo {
    private String roleId;
    private List<String> apiPaths;
}
package com.starmol.sso.server.pojo.dto.param;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Power {
    private static final long serialVersionUID = 5760034272814953522L;
    String fId;
    private String id;
    private String name;
    private String alias;
    private String menuType;
    private String path;
    private String component;
    private String icon;
    private Boolean isFrame;
    private Boolean visible;
    private Boolean enabled;
    private Integer sequence;
}
package com.starmol.sso.client.rest.pojo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Power implements Serializable {
    private static final long serialVersionUID = 5760034272814953522L;
    @JsonProperty("f_id")
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

    @JsonProperty("menuType")
    public String getMenuType() {
        return menuType;
    }

    @JsonProperty("menu_type")
    public void setMenuType(String menuType) {
        this.menuType = menuType;
    }

    @JsonProperty("is_frame")
    public void setFrame(Boolean frame) {
        isFrame = frame;
    }

    @JsonProperty("isFrame")
    public Boolean isFrame() {
        return isFrame;
    }

}
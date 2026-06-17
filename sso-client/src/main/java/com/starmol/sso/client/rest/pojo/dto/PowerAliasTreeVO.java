package com.starmol.sso.client.rest.pojo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class PowerAliasTreeVO extends Power implements Serializable {

    private List<PowerAliasTreeVO> childList;

    @JsonProperty("childList")
    public List<PowerAliasTreeVO> getChildList() {
        return childList;
    }

    @JsonProperty("child_list")
    public void setChildList(List<PowerAliasTreeVO> childList) {
        this.childList = childList;
    }
}
package com.starmol.sso.server.pojo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.starmol.sso.server.pojo.dto.param.Power;

import java.util.List;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PowerAliasTreeVO extends Power {

    private List<PowerAliasTreeVO> childList;

}
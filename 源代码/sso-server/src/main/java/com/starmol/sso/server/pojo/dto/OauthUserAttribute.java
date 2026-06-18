package com.starmol.sso.server.pojo.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
public class OauthUserAttribute implements Serializable {

    private static final long serialVersionUID = -5890673731362415002L;
    private String id;
    private String loginName;
    private String userPhone;
    private String departmentId;
    private String departmentName;
    private String name;
    private String roleId;
    private List<String> clientIds;
    private List<PowerAliasTreeVO> powerAliasTree;
    private String profile;
    private String roleName;

    private boolean passwordExpired;
}

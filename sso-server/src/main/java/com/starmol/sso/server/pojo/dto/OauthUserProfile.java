package com.starmol.sso.server.pojo.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class OauthUserProfile {

    private static final long serialVersionUID = 8098354063458373513L;

    private String id;
    private String loginName;
    private String userPhone;
    private String departmentId;
    private String departmentName;
    private String name;
    private String roleId;
    private String roleName;
    private List<PowerAliasTreeVO> powerAliasTree;

//    private OauthUserAttribute userAttribute;
//    private String grantType;
//    private String clientId;

    private Long iat;
    private Long exp;
    private String profile;


}

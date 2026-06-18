package com.starmol.sso.server.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserLoginVO {
    private String id;
    private String departmentId;
    private String loginName;
    private String password;
    private String name;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private LocalDate birthday;
    private String profile;
    private String province;
    private String city;
    private String county;
    private String address;
    private String engage;
    private String workNo;
    private String email;
    private String sex;
    private String telephone;
    private Integer type;
    private String comments;
    private Boolean locked;
    private String roleName;
    private String roleId;
    private String departmentName;
    private List<String> clientIds;
    private Boolean passwdExpired;

    private List<PowerAliasTreeVO> powerAliasTree;

}

package com.starmol.sso.server.pojo.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author huguojun
 */
@Setter
@Getter
public class User {

    private String id;

    private String departmentId;

    private String loginName;

    private String name;

    private String email;

    private String sex;

    private String telephone;

    private String comments;

    private String departmentName;

    private List<String> clientIds;

    private String roleId;

}

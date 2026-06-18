package com.starmol.sso.server.pojo.dto;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class TokenAttribute implements Serializable {

    private static final long serialVersionUID = -1;

    private String id;
    private String loginName;
    private String userPhone;
    private String departmentId;
    private String departmentName;
    private String name;
    private String roleId;
    private List<String> clientIds;

}

package com.starmol.portal.backend.bean.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author huguojun
 */
@Setter
@Getter
@Accessors(chain = true)
public class UserInfoSummaryVO {
    private Long id;
    private String name;
    private String loginName;
    private Long roleId;
    private String roleName;
    private Long departmentId;
    private String departmentName;
}

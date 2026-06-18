package com.starmol.sso.server.util.feign.param;

import com.starmol.sso.server.pojo.dto.UserLoginVO;

import lombok.Getter;
import lombok.Setter;

/**
 * @author huguojun
 */
@Setter
@Getter
public class UserLoginResponse {
    private boolean succ;
    private String msg;
    private Integer code;
    private UserLoginVO content;

    public boolean isFailed() {
        return !succ;
    }

    public String getUserLoginName() {
        return content.getLoginName();
    }

    public String getUserName() {
        return content.getLoginName();
    }

    public String getUserEmail() {
        return content.getEmail();
    }

    public String getUserId() {
        return content.getId();
    }

    public String getPhone() {
        return content.getTelephone();
    }


}

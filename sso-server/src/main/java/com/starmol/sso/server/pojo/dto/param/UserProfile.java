package com.starmol.sso.server.pojo.dto.param;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@NoArgsConstructor
@Setter
@Getter
public class UserProfile {

    private static final long serialVersionUID = 8098354063458373513L;

    private String id;
    private String userPhone;
    private String name;
    private String oldPassword;
    private String password;
    private String profile;

    @JsonProperty("oldPassword")
    public String getOldPassword() {
        return oldPassword;
    }

    @JsonProperty("old_password")
    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    @JsonProperty("userPhone")
    public String getUserPhone() {
        return userPhone;
    }

    @JsonProperty("user_phone")
    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

}

package com.starmol.sso.client.rest.pojo.bo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * @author huguojun
 */
@Setter
@Getter
public class UserInfoPairs {
    @JsonProperty("user_name")
    String userName;
    @JsonProperty("user_id")
    String userId;
}

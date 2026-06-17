package com.starmol.sso.server.pojo.dto;

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
public class OauthUserInfo {

    private static final long serialVersionUID = 8098354063458373513L;

    private String userId;
    private String userName;
}

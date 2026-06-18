package com.starmol.sso.server.pojo.bo.cache;

import com.starmol.sso.server.pojo.dto.OauthUserAttribute;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class OauthUserInfoToRedisBO implements Serializable {


    private static final long serialVersionUID = 970124664158178650L;
    private OauthUserAttribute userAttribute;

    private Long iat;

}

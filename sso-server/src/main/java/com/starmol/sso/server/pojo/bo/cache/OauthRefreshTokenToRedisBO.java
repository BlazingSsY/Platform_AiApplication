package com.starmol.sso.server.pojo.bo.cache;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class OauthRefreshTokenToRedisBO extends OauthTokenToRedisBO implements Serializable {

    private static final long serialVersionUID = -2849331499164550050L;

    //因为不会经常被查询，所以不直接存用户信息
    private String userInfoRedisKey;

}

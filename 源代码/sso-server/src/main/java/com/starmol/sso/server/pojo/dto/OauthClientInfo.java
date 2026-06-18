package com.starmol.sso.server.pojo.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class OauthClientInfo implements Serializable {

    private String clientName;

}

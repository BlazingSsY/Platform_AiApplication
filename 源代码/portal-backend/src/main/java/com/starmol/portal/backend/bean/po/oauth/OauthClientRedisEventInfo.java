package com.starmol.portal.backend.bean.po.oauth;

import com.starmol.portal.backend.model.OauthClient;

import java.io.Serializable;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OauthClientRedisEventInfo implements Serializable {

    private static final long serialVersionUID = 4768780299364601364L;

    private List<OauthClient> oauthClientListByCreate;
    private List<OauthClient> oauthClientListByDelete;

}

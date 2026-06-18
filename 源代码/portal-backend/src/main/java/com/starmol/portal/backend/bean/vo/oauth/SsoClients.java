package com.starmol.portal.backend.bean.vo.oauth;

import com.starmol.portal.backend.model.OauthClient;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author huguojun
 */
@Setter
@Getter
public class SsoClients {
    private List<OauthClient> clients = new ArrayList<>();

    public SsoClients addClient(String clientName, String clientId) {
        clients.add(new OauthClient().setClientId(clientId).setClientName(clientName));
        return this;
    }

}

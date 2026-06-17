package com.starmol.portal.backend.eventlistener.event;

import com.starmol.portal.backend.bean.po.oauth.OauthClientRedisEventInfo;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class OauthClientRedisEvent extends ApplicationEvent {

    private final OauthClientRedisEventInfo oauthClientRedisEventInfo;

    public OauthClientRedisEvent(Object source, OauthClientRedisEventInfo oauthClientRedisEventInfo) {
        super(source);
        this.oauthClientRedisEventInfo = oauthClientRedisEventInfo;
    }
}

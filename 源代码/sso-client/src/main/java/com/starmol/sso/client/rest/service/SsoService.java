package com.starmol.sso.client.rest.service;


import com.starmol.sso.client.rest.exception.OauthApiException;
import com.starmol.sso.client.rest.pojo.bo.OAuth2AccessToken;
import com.starmol.sso.client.rest.pojo.dto.OauthUserProfile;
import com.starmol.sso.client.rest.pojo.dto.SsoTokenRequest;
import com.starmol.sso.client.rest.properties.SsoProperties;
import com.starmol.sso.client.rest.request.SsoRequestClient;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SsoService {

    @Resource
    private SsoProperties ssoProperties;

    @Resource
    private SsoRequestClient requestClient;

    //=====================================业务处理 start=====================================

    @SneakyThrows
    public OAuth2AccessToken getAccessToken(String grantType, String code, String redirectUrl) {
        final SsoTokenRequest ssoTokenRequest = new SsoTokenRequest();
        ssoTokenRequest.setCode(code);
        ssoTokenRequest.setClientId(ssoProperties.getClientId());
        ssoTokenRequest.setClientSecret(ssoProperties.getClientSecret());
        ssoTokenRequest.setRedirectUri(redirectUrl);
        ssoTokenRequest.setGrantType(grantType);
        final OAuth2AccessToken ssoResponse = requestClient.reqToken(ssoTokenRequest);
        if (ssoResponse.getError() != null) {
            throw new OauthApiException("获取 accessToken 失败");
        }
        return ssoResponse;
    }

    @SneakyThrows
    public OauthUserProfile getUserProfile(String token) {
        return requestClient.getProfile(token);
    }

}

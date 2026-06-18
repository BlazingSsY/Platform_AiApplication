package com.starmol.sso.server.strategy;


import com.starmol.sso.server.constant.GlobalVariable;
import com.starmol.sso.server.exception.OauthApiException;
import com.starmol.sso.server.pojo.bo.handle.OauthTokenStrategyHandleBO;
import com.starmol.sso.server.pojo.dto.OauthToken;
import com.starmol.sso.server.pojo.dto.OauthUserAttribute;
import com.starmol.sso.server.pojo.dto.param.OauthTokenParam;
import com.starmol.sso.server.service.IOauthThirdPartyApiService;
import com.starmol.sso.server.service.OauthCheckParamService;
import com.starmol.sso.server.service.OauthGenerateService;
import com.starmol.sso.server.service.OauthSaveService;
import com.starmol.sso.server.util.StringUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service(GlobalVariable.OAUTH_PASSWORD_GRANT_TYPE)
public class OauthPasswordToTokenStrategy implements OauthTokenStrategyInterface {

    @Autowired
    private OauthCheckParamService oauthCheckParamService;

    @Autowired
    private OauthGenerateService oauthGenerateService;

    @Autowired
    private OauthSaveService oauthSaveService;

    @Autowired
    private IOauthThirdPartyApiService managementApi;

    //=====================================业务处理 start=====================================

    @Override
    public void checkParam(OauthTokenParam oauthTokenParam, OauthTokenStrategyHandleBO oauthTokenStrategyHandleBO) {
        oauthCheckParamService.checkClientIdParam(oauthTokenParam.getClientId());
    }

    @Override
    public OauthToken handle(OauthTokenParam oauthTokenParam, OauthTokenStrategyHandleBO oauthTokenStrategyHandleBO) {
        String clientId = oauthTokenParam.getClientId();
        String clientSecret = oauthTokenParam.getClientSecret();
        oauthCheckParamService.checkClientIdAndClientSecretParam(clientId, clientSecret);
        String username = oauthTokenParam.getUsername();
        String password = oauthTokenParam.getPassword();
        oauthCheckParamService.checkUsernamePasswordParam(username, password);

        // 校验用户名密码
        OauthUserAttribute oauthUserAttribute = managementApi.getOauthUserAttributeDTO(username, password, clientId, oauthTokenParam.getCaptcha());
        if (null == oauthUserAttribute || StringUtil.isBlank(oauthUserAttribute.getId())) {
            throw new OauthApiException("用户名或密码错误");
        }

        String userInfoRedisKey = oauthGenerateService.generateUserInfoRedisKey(oauthUserAttribute.getId());
        oauthSaveService.saveUserInfoKeyToRedis(userInfoRedisKey, oauthUserAttribute);

        oauthTokenStrategyHandleBO.setUserAttribute(oauthUserAttribute);

        OauthUserAttribute userAttribute = oauthTokenStrategyHandleBO.getUserAttribute();

        OauthToken oauthTokenInfoByClientBO = oauthGenerateService.generateOauthTokenInfoBO(userAttribute, true);

        oauthSaveService.saveAccessToken(oauthTokenInfoByClientBO.getAccessToken(), userAttribute, clientId, GlobalVariable.OAUTH_PASSWORD_GRANT_TYPE);
        oauthSaveService.saveRefreshToken(oauthTokenInfoByClientBO.getRefreshToken(), userAttribute, clientId, GlobalVariable.OAUTH_PASSWORD_GRANT_TYPE);

        return oauthTokenInfoByClientBO;
    }

    //=====================================业务处理 end=====================================

}

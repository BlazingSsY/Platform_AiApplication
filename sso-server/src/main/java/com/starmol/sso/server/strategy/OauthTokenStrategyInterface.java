package com.starmol.sso.server.strategy;


import com.starmol.sso.server.pojo.bo.handle.OauthTokenStrategyHandleBO;
import com.starmol.sso.server.pojo.dto.OauthToken;
import com.starmol.sso.server.pojo.dto.param.OauthTokenParam;

public interface OauthTokenStrategyInterface {

    /**
     * 检查请求参数
     */
    void checkParam(OauthTokenParam oauthTokenParam, OauthTokenStrategyHandleBO oauthTokenStrategyHandleBO);

    /**
     * 生成 Token
     */
    OauthToken handle(OauthTokenParam oauthTokenParam, OauthTokenStrategyHandleBO oauthTokenStrategyHandleBO);

}

package com.starmol.sso.client.rest.endpoint;


import com.starmol.sso.client.rest.annotation.Permit;
import com.starmol.sso.client.rest.pojo.bo.OAuth2AccessToken;
import com.starmol.sso.client.rest.pojo.dto.OauthClientCodeRequestParam;
import com.starmol.sso.client.rest.pojo.dto.OauthTokenResponse;
import com.starmol.sso.client.rest.pojo.dto.OauthUserProfile;
import com.starmol.sso.client.rest.pojo.dto.SsoToken;
import com.starmol.sso.client.rest.properties.SsoProperties;
import com.starmol.sso.client.rest.request.ErrorCode;
import com.starmol.sso.client.rest.request.ResponseWrapper;
import com.starmol.sso.client.rest.service.SsoService;
import com.starmol.sso.client.rest.utils.CodecUtil;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping(value = "/oauth/client")
public class SsoCallbackController {

    final static String AUTHORIZATION_CODE_TYPE = "authorization_code";

    @Resource
    private SsoService ssoService;

    @Resource
    private SsoProperties ssoProperties;

    @Permit
    @PostMapping("/callback")
    public ResponseEntity<ResponseWrapper<Object>> callback(@RequestBody OauthClientCodeRequestParam codeRequestParam) {

        OauthTokenResponse oauthTokenResponse;
        try {
            SsoToken ssoToken = getAccessToken(codeRequestParam);
            String accessToken = ssoToken.getAccessToken();

            String redirectUri = codeRequestParam.getRedirectUri();
            redirectUri = CodecUtil.decodeURL(redirectUri);

            oauthTokenResponse = new OauthTokenResponse().setToken(accessToken).setRedirectUri(redirectUri).setProfile(ssoToken.getAttributes());
        } catch (Throwable e) {
            log.error("", e);
            return new ResponseEntity<>(ResponseWrapper.fail().setCode(ErrorCode.SYSTEM_ERROR), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(ResponseWrapper.success(oauthTokenResponse), HttpStatus.OK);

    }

    @GetMapping("/my-login-info")
    public ResponseEntity<ResponseWrapper<Object>> myLoginInfo(@CookieValue(name = "token") String token) {
        OauthUserProfile userProfile;
        try {
            userProfile = ssoService.getUserProfile(token);
        } catch (Throwable e) {
            return new ResponseEntity<>(ResponseWrapper.fail().setCode(ErrorCode.SYSTEM_ERROR), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(ResponseWrapper.success(userProfile), HttpStatus.OK);
    }

    private SsoToken getAccessToken(OauthClientCodeRequestParam code) {
        // redirect_uri 必须与 SSO authorize 阶段注册的 clientCodeCallbackUri 一致，
        // 不能使用前端传来的 redirectUri（那是 SDK 嵌入在 redirect_uri 中的源请求地址）
        OAuth2AccessToken oauthToken = ssoService.getAccessToken(AUTHORIZATION_CODE_TYPE, code.getCode(), ssoProperties.getClientCodeCallbackUri());
        String accessToken = oauthToken.getAccessToken();

        SsoToken token = new SsoToken();
        token.setAccessToken(accessToken);
        token.setRefreshToken(oauthToken.getRefreshToken());
        return token;
    }

}

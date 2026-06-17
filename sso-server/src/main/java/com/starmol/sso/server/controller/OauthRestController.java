package com.starmol.sso.server.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.starmol.sso.server.constant.GlobalVariable;
import com.starmol.sso.server.enums.ResponseProduceTypeEnum;
import com.starmol.sso.server.exception.OauthApiException;
import com.starmol.sso.server.exception.OauthRestException;
import com.starmol.sso.server.pojo.bo.cache.OauthAccessTokenToRedisBO;
import com.starmol.sso.server.pojo.bo.cache.OauthClientToRedisBO;
import com.starmol.sso.server.pojo.bo.cache.OauthRefreshTokenToRedisBO;
import com.starmol.sso.server.pojo.bo.cache.OauthTgcToRedisBO;
import com.starmol.sso.server.pojo.bo.cache.OauthUserInfoToRedisBO;
import com.starmol.sso.server.pojo.bo.handle.OauthTokenStrategyHandleBO;
import com.starmol.sso.server.pojo.dto.OauthClientInfo;
import com.starmol.sso.server.pojo.dto.OauthIntrospect;
import com.starmol.sso.server.pojo.dto.OauthToken;
import com.starmol.sso.server.pojo.dto.OauthUserAttribute;
import com.starmol.sso.server.pojo.dto.OauthUserProfile;
import com.starmol.sso.server.pojo.dto.ResponseWrapper;
import com.starmol.sso.server.pojo.dto.param.OauthAuthorizeParam;
import com.starmol.sso.server.pojo.dto.param.OauthClientParam;
import com.starmol.sso.server.pojo.dto.param.OauthFormLoginParam;
import com.starmol.sso.server.pojo.dto.param.OauthIntrospectTokenParam;
import com.starmol.sso.server.pojo.dto.param.OauthTokenParam;
import com.starmol.sso.server.properties.OauthProperties;
import com.starmol.sso.server.service.IOauthThirdPartyApiService;
import com.starmol.sso.server.service.OauthCheckParamService;
import com.starmol.sso.server.service.OauthGenerateService;
import com.starmol.sso.server.service.OauthSaveService;
import com.starmol.sso.server.strategy.OauthTokenStrategyContext;
import com.starmol.sso.server.util.CodecUtil;
import com.starmol.sso.server.util.CookieUtil;
import com.starmol.sso.server.util.IPUtil;
import com.starmol.sso.server.util.StringUtil;
import com.starmol.sso.server.util.redis.StringRedisService;
import com.starmol.sso.server.util.response.ResponseErrorEnum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping("/rest/oauth")
public class OauthRestController {


	@Autowired
	private StringRedisService<String, OauthTgcToRedisBO> tgcRedisService;

	@Autowired
	private StringRedisService<String, OauthUserInfoToRedisBO> userInfoRedisService;

	@Autowired
	private StringRedisService<String, OauthAccessTokenToRedisBO> accessTokenRedisService;

	@Autowired
	private StringRedisService<String, OauthRefreshTokenToRedisBO> refreshTokenRedisService;

	@Autowired
	private OauthCheckParamService oauthCheckParamService;

	@Autowired
	private OauthGenerateService oauthGenerateService;

	@Autowired
	private OauthSaveService oauthSaveService;

    @Autowired
    private IOauthThirdPartyApiService oauthThirdPartyApiService;

    @Autowired
    private OauthTokenStrategyContext oauthTokenStrategyContext;

    @Autowired
    private OauthProperties oauthProperties;

    @Autowired
    private ObjectMapper objectMapper;

    //=====================================业务处理 start=====================================

    /**
     * 登录页面入口
     */
    @GetMapping("/authorize")
    public ResponseWrapper<OauthClientInfo> authorize(final HttpServletRequest request, OauthAuthorizeParam oAuthAuthorizeParam) {

        OauthClientToRedisBO oauthClientToRedisBO = oauthCheckParamService.checkOauthAuthorizeParam(oAuthAuthorizeParam);
		final OauthClientInfo oauthClientInfo = new OauthClientInfo();
		oauthClientInfo.setClientName(oauthClientToRedisBO.getClientName());

		String tgcCookieValue = CookieUtil.getCookie(request, GlobalVariable.OAUTH_SERVER_COOKIE_KEY);
		if (StringUtil.isBlank(tgcCookieValue)) {
			throw new OauthRestException(ResponseErrorEnum.TGC_INVALID);
		}

		String userInfoRedisKey = oauthCheckParamService.checkCookieTgc(request.getHeader(GlobalVariable.HTTP_HEADER_USER_AGENT), IPUtil.getIp(request), tgcCookieValue);

		String finalRedirectUrl;
		String redirectUri = oAuthAuthorizeParam.getRedirectUri();
		if (StringUtil.equalsIgnoreCase(oAuthAuthorizeParam.getResponseType(), GlobalVariable.OAUTH_TOKEN_RESPONSE_TYPE)) {
			// 简化模式
			OauthUserInfoToRedisBO oauthUserInfoToRedisBO = userInfoRedisService.get(userInfoRedisKey);

			OauthToken oauthTokenInfoByCodePO = oauthGenerateService.generateOauthTokenInfoBO(oauthUserInfoToRedisBO.getUserAttribute(), true);
			oauthSaveService.saveAccessToken(oauthTokenInfoByCodePO.getAccessToken(), oauthUserInfoToRedisBO.getUserAttribute(), oAuthAuthorizeParam.getClientId(), GlobalVariable.OAUTH_TOKEN_GRANT_TYPE);
			oauthSaveService.saveRefreshToken(oauthTokenInfoByCodePO.getRefreshToken(), oauthUserInfoToRedisBO.getUserAttribute(), oAuthAuthorizeParam.getClientId(), GlobalVariable.OAUTH_TOKEN_GRANT_TYPE);
			finalRedirectUrl = getRedirectUrlWithAccessToken(redirectUri, oauthTokenInfoByCodePO);
		} else {
			// 授权码模式
			String code = oauthGenerateService.generateCode();
			oauthSaveService.saveCodeToRedis(code, tgcCookieValue, userInfoRedisKey, oAuthAuthorizeParam.getClientId());
			finalRedirectUrl = getRedirectUrlWithCode(redirectUri, oAuthAuthorizeParam.getState(), code);
		}

		oauthSaveService.updateTgcAndUserInfoRedisKeyExpire(tgcCookieValue, userInfoRedisKey);
		return ResponseWrapper.success();
	}

	/**
	 * 表单登录接口：验证用户名和密码
	 */
	@PostMapping("/authorize")
	public void formLogin(final HttpServletRequest request, final HttpServletResponse response, OauthFormLoginParam oauthFormLoginParam) {

		OauthUserAttribute oauthUserAttribute;
		String userAgent = request.getHeader(GlobalVariable.HTTP_HEADER_USER_AGENT);
		String requestIp = IPUtil.getIp(request);

		try {
			oauthCheckParamService.checkClientIdParam(oauthFormLoginParam.getClientId());
			oauthCheckParamService.checkUserAgentAndRequestIpParam(userAgent, requestIp);
			oauthCheckParamService.checkOauthFormLoginParam(oauthFormLoginParam);

			// 校验用户名密码
			oauthUserAttribute = requestLoginApi(oauthFormLoginParam);
		} catch (Exception e) {
			throw new OauthRestException(ResponseErrorEnum.LOGIN_ERROR);
		}

		String userInfoRedisKey = oauthGenerateService.generateUserInfoRedisKey(oauthUserAttribute.getId());
		oauthSaveService.saveUserInfoKeyToRedis(userInfoRedisKey, oauthUserAttribute);

		boolean isRememberMe = oauthFormLoginParam.getBoolIsRememberMe();
		String tgc = oauthGenerateService.generateTgc();

		Integer maxTimeToLiveInSeconds = oauthProperties.getTgcAndUserInfoMaxTimeToLiveInSeconds();
		if (isRememberMe) {
			maxTimeToLiveInSeconds = oauthProperties.getRememberMeMaxTimeToLiveInSeconds();
		}
		CookieUtil.setCookie(response, GlobalVariable.OAUTH_SERVER_COOKIE_KEY, tgc, maxTimeToLiveInSeconds, true, oauthProperties.getTgcCookieSecure());

		oauthSaveService.saveTgcToRedisAndCookie(tgc, maxTimeToLiveInSeconds, userInfoRedisKey, userAgent, requestIp, isRememberMe);

		String finalRedirectUrl;
		String redirectUri = oauthFormLoginParam.getRedirectUri();
		if (StringUtil.equalsIgnoreCase(oauthFormLoginParam.getResponseType(), GlobalVariable.OAUTH_TOKEN_RESPONSE_TYPE)) {
			// 简化模式
			OauthToken oauthToken = oauthGenerateService.generateOauthTokenInfoBO(oauthUserAttribute, true);
			oauthSaveService.saveAccessToken(oauthToken.getAccessToken(), oauthUserAttribute, oauthFormLoginParam.getClientId(), GlobalVariable.OAUTH_TOKEN_GRANT_TYPE);
			oauthSaveService.saveRefreshToken(oauthToken.getRefreshToken(), oauthUserAttribute, oauthFormLoginParam.getClientId(), GlobalVariable.OAUTH_TOKEN_GRANT_TYPE);
			finalRedirectUrl = getRedirectUrlWithAccessToken(redirectUri, oauthToken);
		} else {
			// 授权码模式
			String code = oauthGenerateService.generateCode();
			oauthSaveService.saveCodeToRedis(code, tgc, userInfoRedisKey, oauthFormLoginParam.getClientId());
			finalRedirectUrl = getRedirectUrlWithCode(redirectUri, oauthFormLoginParam.getState(), code);
		}

	}

	private void redirect(HttpServletResponse response, String finalRedirectUrl) {
		try {
			response.sendRedirect(finalRedirectUrl);
		} catch (IOException e) {
			throw new OauthApiException("send redirect failed.");
		}
	}


	/**
	 * 换取 token（授权码模式、客户端模式、密码模式、刷新模式）
	 */
	@PostMapping("/token")
	@ResponseBody
	public ResponseEntity<?> token(final HttpServletRequest request, @RequestBody OauthTokenParam oauthTokenParam) {
		String grantType = oauthTokenParam.getGrantType();
		oauthCheckParamService.checkGrantTypeParam(grantType);
		resolveRequestHeader(request, oauthTokenParam);

		OauthTokenStrategyHandleBO oauthTokenStrategyHandleBO = new OauthTokenStrategyHandleBO();
		oauthTokenStrategyContext.checkParam(grantType, oauthTokenParam, oauthTokenStrategyHandleBO);

		OauthToken oauthToken = oauthTokenStrategyContext.generateOauthTokenInfo(grantType, oauthTokenParam, oauthTokenStrategyHandleBO);
		final ResponseEntity<OauthToken> ok = ResponseEntity.ok(oauthToken);
		log.info("ok: {}", ok);
		return ok;
	}


	/**
	 * 根据 AccessToken 获取用户信息
	 */
	@PostMapping("/userinfo")
	public ResponseEntity<?> userinfo(final HttpServletRequest request) {
        OauthAccessTokenToRedisBO oauthAccessTokenToRedisBO = oauthCheckParamService.checkAccessTokenParam(request);
        final OauthUserProfile oauthUserProfile = buildOauthUserInfoByTokenDTO(oauthAccessTokenToRedisBO);
		return ResponseEntity.ok(oauthUserProfile);
	}


	/**
	 * 验证 AccessToken / RefreshToken 有效性和基础信息
	 */
	@PostMapping("/introspect")
	public ResponseWrapper<OauthIntrospect> introspect(final HttpServletRequest request, OauthIntrospectTokenParam oauthIntrospectTokenParam) {

		resolveRequestHeader(request, oauthIntrospectTokenParam);
		OauthIntrospect oauthIntrospect = oauthCheckParamService.checkOauthIntrospectTokenParam(oauthIntrospectTokenParam);

		Long iat = 0L;
		String grantType = "";

		String token = oauthIntrospectTokenParam.getToken();
		String tokenTypeHint = oauthIntrospectTokenParam.getTokenTypeHint();
		if (StringUtil.equalsIgnoreCase(tokenTypeHint, GlobalVariable.OAUTH_ACCESS_TOKEN_TYPE_HINT)) {
			// 验证 AccessToken
			OauthAccessTokenToRedisBO oauthTokenToRedisDTO = accessTokenRedisService.get(GlobalVariable.REDIS_OAUTH_ACCESS_TOKEN_KEY_PREFIX + token);
			if (null == oauthTokenToRedisDTO) {
				throw new OauthApiException("token 已失效");
			}
			grantType = oauthTokenToRedisDTO.getGrantType();
			iat = oauthTokenToRedisDTO.getIat();
			oauthIntrospect.setExp(iat + oauthProperties.getAccessTokenMaxTimeToLiveInSeconds());
		} else if (StringUtil.equalsIgnoreCase(tokenTypeHint, GlobalVariable.OAUTH_REFRESH_TOKEN_GRANT_TYPE)) {
			// 验证 RefreshToken
			OauthRefreshTokenToRedisBO oauthTokenToRedisDTO = refreshTokenRedisService.get(GlobalVariable.REDIS_OAUTH_REFRESH_TOKEN_KEY_PREFIX + token);
			if (null == oauthTokenToRedisDTO) {
				throw new OauthApiException("token 已失效");
			}
			grantType = oauthTokenToRedisDTO.getGrantType();
			iat = oauthTokenToRedisDTO.getIat();
			oauthIntrospect.setExp(iat + oauthProperties.getRefreshTokenMaxTimeToLiveInSeconds());
		}

		oauthIntrospect.setGrantType(grantType);
		oauthIntrospect.setIat(iat);

		return ResponseWrapper.success(oauthIntrospect);
	}

	/**
	 * 登出
	 */
	@PostMapping("/logout")
	public String logout(final HttpServletRequest request, final HttpServletResponse response, @RequestParam(value = "redirect_uri", required = false) String redirectUri) {

		String tgcCookieValue = CookieUtil.getCookie(request, GlobalVariable.OAUTH_SERVER_COOKIE_KEY);
		if (StringUtil.isNotBlank(tgcCookieValue)) {
			userInfoRedisService.delete(GlobalVariable.REDIS_TGC_KEY_PREFIX + tgcCookieValue);
			CookieUtil.deleteCookie(request, response, GlobalVariable.OAUTH_SERVER_COOKIE_KEY);
		}

		if (StringUtil.isNotBlank(redirectUri)) {
			return GlobalVariable.REDIRECT_URI_PREFIX + redirectUri;
		}

		return GlobalVariable.DEFAULT_LOGOUT_PAGE_PATH;

	}

	//=====================================业务处理  end=====================================

	//=====================================私有方法 start=====================================


	private OauthUserAttribute requestLoginApi(OauthFormLoginParam oauthFormLoginParam) {
		OauthUserAttribute oauthUserAttribute = oauthThirdPartyApiService.getOauthUserAttributeDTO(oauthFormLoginParam.getUsername(), oauthFormLoginParam.getPassword(), oauthFormLoginParam.getClientId(), oauthFormLoginParam.getCaptcha());
		if (null == oauthUserAttribute || StringUtil.isBlank(oauthUserAttribute.getId())) {
			log.error("调用 UserService 接口返回错误信息，用户名：<{}>", oauthFormLoginParam.getUsername());
			throw new OauthApiException("用户名/密码验证错误", ResponseProduceTypeEnum.HTML, GlobalVariable.DEFAULT_LOGIN_PAGE_PATH);
		}
		return oauthUserAttribute;
    }

    private OauthUserProfile buildOauthUserInfoByTokenDTO(OauthAccessTokenToRedisBO oauthAccessTokenToRedisBO) {
        OauthUserAttribute oauthUserAttribute = oauthAccessTokenToRedisBO.getUserAttribute();

        // 客户端模式情况下是没有用户信息的
        if (null == oauthUserAttribute) {
            return new OauthUserProfile();
        }

        return objectMapper.convertValue(oauthUserAttribute, OauthUserProfile.class);
	}


	private String getRedirectUrlWithCode(String redirectUri, String state, String code) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(redirectUri);
		if (StringUtil.containsIgnoreCase(redirectUri, "?")) {
			stringBuilder.append("&");
		} else {
			stringBuilder.append("?");
		}
		stringBuilder.append(GlobalVariable.OAUTH_CODE_RESPONSE_TYPE);
		stringBuilder.append("=");
		stringBuilder.append(code);
		if (StringUtil.isNotBlank(state)) {
			stringBuilder.append("&");
			stringBuilder.append(GlobalVariable.OAUTH_STATE_KEY);
			stringBuilder.append("=");
			stringBuilder.append(state);
		}

		return stringBuilder.toString();
	}

	private String getRedirectUrlWithAccessToken(String redirectUri, OauthToken oauthToken) {
		return redirectUri +
				"#" +
				GlobalVariable.OAUTH_ACCESS_TOKEN_KEY +
				"=" +
				oauthToken.getAccessToken() +
				"&" +
				GlobalVariable.OAUTH_TOKEN_TYPE_KEY +
				"=" +
				GlobalVariable.OAUTH_TOKEN_TYPE +
				"&" +
				GlobalVariable.OAUTH_EXPIRES_IN_KEY +
				"=" +
				oauthProperties.getAccessTokenMaxTimeToLiveInSeconds() +
				"&" +
				GlobalVariable.OAUTH_REFRESH_TOKEN_KEY +
				"=" +
				oauthToken.getRefreshToken();
	}

	private void resolveRequestHeader(HttpServletRequest request, OauthClientParam oauthClientParam) {
		String authorizationHeader = request.getHeader(GlobalVariable.HTTP_HEADER_AUTHORIZATION);
		if (StringUtil.isBlank(authorizationHeader)) {
			return;
		}

		if (StringUtil.containsIgnoreCase(authorizationHeader, GlobalVariable.BASIC_AUTH_UPPER_PREFIX)) {
			String replaceIgnoreCase = StringUtil.replaceIgnoreCase(authorizationHeader, GlobalVariable.BASIC_AUTH_UPPER_PREFIX, GlobalVariable.BASIC_AUTH_LOWER_PREFIX);
			authorizationHeader = StringUtil.substringAfter(replaceIgnoreCase, GlobalVariable.BASIC_AUTH_LOWER_PREFIX);
		}
		String basic = CodecUtil.base64DecodeBySafe(authorizationHeader);
		List<String> stringList = StringUtil.split(basic, ":");
		if (stringList.size() < 2) {
			return;
		}
		oauthClientParam.setClientId(stringList.get(0));
		oauthClientParam.setClientSecret(stringList.get(1));

	}
	//=====================================私有方法  end=====================================

}

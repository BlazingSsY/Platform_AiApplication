package com.starmol.sso.server.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.starmol.sso.server.constant.GlobalVariable;
import com.starmol.sso.server.enums.ResponseProduceTypeEnum;
import com.starmol.sso.server.exception.OauthApiException;
import com.starmol.sso.server.pojo.bo.cache.OauthAccessTokenToRedisBO;
import com.starmol.sso.server.pojo.bo.cache.OauthClientToRedisBO;
import com.starmol.sso.server.pojo.bo.cache.OauthRefreshTokenToRedisBO;
import com.starmol.sso.server.pojo.bo.cache.OauthTgcToRedisBO;
import com.starmol.sso.server.pojo.bo.cache.OauthUserInfoToRedisBO;
import com.starmol.sso.server.pojo.bo.handle.OauthTokenStrategyHandleBO;
import com.starmol.sso.server.pojo.dto.OauthIntrospect;
import com.starmol.sso.server.pojo.dto.OauthToken;
import com.starmol.sso.server.pojo.dto.OauthUserAttribute;
import com.starmol.sso.server.pojo.dto.OauthUserInfo;
import com.starmol.sso.server.pojo.dto.OauthUserProfile;
import com.starmol.sso.server.pojo.dto.ResponseWrapper;
import com.starmol.sso.server.pojo.dto.RoleSyncResult;
import com.starmol.sso.server.pojo.dto.param.OauthAuthorizeParam;
import com.starmol.sso.server.pojo.dto.param.OauthClientParam;
import com.starmol.sso.server.pojo.dto.param.OauthFormLoginParam;
import com.starmol.sso.server.pojo.dto.param.OauthIntrospectTokenParam;
import com.starmol.sso.server.pojo.dto.param.OauthTokenParam;
import com.starmol.sso.server.pojo.dto.param.PasswordUpdateDto;
import com.starmol.sso.server.pojo.dto.param.UserAttributeModel;
import com.starmol.sso.server.pojo.dto.param.UserProfile;
import com.starmol.sso.server.properties.OauthProperties;
import com.starmol.sso.server.service.IOauthThirdPartyApiService;
import com.starmol.sso.server.service.OauthCheckParamService;
import com.starmol.sso.server.service.OauthGenerateService;
import com.starmol.sso.server.service.OauthSaveService;
import com.starmol.sso.server.service.OauthUserInfoService;
import com.starmol.sso.server.strategy.OauthTokenStrategyContext;
import com.starmol.sso.server.util.CodecUtil;
import com.starmol.sso.server.util.CookieUtil;
import com.starmol.sso.server.util.IPUtil;
import com.starmol.sso.server.util.StringUtil;
import com.starmol.sso.server.util.redis.StringRedisService;
import com.starmol.sso.server.util.response.ResponseErrorEnum;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Controller
@RequestMapping("/oauth")
public class OauthController {

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

	@Autowired
	private OauthUserInfoService oauthUserInfoService;

	//=====================================业务处理 start=====================================

	/**
	 * 登录页面入口
	 */
	@RequestMapping(value = "/authorize", method = RequestMethod.GET)
	public String authorize(final HttpServletRequest request, ModelMap model, OauthAuthorizeParam oAuthAuthorizeParam) {

		OauthClientToRedisBO oauthClientToRedisBO = oauthCheckParamService.checkOauthAuthorizeParam(oAuthAuthorizeParam);

		model.put(GlobalVariable.DEFAULT_LOGIN_PAGE_CLIENT_INFO_KEY, oauthClientToRedisBO);

		String tgcCookieValue = CookieUtil.getCookie(request, GlobalVariable.OAUTH_SERVER_COOKIE_KEY);
		log.info("request url is: " + request.getRequestURL());
		log.info("get X-Forwarded-Proto is: " + request.getHeader("X-Forwarded-Proto"));
		if (StringUtil.isBlank(tgcCookieValue)) {
			return GlobalVariable.DEFAULT_LOGIN_PAGE_PATH;
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
		log.info("redirect to: {}", finalRedirectUrl);
		return GlobalVariable.REDIRECT_URI_PREFIX + finalRedirectUrl;
	}

	/**
	 * 表单登录接口：验证用户名和密码
	 */
	@RequestMapping(value = "/authorize", method = RequestMethod.POST)
	public String formLogin(final HttpServletRequest request, final HttpServletResponse response, ModelMap model, OauthFormLoginParam oauthFormLoginParam) {


		String userAgent = request.getHeader(GlobalVariable.HTTP_HEADER_USER_AGENT);
		String requestIp = IPUtil.getIp(request);

		oauthCheckParamService.checkClientIdParam(oauthFormLoginParam.getClientId());
		oauthCheckParamService.checkUserAgentAndRequestIpParam(userAgent, requestIp);
		oauthCheckParamService.checkOauthFormLoginParam(oauthFormLoginParam);

		if (oauthFormLoginParam.isBoolIsUpdatePwd()) {
			updatePwd(model, oauthFormLoginParam);
		}

		// 校验用户名密码
		OauthUserAttribute oauthUserAttribute = requestLoginApi(oauthFormLoginParam);
		if (oauthUserAttribute.isPasswordExpired()) {
			model.put(GlobalVariable.USER_LOGIN_INFO_KEY, new UserAttributeModel()
					.setPasswordExpired(true)
					.setPassword(oauthFormLoginParam.getPassword())
					.setUsername(oauthFormLoginParam.getUsername()));
			return GlobalVariable.DEFAULT_LOGIN_PAGE_PATH;
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

		log.info("redirect to: {}", finalRedirectUrl);
		return GlobalVariable.REDIRECT_URI_PREFIX + finalRedirectUrl;

	}

	public void updatePwd(ModelMap model, OauthFormLoginParam oauthTokenParam) {
		final ResponseEntity<ResponseWrapper<String>> responseEntity = oauthThirdPartyApiService.updateUserPassword(oauthTokenParam.getUsername(), oauthTokenParam.getOldPassword(), oauthTokenParam.getPassword());
		final ResponseWrapper<String> responseWrapper = Optional.ofNullable(responseEntity.getBody())
				.orElse(ResponseWrapper.failed("请求无响应", ResponseErrorEnum.CALL_INNER_ERROR.getCode()));
		if (BooleanUtils.isFalse(responseWrapper.isSucc())) {
			model.put(GlobalVariable.USER_LOGIN_INFO_KEY, new UserAttributeModel()
					.setPasswordExpired(true));
			throw new OauthApiException(responseWrapper.getMsg(), ResponseProduceTypeEnum.HTML, GlobalVariable.DEFAULT_LOGIN_PAGE_PATH);
		}

	}


	/**
	 * 换取 token（授权码模式、客户端模式、密码模式、刷新模式）
	 */
	@RequestMapping(value = "/token", method = {RequestMethod.POST}, consumes = MediaType.APPLICATION_JSON_VALUE)
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
	@RequestMapping(value = "/userinfo", method = {RequestMethod.GET})
	@ResponseBody
	public OauthUserProfile userinfo(final HttpServletRequest request) {
		OauthAccessTokenToRedisBO oauthAccessTokenToRedisBO = oauthCheckParamService.checkAccessTokenParam(request);
		return buildOauthUserInfoByTokenDTO(oauthAccessTokenToRedisBO);
	}

	/**
	 * 根据 AccessToken 获取用户信息
	 */
	@RequestMapping(value = "/userinfo", method = {RequestMethod.POST})
	@ResponseBody
	public ResponseWrapper<String> updateUserInfo(final HttpServletRequest request, @RequestBody UserProfile userProfile) {
		oauthCheckParamService.checkAccessTokenParam(request);
		oauthUserInfoService.updateUserProfile(userProfile);
		return ResponseWrapper.success();
	}


	/**
	 * 根据 AccessToken 获取用户信息
	 */
	@RequestMapping(value = "/reset-password", method = {RequestMethod.POST})
	@ResponseBody
	public ResponseEntity<ResponseWrapper<String>> resetPassword(final HttpServletRequest request, @RequestBody PasswordUpdateDto pwd) {
		final OauthAccessTokenToRedisBO oauthAccessTokenToRedisBO = oauthCheckParamService.checkAccessTokenParam(request);
		final String loginName = oauthAccessTokenToRedisBO.getUserAttribute().getLoginName();
		return oauthThirdPartyApiService.updateUserPassword(loginName, pwd.getOldPassword(), pwd.getPassword());
	}


	/**
	 * 根据 AccessToken 获取用户信息
	 */
	@RequestMapping(value = "/user-id-map", method = {RequestMethod.POST})
	@ResponseBody
	public List<OauthUserInfo> userList(@RequestBody List<String> userIds, final HttpServletRequest request) {
		oauthCheckParamService.checkAccessTokenParam(request);
		return oauthUserInfoService.getUserIdNameMapByIds(userIds);
	}

	/**
	 * 根据 AccessToken 获取用户信息
	 */
	@RequestMapping(value = "/roleInfo", method = {RequestMethod.GET})
	@ResponseBody
	public RoleSyncResult roleInfo(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader, @RequestParam("clientId") String clientId) {
		return oauthCheckParamService.getRoleInfo(clientId);
	}


	/**
	 * 验证 AccessToken / RefreshToken 有效性和基础信息
	 */
	@RequestMapping(value = "/introspect", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> introspect(final HttpServletRequest request, OauthIntrospectTokenParam oauthIntrospectTokenParam) {

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

		return ResponseEntity.ok(oauthIntrospect);
	}

	/**
	 * 登出
	 */
	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(final HttpServletRequest request, final HttpServletResponse response, @RequestParam(value = "redirect_uri", required = false) String redirectUri) {

		String tgcCookieValue = CookieUtil.getCookie(request, GlobalVariable.OAUTH_SERVER_COOKIE_KEY);
		if (StringUtil.isNotBlank(tgcCookieValue)) {
			tgcRedisService.delete(GlobalVariable.REDIS_TGC_KEY_PREFIX + tgcCookieValue);
			CookieUtil.deleteCookie(request, response, GlobalVariable.OAUTH_SERVER_COOKIE_KEY);
		}

		if (StringUtil.isNotBlank(redirectUri)) {
			return GlobalVariable.REDIRECT_URI_PREFIX + redirectUri;
		}

		return GlobalVariable.DEFAULT_LOGOUT_PAGE_PATH;

	}

	@RequestMapping(value = "/captcha", method = {RequestMethod.GET}, produces = MediaType.IMAGE_JPEG_VALUE)
	public ResponseEntity<InputStreamResource> captcha() {
		return oauthThirdPartyApiService.generateCaptcha();
	}

	//=====================================业务处理  end=====================================

	//=====================================私有方法 start=====================================


	private OauthUserAttribute requestLoginApi(OauthFormLoginParam oauthFormLoginParam) {
		return oauthThirdPartyApiService.getOauthUserAttributeDTO(oauthFormLoginParam.getUsername()
				, oauthFormLoginParam.getPassword(), oauthFormLoginParam.getClientId(), oauthFormLoginParam.getCaptcha());
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

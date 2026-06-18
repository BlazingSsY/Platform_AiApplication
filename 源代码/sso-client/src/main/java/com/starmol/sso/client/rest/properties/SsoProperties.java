package com.starmol.sso.client.rest.properties;

import com.starmol.sso.client.rest.utils.CodecUtil;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;


@ConfigurationProperties(prefix = "sso.oauth.client")
@Setter
@Getter
public class SsoProperties {

	private String clientId;
	private String clientSecret;
	private String ssoServerUri;
	private Interceptor interceptor;

	private Boolean enableCodeCallbackToFront = false;
	private String clientCodeCallbackUri;
	private String clientLogoutRedirectUri;
	private String tokenSignKey;

	public String getFinalLogoutUri() {
		return getFinalLogoutUri(clientLogoutRedirectUri);
	}

	public String getFinalLogoutUri(String redirectUri) {
		if (StringUtils.isBlank(redirectUri)) {
			redirectUri = clientLogoutRedirectUri;
		}
		return getServerLogoutUri() + "?redirect_uri=" + redirectUri;
	}

	public String getFinalRedirectUri(javax.servlet.http.HttpServletRequest request) {
		return getFinalRedirectUri(request, false);
	}

	public String getFinalRedirectUri(javax.servlet.http.HttpServletRequest request, Boolean useReferer) {
		String sourceRequestURL = request.getRequestURL().toString();
		String queryParam = request.getQueryString();
		if (StringUtils.isNotBlank(queryParam)) {
			sourceRequestURL = sourceRequestURL + "?" + queryParam;
		}

		if (useReferer) {
			String refererUrl = request.getHeader("referer");
			if (StringUtils.isNotBlank(refererUrl)) {
				sourceRequestURL = refererUrl;
			}
		}

		sourceRequestURL = CodecUtil.encodeURL(sourceRequestURL);
		clientCodeCallbackUri = StringUtils.removeEnd(clientCodeCallbackUri, "/");
		sourceRequestURL = CodecUtil.encodeURL(clientCodeCallbackUri + "?redirect_uri=" + sourceRequestURL);
		return ssoServerUri + "/oauth/authorize?response_type=code&client_id=" + clientId + "&redirect_uri=" + sourceRequestURL;
	}

	public String getServerLogoutUri() {
		return ssoServerUri + "/oauth/logout";
	}

	public String getAccessTokenUri() {
		return ssoServerUri + "/oauth/token";
	}

	public String getUserInfoUri() {
		return ssoServerUri + "/oauth/userinfo";
	}

	public String getAuthorizeUri() {
		return ssoServerUri + "/oauth/authorize";
	}

	@Setter
	@Getter
	static class Interceptor {
		private Boolean enabled;
	}

}

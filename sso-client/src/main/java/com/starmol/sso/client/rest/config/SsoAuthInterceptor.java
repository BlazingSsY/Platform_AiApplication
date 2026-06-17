package com.starmol.sso.client.rest.config;


import com.starmol.sso.client.rest.annotation.Permit;
import com.starmol.sso.client.rest.exception.OauthApiException;
import com.starmol.sso.client.rest.exception.OauthClientIdNotValidException;
import com.starmol.sso.client.rest.pojo.bo.UniUser;
import com.starmol.sso.client.rest.properties.SsoProperties;
import com.starmol.sso.client.rest.utils.JwtTokenUtil;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.WebUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.jsonwebtoken.Claims;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SsoAuthInterceptor implements HandlerInterceptor {

	private static final String TOKEN_KEY = "token";
	private static final String USER_CONTEXT_KEY = "uniUser";
	private static final String BEARER_KEY = "AT-";
	private final SsoProperties ssoProperties;


	public SsoAuthInterceptor(SsoProperties ssoProperties) {
		this.ssoProperties = ssoProperties;
	}

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String path = request.getServletPath();
		response.addHeader(HttpHeaders.CACHE_CONTROL, CacheControl.noCache().getHeaderValue());
		if (this.checkURL(path)) {
			return true;
		}

		Permit permit = this.authPermit(handler, path);
		if (permit != null) {
			return true;
		}

		try {
			final Cookie tokenCookie = Optional.ofNullable(WebUtils.getCookie(request, TOKEN_KEY)).orElse(new Cookie(TOKEN_KEY, ""));
			UniUser uniUser = this.authToken(tokenCookie.getValue());
			request.setAttribute(USER_CONTEXT_KEY, uniUser);
			return true;
		} catch (OauthApiException e) {
			response.sendError(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
			return false;
		} catch (OauthClientIdNotValidException e) {
			log.error("the client id not valid.");
			response.sendError(HttpStatus.FORBIDDEN.value(), e.getMessage());
			return false;
		} catch (Exception e) {
			log.error("Invalid token was found");
			response.sendError(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
			throw e;
		}
	}

	private boolean checkURL(String path) {
		if (path != null) {
			path = path.toLowerCase();
		}

		return path != null
				&& (path.contains("/swagger-resources")
				|| path.contains("/api-docs")
				|| path.contains("/api-docs-ext")
				|| path.contains("swagger-ui")
				|| path.equals("/")
				|| path.equals("/error")
				|| path.equals("/csrf")
				|| path.contains(".css")
				|| path.contains(".js")
				|| path.contains(".woff")
				|| path.contains("/favicon.ico")
				|| path.contains("doc.html")
				|| path.contains("/index.html"));
	}

	private Permit authPermit(Object handler, String path) {
		Method method;
		try {
			if (handler instanceof HandlerMethod) {
				HandlerMethod hm = (HandlerMethod) handler;
				method = hm.getMethod();
			} else {
				return null;
			}
		} catch (Exception var7) {
			log.error(String.format("Cast handler for %s with error: " + var7.getMessage(), path), var7);
			return null;
		}

		Class<?> declaringClass = method.getDeclaringClass();
		Permit permit = null;
		if (method.isAnnotationPresent(Permit.class)) {
			permit = method.getAnnotation(Permit.class);
		} else if (declaringClass.isAnnotationPresent(Permit.class)) {
			permit = declaringClass.getAnnotation(Permit.class);
		}

		return permit;
	}

	private UniUser authToken(String authHeader) {

		if (StringUtils.isBlank(authHeader) || isNotStartWithTokenPrefix(authHeader)) {
			throw new OauthApiException("登录信息无效，请重新登录");
		}

		String token = authHeader.substring(BEARER_KEY.length());
		Claims claims = JwtTokenUtil.parse(token, ssoProperties.getTokenSignKey());
		checkClientIdValid(claims);
		final UniUser uniUser = createClaims(claims, UniUser.class);
		uniUser.setToken(authHeader);
		return uniUser;
	}

	private boolean isNotStartWithTokenPrefix(String authHeader) {
		return !authHeader.startsWith(BEARER_KEY);
	}

	private void checkClientIdValid(Claims claims) {
		final List<String> clientIds = (List) claims.get("clientIds");
		if (Objects.isNull(clientIds)) {
			throw new OauthClientIdNotValidException("client id not valid with this system.");
		}
		final Set<String> collect = new HashSet<>(clientIds);
		if (!collect.contains(ssoProperties.getClientId())) {
			throw new OauthClientIdNotValidException("client id not valid with this system.");
		}
	}

	@SneakyThrows
	private <T> T createClaims(Claims claims, Class<T> clazz) {
		final T o = clazz.getDeclaredConstructor().newInstance();
		final Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			final String name = field.getName();
			final Object val = claims.get(name, field.getType());
			final Method method = clazz.getMethod("set" + StringUtils.capitalize(name), field.getType());
			method.invoke(o, val);
		}
		return o;
	}

}

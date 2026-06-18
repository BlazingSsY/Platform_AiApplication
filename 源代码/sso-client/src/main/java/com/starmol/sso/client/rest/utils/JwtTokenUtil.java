package com.starmol.sso.client.rest.utils;

import com.starmol.sso.client.rest.exception.OauthApiException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;

public class JwtTokenUtil {

	public static Claims parse(String token, String signKey) throws OauthApiException {
		try {
			return Jwts.parser().setSigningKey(signKey).parseClaimsJws(token).getBody();
		} catch (ExpiredJwtException var5) {
			throw new OauthApiException("登录信息已过期，请重新登录");
		} catch (Exception var6) {
			throw new OauthApiException("登录信息无效，请重新登录");
		}
	}
}

package com.starmol.sso.server.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * @author huguojun
 */
public class JwtTokenUtil {

	private static final String REFRESH_TOKEN_USER_INFO_KEY = "userId";

	public static String generateToken(Map<String, Object> claimData, String signKey, long expireMinutes) {
		LocalDateTime currentTime = LocalDateTime.now();
		String tokenId = IdWorker.getInstance().nextUUID();
		final JwtBuilder jwtBuilder = Jwts.builder().setId(tokenId).setSubject(tokenId);
		for (Map.Entry<String, Object> entry : claimData.entrySet()) {
			jwtBuilder.claim(entry.getKey(), entry.getValue());
		}
		return jwtBuilder.setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
				.setExpiration(Date.from(currentTime.plusMinutes(expireMinutes).atZone(ZoneId.systemDefault()).toInstant())).signWith(SignatureAlgorithm.HS256, signKey)
				.compact();
	}

	public static String generateRefreshToken(String userId, String signKey, long expireMinutes) {
		LocalDateTime currentTime = LocalDateTime.now();
		return Jwts.builder().setId(IdWorker.getInstance().nextUUID()).claim(REFRESH_TOKEN_USER_INFO_KEY, userId)
				.setIssuedAt(Date.from(currentTime.atZone(ZoneId.systemDefault()).toInstant()))
				.setExpiration(Date.from(currentTime.plusMinutes(expireMinutes).atZone(ZoneId.systemDefault()).toInstant()))
				.signWith(SignatureAlgorithm.HS256, signKey).compact();
	}

}

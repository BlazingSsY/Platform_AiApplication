package com.starmol.sso.server.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.starmol.sso.server.constant.GlobalVariable;
import com.starmol.sso.server.pojo.dto.OauthToken;
import com.starmol.sso.server.pojo.dto.OauthUserAttribute;
import com.starmol.sso.server.pojo.dto.TokenAttribute;
import com.starmol.sso.server.properties.OauthProperties;
import com.starmol.sso.server.util.JwtTokenUtil;
import com.starmol.sso.server.util.NumericGeneratorUtil;
import com.starmol.sso.server.util.RandomUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class OauthGenerateService {

	@Autowired
	private OauthProperties oauthProperties;

	@Autowired
	private ObjectMapper objectMapper;

	//=====================================业务处理 start=====================================

	public OauthToken generateOauthTokenInfoBO(OauthUserAttribute userAttribute, boolean needIncludeRefreshToken) {
		final TokenAttribute tokenAttribute = objectMapper.convertValue(userAttribute, TokenAttribute.class);
		OauthToken oauthToken = new OauthToken();
		oauthToken.setAccessToken(generateAccessToken(tokenAttribute));
		if (needIncludeRefreshToken) {
			oauthToken.setRefreshToken(generateRefreshToken(tokenAttribute));
		}
		oauthToken.setTokenType(GlobalVariable.OAUTH_TOKEN_TYPE);
		oauthToken.setExpiresIn(oauthProperties.getAccessTokenMaxTimeToLiveInSeconds());

		return oauthToken;
	}

	public String generateUserInfoRedisKey(String userId) {
		return GlobalVariable.REDIS_OAUTH_USER_INFO_KEY_PREFIX + userId;
	}

	public String generateTgc() {
		return getUniqueTicket(GlobalVariable.OAUTH_TGC_PREFIX);
	}

	public String generateCode() {
		return getUniqueTicket(GlobalVariable.OAUTH_CODE_PREFIX);
	}

	public String generateAccessToken(TokenAttribute userAttribute) {
		final Map<String, Object> userMap = objectMapper.convertValue(userAttribute, TypeFactory.defaultInstance().constructMapType(HashMap.class, String.class, Object.class));
		return GlobalVariable.OAUTH_ACCESS_TOKEN_PREFIX + JwtTokenUtil.generateToken(userMap, this.oauthProperties.getTokenSignKey(), this.oauthProperties.getAccessTokenMaxTimeToLiveInSeconds());
	}

	public String generateRefreshToken(TokenAttribute userAttribute) {
		return GlobalVariable.OAUTH_REFRESH_TOKEN_PREFIX + JwtTokenUtil.generateRefreshToken(userAttribute.getId(), this.oauthProperties.getTokenSignKey(), this.oauthProperties.getRefreshTokenMaxTimeToLiveInSeconds());
	}

	//=====================================业务处理  end=====================================

	//=====================================私有方法 start=====================================

	private String getUniqueTicket(String prefix) {
		// 组成结构：前缀-节点编号+计算器数-随机数
		return prefix + oauthProperties.getNodeNumber() + NumericGeneratorUtil.getNumber() + "-" + RandomUtil.randomAlphanumeric(32);
	}

	//=====================================私有方法  end=====================================

}

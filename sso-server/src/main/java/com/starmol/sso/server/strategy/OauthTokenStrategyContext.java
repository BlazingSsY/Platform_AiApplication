package com.starmol.sso.server.strategy;

import com.starmol.sso.server.pojo.bo.handle.OauthTokenStrategyHandleBO;
import com.starmol.sso.server.pojo.dto.OauthToken;
import com.starmol.sso.server.pojo.dto.param.OauthTokenParam;
import com.starmol.sso.server.util.StringUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class OauthTokenStrategyContext {


	private final Map<String, OauthTokenStrategyInterface> strategyMap = new ConcurrentHashMap<>();

	@Autowired
	public OauthTokenStrategyContext(Map<String, OauthTokenStrategyInterface> strategyMap) {
		this.strategyMap.putAll(strategyMap);
	}

	public void checkParam(String beanName, OauthTokenParam oauthTokenParam, OauthTokenStrategyHandleBO oauthTokenStrategyHandleBO) {
		if (StringUtil.isNotBlank(beanName)) {
			strategyMap.get(beanName).checkParam(oauthTokenParam, oauthTokenStrategyHandleBO);
		}
	}

	public OauthToken generateOauthTokenInfo(String beanName, OauthTokenParam oauthTokenParam, OauthTokenStrategyHandleBO oauthTokenStrategyHandleBO) {
		if (StringUtil.isNotBlank(beanName)) {
			return strategyMap.get(beanName).handle(oauthTokenParam, oauthTokenStrategyHandleBO);
		}
		return null;
	}


}

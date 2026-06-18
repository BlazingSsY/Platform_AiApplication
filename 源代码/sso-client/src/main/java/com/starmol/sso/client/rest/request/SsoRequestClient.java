package com.starmol.sso.client.rest.request;

import com.starmol.sso.client.rest.pojo.bo.OAuth2AccessToken;
import com.starmol.sso.client.rest.pojo.bo.UserInfoPairs;
import com.starmol.sso.client.rest.pojo.dto.OauthUserProfile;
import com.starmol.sso.client.rest.pojo.dto.RoleSyncResult;
import com.starmol.sso.client.rest.pojo.dto.SsoTokenRequest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author huguojun
 */
@FeignClient(name = "SsoRequestClient", url = "${sso.oauth.client.sso-server-uri}")
public interface SsoRequestClient {

	@PostMapping(value = "/oauth/token", consumes = MediaType.APPLICATION_JSON_VALUE)
	OAuth2AccessToken reqToken(@RequestBody SsoTokenRequest ssoTokenRequest);

	@GetMapping(value = "/oauth/userinfo")
	OauthUserProfile getProfile(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String accessToken);

	@GetMapping(value = "/oauth/roleInfo", consumes = MediaType.APPLICATION_JSON_VALUE)
	RoleSyncResult getRoleInfo(@RequestHeader(name = HttpHeaders.AUTHORIZATION) String accessToken, @RequestParam("clientId") String clientId);

	@PostMapping(value = "/oauth/user-id-map", consumes = MediaType.APPLICATION_JSON_VALUE)
	List<UserInfoPairs> listUserInfos(@RequestHeader("Authorization") String token, @RequestBody List<String> userIds);

}

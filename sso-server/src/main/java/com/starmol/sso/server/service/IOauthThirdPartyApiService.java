package com.starmol.sso.server.service;

import com.starmol.sso.server.pojo.dto.OauthUserAttribute;
import com.starmol.sso.server.pojo.dto.ResponseWrapper;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

/**
 * @author huguojun
 */
public interface IOauthThirdPartyApiService {

    OauthUserAttribute getOauthUserAttributeDTO(String username, String password, String clientId, String captcha);

    ResponseEntity<ResponseWrapper<String>> updateUserPassword(String userName, String oldPassword, String password);

    ResponseEntity<InputStreamResource> generateCaptcha();
}

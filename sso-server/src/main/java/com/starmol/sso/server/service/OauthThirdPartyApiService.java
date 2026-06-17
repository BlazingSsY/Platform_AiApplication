package com.starmol.sso.server.service;


import com.starmol.sso.server.constant.GlobalVariable;
import com.starmol.sso.server.enums.ResponseProduceTypeEnum;
import com.starmol.sso.server.exception.OauthApiException;
import com.starmol.sso.server.pojo.dto.OauthUserAttribute;
import com.starmol.sso.server.pojo.dto.ResponseWrapper;
import com.starmol.sso.server.util.CookieUtil;
import com.starmol.sso.server.util.feign.ManagementRequest;
import com.starmol.sso.server.util.feign.param.UpdatePasswordDTO;
import com.starmol.sso.server.util.feign.param.UserLoginRequest;
import com.starmol.sso.server.util.feign.param.UserLoginResponse;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class OauthThirdPartyApiService implements IOauthThirdPartyApiService {

    private static final String COOKIE_CAPTCHA_KEY = "CAPTCHA_ID";
    @Autowired
    private ManagementRequest managementService;

    @Override
    public OauthUserAttribute getOauthUserAttributeDTO(String username, String password, String clientId, String captcha) {
        final UserLoginRequest userLoginRequest = new UserLoginRequest()
                .setLoginName(username)
                .setPassword(password)
                .setClientId(clientId)
                .setCaptcha(captcha);

        String ip = null;
        String port = null;
        String captchaId = null;
        final ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            ip = requestAttributes.getRequest().getHeader("X-Real-IP");
            port = requestAttributes.getRequest().getHeader("X-Real-PORT");
            captchaId = CookieUtil.getCookie(requestAttributes.getRequest(), COOKIE_CAPTCHA_KEY);
        }

        final UserLoginResponse login = managementService.login(captchaId, ip, port, userLoginRequest);
        if (login.isFailed()) {
            if (BooleanUtils.isTrue(Objects.equals(login.getCode(), GlobalVariable.OAUTH_PASSWORD_EXPIRED_CODE))) {
                return new OauthUserAttribute().setPasswordExpired(true);
            } else {
                throw new OauthApiException(login.getMsg(), ResponseProduceTypeEnum.HTML, GlobalVariable.DEFAULT_LOGIN_PAGE_PATH);
            }
        }

        log.debug("login response: {}", login);
        final OauthUserAttribute oauthUserAttribute = new OauthUserAttribute();
        oauthUserAttribute.setPowerAliasTree(login.getContent().getPowerAliasTree());
        oauthUserAttribute.setId(login.getUserId());
        oauthUserAttribute.setLoginName(login.getUserName());
        oauthUserAttribute.setName(login.getContent().getName());
        oauthUserAttribute.setUserPhone(login.getPhone());
        oauthUserAttribute.setDepartmentId(login.getContent().getDepartmentId());
        oauthUserAttribute.setDepartmentName(login.getContent().getDepartmentName());
        oauthUserAttribute.setClientIds(login.getContent().getClientIds());
        oauthUserAttribute.setRoleId(login.getContent().getRoleId());
        oauthUserAttribute.setRoleName(login.getContent().getRoleName());
        oauthUserAttribute.setProfile(login.getContent().getProfile());
        return oauthUserAttribute;
    }

    @Override
    public ResponseEntity<ResponseWrapper<String>> updateUserPassword(String userName, String oldPassword, String password) {

        final UpdatePasswordDTO updatePasswordDTO = new UpdatePasswordDTO()
                .setLoginName(userName)
                .setOldPassword(oldPassword)
                .setPassword(password);
        ResponseWrapper<String> responseWrapper = managementService.updatePassword(updatePasswordDTO);
        return ResponseEntity.ok(responseWrapper);
    }

    @Override
    public ResponseEntity<InputStreamResource> generateCaptcha() {
        return managementService.captchaGenerate();
    }

}

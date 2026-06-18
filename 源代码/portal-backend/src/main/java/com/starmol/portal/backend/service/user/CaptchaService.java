package com.starmol.portal.backend.service.user;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Yuexiaopeng
 */
public interface CaptchaService {

    void generate(HttpServletRequest request, HttpServletResponse response);

    ResponseEntity<InputStreamResource> generate(HttpServletResponse response);

    void verify(HttpServletResponse response, String captchaId, String captcha);

    String getCaptchaId(HttpServletRequest request);
}

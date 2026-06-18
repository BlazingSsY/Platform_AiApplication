package com.starmol.portal.backend.controller.user;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.starmol.portal.backend.aop.Permit;
import com.starmol.portal.backend.common.Permission;
import com.starmol.portal.backend.service.user.CaptchaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/v1/captcha")
@Tag(description = "验证码", name = "验证码")
public class CaptchaController {
    private CaptchaService captchaService;

    public CaptchaController(CaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    /**
     * 用于生成带四位数字验证码的图片
     */
    @GetMapping
    @Operation(summary = "获取验证码")
    @Permit(Permission.ALL)
    public ResponseEntity<InputStreamResource> imageCode(HttpServletResponse response) {
        return captchaService.generate(response);
    }

    /**
     * 用于生成带四位数字验证码的图片
     */
    @GetMapping(value = "/generate")
    @Operation(summary = "获取验证码")
    @Permit(Permission.ALL)
    public void imageCode(HttpServletRequest request, HttpServletResponse response) {
        captchaService.generate(request, response);
    }
}
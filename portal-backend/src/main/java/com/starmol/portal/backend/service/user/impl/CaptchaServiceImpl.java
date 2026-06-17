package com.starmol.portal.backend.service.user.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.starmol.portal.backend.bean.bo.ImageCode;
import com.starmol.portal.backend.exception.KnowException;
import com.starmol.portal.backend.service.user.CaptchaService;
import com.starmol.portal.backend.service.user.ImageCodeGenerator;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Yuexiaopeng
 */
@Service
@Slf4j
public class CaptchaServiceImpl implements CaptchaService {
   private final String CAPTCHA_ID = "CAPTCHA_ID";
   private final String CAPTCHA_CACHE_KEY = CAPTCHA_ID + ":";

    final Cache<String, String> CACHE = CacheBuilder.newBuilder().maximumSize(100L).expireAfterWrite(5, TimeUnit.MINUTES).<String, String>build();
    private ImageCodeGenerator imageCodeGenerator;

    public CaptchaServiceImpl(ImageCodeGenerator imageCodeGenerator) {
        this.imageCodeGenerator = imageCodeGenerator;
    }

    private Cookie generateCookie(long captchaId) {
        final Cookie cookie = new Cookie(CAPTCHA_ID, Long.toString(captchaId));
        //cookie.setSecure(true);
        cookie.setHttpOnly(true);
        return cookie;
    }

    @SneakyThrows
    @Override
    public ResponseEntity<InputStreamResource> generate(HttpServletResponse response) {
        final long captchaId = IdWorker.getId();
        final Cookie cookie = generateCookie(captchaId);
        response.addCookie(cookie);

        final ImageCode captcha = imageCodeGenerator.generate();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(captcha.getImage(), "png", os);
        InputStream inputStream = new ByteArrayInputStream(os.toByteArray());
        CACHE.put(CAPTCHA_CACHE_KEY + captchaId, captcha.getCode());
        return ResponseEntity.ok()
                .header(HttpHeaders.EXPIRES, "0")
                .cacheControl(CacheControl.noCache())
                .contentType(MediaType.IMAGE_PNG).body(new InputStreamResource(inputStream));
    }

    @SneakyThrows
    @Override
    public void generate(HttpServletRequest request, HttpServletResponse response) {
        final MediaType imagePng = MediaType.IMAGE_PNG;
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType(imagePng.toString());

        final String captchaId = String.valueOf(IdWorker.getId());
        final Cookie cookie = new Cookie(CAPTCHA_ID, captchaId);
        response.addCookie(cookie);

        final ImageCode captcha = imageCodeGenerator.generate();
        try (OutputStream os = response.getOutputStream()) {

            final boolean success = ImageIO.write(captcha.getImage(), imagePng.getSubtype(), os);
            if (BooleanUtils.isFalse(success)) {
                throw new KnowException("write image to output failed.");
            }
        }
    }

    @SneakyThrows
    @Override
    public void verify(HttpServletResponse response, String captchaId, String captcha) {

        if (StringUtils.isBlank(captchaId)) {
            throw new KnowException("captcha ID is not provide");
        }

        final String captchaInCache = CACHE.get(CAPTCHA_CACHE_KEY + captchaId, () -> "");
        if (isNotEquals(captcha, captchaInCache)) {
            throw new KnowException("验证码不正确");
        }
        // 使得当前ID的CACHE的值无效
        CACHE.invalidate(CAPTCHA_CACHE_KEY + captchaId);
//这里不能清除Cookie, 清除后会导致第二次无法登录
//        final Cookie cookie = new Cookie(CAPTCHA_ID, null);
//        cookie.setMaxAge(-1);
//        response.addCookie(cookie);
    }

    @Override
    public String getCaptchaId(HttpServletRequest request) {
        final Cookie tokenCookie = Optional.ofNullable(WebUtils.getCookie(request, CAPTCHA_ID)).orElse(new Cookie(CAPTCHA_ID, ""));
        final String value = tokenCookie.getValue();
        if (StringUtils.isBlank(value)) {
            throw new KnowException("captcha ID is not provide");
        }
        return value;
    }

    private boolean isNotEquals(String captcha, String captchaInRedis) {
        return !StringUtils.equalsIgnoreCase(captchaInRedis, captcha);
    }
}

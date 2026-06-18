package com.starmol.portal.backend.common;


import java.util.regex.Pattern;

public interface GlobalVariable {
    String CAPTCHA_ID = "CAPTCHA_ID";
    String CAPTCHA_CACHE_KEY = CAPTCHA_ID + ":";
    Pattern PHONE_REGEXP = Pattern.compile("^1[3456789]\\d{9}$");
}

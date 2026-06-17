package com.starmol.sso.server.util;

import javax.servlet.http.HttpServletRequest;

import cn.hutool.extra.servlet.ServletUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class IPUtil {

    public static String getIp(HttpServletRequest request) {
        return ServletUtil.getClientIP(request);
    }
}

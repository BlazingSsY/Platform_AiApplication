package com.starmol.sso.server.util;

import org.apache.commons.codec.binary.Base64;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class CodecUtil {

    public static String base64DecodeBySafe(final String base64String) {
        return new String(Base64.decodeBase64(base64String));
    }

}

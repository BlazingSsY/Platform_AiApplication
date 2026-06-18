package com.starmol.sso.client.rest.utils;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public final class CodecUtil {

	//编码
	@SneakyThrows
	public static String encodeURL(String source) {
		return URLEncoder.encode(source, StandardCharsets.UTF_8.name());
	}

	//解码
	@SneakyThrows
	public static String decodeURL(String source) {
		return URLDecoder.decode(source, StandardCharsets.UTF_8.name());
	}

}

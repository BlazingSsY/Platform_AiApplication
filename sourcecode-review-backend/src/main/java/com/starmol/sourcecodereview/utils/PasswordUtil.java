package com.starmol.sourcecodereview.utils;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Random;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PasswordUtil {

    public final static String INITIALIZE_PSW = "123456";

    private static final Integer DEFAULT_SALT_LENGTH = 16;
    private static final Integer DEFAULT_MD5_LENGTH = 32;

    /**
     * 基于传入的密码字符串生成SHA-256的hash
     */
    public static String generateHash(String loginName, String password) {
        return generate(password);
    }

    /**
     * 校验加盐后是否和原文一致
     *
     * @param password 用户提交上来的密码md5字符串
     * @param md5      数据库中已经加密过的md5值
     * @return boolean
     */
    public static boolean verify(String password, String md5) {
        if (StringUtils.isBlank(password) || StringUtils.isBlank(md5)) {
            return false;
        }
        char[] cs1 = new char[32];
        char[] cs2 = new char[16];
        for (int i = 0; i < 48; i += 3) {
            cs1[i / 3 * 2] = md5.charAt(i);
            cs1[i / 3 * 2 + 1] = md5.charAt(i + 2);
            cs2[i / 3] = md5.charAt(i + 1);
        }
        String salt = new String(cs2);
        String md5HexVal = md5Hex(password + salt);
        return md5HexVal != null && md5HexVal.equals(new String(cs1));
    }

    /**
     * 获取十六进制字符串形式的MD5摘要
     */
    public static String md5Hex(String src) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bs = md5.digest(src.getBytes());
            return new String(new Hex().encode(bs));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 生成加盐MD5 32+16 位
     *
     * @param password 待加密密码
     * @return String
     */
    public static String generate(String password) {
        Random r = new Random();
        StringBuilder sb = new StringBuilder(16);
        String salt = byteToHexString(generateSalt(DEFAULT_SALT_LENGTH));
        sb.append(salt);
        int len = sb.length();
        if (len < 16) {
            for (int i = 0; i < 16 - len; i++) {
                sb.append("0");
            }
        } else {
            salt = sb.substring(0, 16);
        }
//        System.out.println(salt);
        password = md5Hex(password + salt);
//        System.out.println(password);
        if (password == null) {
            return null;
        }
        char[] cs = new char[48];
        for (int i = 0; i < 48; i += 3) {
            cs[i] = password.charAt(i / 3 * 2);
            char c = salt.charAt(i / 3);
            cs[i + 1] = c;
            cs[i + 2] = password.charAt(i / 3 * 2 + 1);
        }
        return new String(cs);
    }

    /**
     * 安全生成指定位数盐值
     *
     * @param saltLength 盐值长度
     * @return byte[]
     */
    private static byte[] generateSalt(Integer saltLength) {
        if (saltLength == null || saltLength <= DEFAULT_SALT_LENGTH) {
            saltLength = DEFAULT_SALT_LENGTH;
        }
        if (saltLength >= DEFAULT_MD5_LENGTH) {
            saltLength = DEFAULT_MD5_LENGTH;
        }
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[saltLength];
        random.nextBytes(bytes);
        return bytes;
    }

    /**
     * 将指定byte数组转换成16进制字符串
     *
     * @param b 字节数组
     * @return String
     */
    public static String byteToHexString(byte[] b) {
        StringBuilder hexString = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            hexString.append(hex.toUpperCase());
        }
        return hexString.toString();
    }

}

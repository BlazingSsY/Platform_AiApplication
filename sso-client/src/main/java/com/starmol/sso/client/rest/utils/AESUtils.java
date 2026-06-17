package com.starmol.sso.client.rest.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public class AESUtils {

    private static final String ALG = "AES";
    private static final String SEC_NORMALIZE_ALG = "MD5";

    public static String encrypt(String secret, String data) throws Exception {
        MessageDigest dig = MessageDigest.getInstance(SEC_NORMALIZE_ALG);
        byte[] key = dig.digest(secret.getBytes(StandardCharsets.UTF_8));
        SecretKeySpec secKey = new SecretKeySpec(key, ALG);

        Cipher aesCipher = Cipher.getInstance(ALG);
        byte[] byteText = data.getBytes(StandardCharsets.UTF_8);

        aesCipher.init(Cipher.ENCRYPT_MODE, secKey);
        byte[] byteCipherText = aesCipher.doFinal(byteText);
        return new String(Base64.getEncoder().encode(byteCipherText));
    }

    public static String decrypt(String secret, String ciphertext) throws Exception {
        MessageDigest dig = MessageDigest.getInstance(SEC_NORMALIZE_ALG);
        byte[] key = dig.digest(secret.getBytes(StandardCharsets.UTF_8));
        SecretKeySpec secKey = new SecretKeySpec(key, ALG);

        Cipher aesCipher = Cipher.getInstance(ALG);
        aesCipher.init(Cipher.DECRYPT_MODE, secKey);
        byte[] cipherBytes = Base64.getDecoder().decode(ciphertext.getBytes());
        byte[] bytePlainText = aesCipher.doFinal(cipherBytes);
        return new String(bytePlainText, StandardCharsets.UTF_8);
    }

}
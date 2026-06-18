package com.starmol.logicreview.utils;


import org.apache.commons.codec.binary.Base64;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 生成uuid
 *
 * @author: Yuexiaopeng
 * @date 2019/11/8
 **/
public class UUIDUtils {
    static final long NUM_100NS_INTERVALS_SINCE_UUID_EPOCH = 0x01b21dd213814000L;
    static final long CONST_SALT = -4974913230128461891L;


    /**
     * 将传入Long类型的id参数转换成UUID
     *
     * @param id: 需要转化的id
     * @return : uuid
     */
    public static String getUUIDFromLong(Long id) {
        long rawTimestamp = id;
        int clockHi = (int) (rawTimestamp >>> 32);
        int clockLo = (int) rawTimestamp;
        int midhi = clockHi << 16 | clockHi >>> 16;
        midhi &= -61441;
        midhi |= 4096;
        long midhiL = (long) midhi;
        midhiL = midhiL << 32 >>> 32;
        long l1 = (long) clockLo << 32 | midhiL;

        UUID uuid = new UUID(l1, CONST_SALT);
        return compressedUUID(uuid);
    }

    /**
     * 生成随机的UUID
     *
     * @return : uuid
     */
    public static String generateRandomUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 用于解密基于时间的UUID
     *
     * @return : long型的时间
     */
    private static long getTimeFromUUID(UUID uuid) {
        return (uuid.timestamp() - NUM_100NS_INTERVALS_SINCE_UUID_EPOCH) / 10000;
    }

    /**
     * 为UUID字符串添加-
     *
     * @return : long型的时间
     */
    private static String addsUUIDSlitter(String trimmedUUIDStr) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(trimmedUUIDStr.substring(0, 8));
        stringBuilder.append("-");
        stringBuilder.append(trimmedUUIDStr.substring(8, 12));
        stringBuilder.append("-");
        stringBuilder.append(trimmedUUIDStr.substring(12, 16));
        stringBuilder.append("-");
        stringBuilder.append(trimmedUUIDStr.substring(16, 20));
        stringBuilder.append("-");
        stringBuilder.append(trimmedUUIDStr.substring(20, 32));
        return stringBuilder.toString();
    }

    /**
     * 生成随机的UUID
     *
     * @return : uuid
     */


    /**
     * 将基于时间的UUID字符串,转换为直径的时间格式
     *
     * @return : 生成的"yyyy-MM-dd HH:mm:ss"格式的字符串
     */
    public static String decodTimeBasedUUIDToDateTimeStr(String decodingUUIDStr) {
        String formatedUUIDStr = addsUUIDSlitter(decodingUUIDStr);
        UUID uuid = UUID.fromString(formatedUUIDStr);
        long time = getTimeFromUUID(uuid);
        Date date = new Date(time);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
    }

    /**
     * 将uuid压缩后生成长度为22的字符串
     *
     * @return : 压缩后长度为22的字符串
     */
    protected static String compressedUUID(UUID uuid) {
        byte[] byUuid = new byte[16];
        long least = uuid.getLeastSignificantBits();
        long most = uuid.getMostSignificantBits();
        long2bytes(most, byUuid, 0);
        long2bytes(least, byUuid, 8);
        String compressUUID = Base64.encodeBase64URLSafeString(byUuid);
        return compressUUID;
    }

    private static void long2bytes(long value, byte[] bytes, int offset) {
        for (int i = 7; i > -1; i--) {
            bytes[offset++] = (byte) ((value >> 8 * i) & 0xFF);
        }
    }

    /**
     * 将长度为22的压缩uuid字符串解压为普通的UUID
     *
     * @return : 普通的uuid字符串
     */
    public static UUID uncompress(String compressedUuid) {
        if (compressedUuid.length() != 22) {
            throw new IllegalArgumentException("Invalid uuid!");
        }
        byte[] byUuid = Base64.decodeBase64(compressedUuid + "==");
        long most = bytes2long(byUuid, 0);
        long least = bytes2long(byUuid, 8);
        UUID uuid = new UUID(most, least);
        return uuid;
    }

    private static long bytes2long(byte[] bytes, int offset) {
        long value = 0;
        for (int i = 7; i > -1; i--) {
            value |= (((long) bytes[offset++]) & 0xFF) << 8 * i;
        }
        return value;
    }

    /**
     * 将基于时间的压缩的UUID字符串,转换为直径的时间格式
     *
     * @return : 生成的"yyyy-MM-dd HH:mm:ss"格式的字符串
     */
    public static String decodTimeBasedCompressedUUIDToDateTimeStr(String decodingUUIDStr) {
        UUID uuid = uncompress(decodingUUIDStr);
        long time = getTimeFromUUID(uuid);
        Date date = new Date(time);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
    }

    /**
     * 用于测试UUID的逻辑
     public static void main(String[] args) {
     try {
     String uuidStr =  UUIDUtils.getUUID();
     String dataTimeStr = UUIDUtils.decodTimeBasedCompressedUUIDToDateTimeStr(uuidStr);
     System.out.println(dataTimeStr);

     String uuidl =  UUIDUtils.getUUIDFromLong(20L);
     UUID uuid = uncompress(uuidl);
     long rawId = uuid.timestamp();
     System.out.println(rawId);
     }
     catch (Exception e ){
     e.printStackTrace();
     }
     }
     */
}

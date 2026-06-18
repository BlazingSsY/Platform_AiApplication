package com.starmol.sourcecodereview.utils;

import com.starmol.sourcecodereview.model.base.DeleteDTO;

import org.springframework.util.StringUtils;

import java.security.MessageDigest;
import java.util.Objects;
import java.util.Random;

public class StringUtil {
    public static String str;
    public static final String EMPTY_STRING = "";

    private final static String[] HEX_DIGITS = { "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n = 256 + n;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return HEX_DIGITS[d1] + HEX_DIGITS[d2];
    }

    /**
     * 转换字节数组为16进制字串
     * @param b 字节数组
     * @return 16进制字串
     */
    public static String byteArrayToHexString(byte[] b) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            resultSb.append(byteToHexString(b[i]));
        }
        return resultSb.toString();
    }

    /**
     * 生成MD5加密后字符串
     * @param origin
     * @return
     */
    public static String mD5Encode(String origin) {
        String resultString = null;
        try {
            resultString = new String(origin);
            MessageDigest md = MessageDigest.getInstance("MD5");
            resultString = byteArrayToHexString(md.digest(resultString
                    .getBytes()));
        } catch (Exception ex) {
        }
        return resultString;
    }

    /**
     * 验证参数不可空统一方法
     * @param args 可变长参数
     * @return
     */
    public static boolean checkStrs(String... args){
        for(String str:args){
            if(StringUtils.isEmpty(str) || null == str){
                return false;
            }
        }
        return true;
    }

    /**
     * 验证邮箱
     * @param email
     * @return
     */
    public static boolean isEmail(String email){
        String reg = "^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$";
        return email.matches(reg);
    }

    /**
     * @Title: isMobileNo
     * @Description: 是否为一个手机号码
     * @param @param str
     * @return boolean 返回类型
     */
    public static boolean isMobileNo(String str){
        String phoneRegex = "[1][3578]\\d{9}";
        String telRegex = "(0[0-9]{2,3}-)?([2-9][0-9]{6,7})+(-[0-9]{1,4})?";
        return str.matches(phoneRegex) || str.matches(telRegex);

    }

    /**
     * 生成随机数
     * @param num 生成随机数的位数
     * @return
     */
    public static String buildValidateCode(int num){
        Random random = new Random();
        String result = "";
        for (int i=0; i<num; i++){
            result+=random.nextInt(10);
        }
        return result;

    }

    /**
     * 生成随机字符串
     * @param num  生成随机字符串的位数
     * @return
     */
    public static String randomStr(int num){
        String code = "";
        for(int i=0; i<num; i++){
            code += randomChar();
        }
        return code;
    }

    private static char randomChar(){
        Random r = new Random();
        String s = "ABCDEFGHJKLMNPRSTUVWXYZ0123456789";
        return s.charAt(r.nextInt(s.length()));
    }

    /**
     * 字符串长度大于指定长度时多余的字符用“...”代替
     * @return
     */
    public static String cutString(String str,int len){
        if(str.length()>len){
            String substring = str.substring(0, len);
            substring+="...";
            return substring;
        }else {
            return str;
        }
    }

    /**
     * 通过DeleteDTO对象获取对象日志描述
     * @return 对象日志描述
     */
    public static String getLogDescription(DeleteDTO deleteDTO) {
        String readableLabel = Objects.isNull(deleteDTO.getLabel()) ? " " : deleteDTO.getLabel();
        String readableId = Objects.isNull(deleteDTO.getId()) ? EMPTY_STRING : deleteDTO.getId().toString();
        if (EMPTY_STRING.equals(readableId)){
            return String.format("%s", readableLabel);
        }
        else{
            return String.format("%s(ID:%s)", readableLabel, readableId);
        }
    }

    /**
     * 通过id和对象获取对象日志描述
     * @return 对象日志描述
     */
    public static String getLogDescription(Long id, String label) {
        String readableLabel = Objects.isNull(label) ? " " : label;
        String readableId = Objects.isNull(id) ? " " : id.toString();
        if (EMPTY_STRING.equals(readableId)){
            return String.format("%s", readableLabel);
        }
        else{
            return String.format("%s(ID:%s)", readableLabel, readableId);
        }
    }

    /**
     * 功能：驼峰命名转下划线命名
     * 小写和大写紧挨一起的地方,加上分隔符,然后全部转小写
     * @param name
     * @return 转换后的结果
     */
    public static String camelToUnder(String name)
    {
        String separator = "_";
        name = name.replaceAll("([a-z])([A-Z])", "$1"+separator+"$2").toLowerCase();
        return name;
    }


    /**
     * 功能：下划线命名转驼峰命名
     * 将下划线替换为空格,将字符串根据空格分割成数组,再将每个单词首字母大写
     * @param name
     * @return 转换后的结果
     */
    public static String underToCamel(String name) {
        String separator = "_";
        String under = "";
        name = name.toLowerCase().replace(separator, " ");
        String sarr[] = name.split(" ");
        if (sarr.length > 0) {
            under += sarr[0];
            for (int i = 1; i < sarr.length; i++) {
                String w = sarr[i].substring(0, 1).toUpperCase() + sarr[i].substring(1);
                under += w;
            }
        }
        return under;
    }
}

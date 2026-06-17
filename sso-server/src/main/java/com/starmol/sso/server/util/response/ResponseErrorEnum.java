package com.starmol.sso.server.util.response;

/**
 * 微信：https://mp.weixin.qq.com/wiki?action=doc&id=mp1433747234 钉钉：https://open-doc.dingtalk.com/microapp/faquestions/rftpfg
 */
public enum ResponseErrorEnum {

    //系统相关错误
    SYSTEM_BUSY(10000, "系统繁忙，请稍候重试"),
    ILLEGAL_STATE(10001, "非法访问"),
    PARAM_REQUIRED(10002, "参数不能为空"),
    PARAM_FORMAT_ILLEGAL(10003, "参数格式错误"),
    REQUEST_DATA_DUPLICATION(10004, "重复请求"),
    REQUEST_DATA_ERROR(10005, "请求数据错误"),
    REQUEST_DATA_NOT_MATCH(10006, "请求数据不一致"),
    RECORD_NOT_EXIST(10007, "数据不存在"),
    RECORD_EXISTED(10008, "数据已存在"),
    RECORD_ILLEGAL_STATE(10009, "数据异常"),
    CALL_INNER_ERROR(10010, "调用内部服务接口异常"),
    THIRD_PART_ERROR(10011, "调用第三方接口异常"),
    SYSTEM_ERROR(99999, "系统异常"),

    // 业务相关错误
    TGC_INVALID(10100, "TGC无效，请重新登录."),
    LOGIN_ERROR(10101, "用户名密码验证失败");

    private int code;
    private String description;

    ResponseErrorEnum(final int code, final String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

}

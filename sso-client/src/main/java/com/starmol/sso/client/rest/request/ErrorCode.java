package com.starmol.sso.client.rest.request;

/**
 * @author huguojun
 */
public interface ErrorCode {
    public static final Integer SYSTEM_ERROR = 99999;// "系统异常"
    public static final Integer SYSTEM_BUSY = 10000;// "系统繁忙，请稍候重试"
    public static final Integer ILLEGAL_STATE = 10001;// "非法访问"
    public static final Integer PARAM_REQUIRED = 10002;// "参数不能为空"
    public static final Integer PARAM_FORMAT_ILLEGAL = 10003;// "参数格式错误"
    public static final Integer REQUEST_DATA_DUPLICATION = 10004;// "重复请求"
    public static final Integer REQUEST_DATA_ERROR = 10005;// "请求数据错误"
    public static final Integer REQUEST_DATA_NOT_MATCH = 10006;// "请求数据不一致"
    public static final Integer RECORD_NOT_EXIST = 10007;// "数据不存在"
    public static final Integer RECORD_EXISTED = 10008;// "数据已存在"
    public static final Integer RECORD_ILLEGAL_STATE = 10009;// "数据异常"
    public static final Integer CALL_INNER_ERROR = 10010;// "调用内部服务接口异常"
    public static final Integer THIRD_PART_ERROR = 10011;// "调用第三方接口异常"
}

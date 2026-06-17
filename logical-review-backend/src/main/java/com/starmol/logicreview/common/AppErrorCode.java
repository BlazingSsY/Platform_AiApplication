package com.starmol.logicreview.common;


import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * @author huguojun
 */
public class AppErrorCode {

    public final static ErrorBody SERVER_INTERNAL_ERROR = new ErrorBody().setCode(99999).setMsg("服务器内部错误");
    public final static ErrorBody PARAM_REQUIRED = new ErrorBody().setCode(99998).setMsg("参数错误");
    public final static ErrorBody API_FREQ_ERROR = new ErrorBody().setCode(99997).setMsg("请求过于频繁");
    public final static ErrorBody NOT_FOUND = new ErrorBody().setCode(99996).setMsg("资源不存在");

    public final static ErrorBody ENT_NAME_OR_DOMAIN_EXISTS = new ErrorBody().setCode(10001).setMsg("企业名称或域名前缀已存在");
    public final static ErrorBody PHONE_NUMBER_ALL_READY_USED = new ErrorBody().setCode(10002).setMsg("电话号码已存在");
    public final static ErrorBody ENT_NOT_EXISTS = new ErrorBody().setCode(10003).setMsg("企业不存在");
    public static final ErrorBody USER_NOT_FOUND = new ErrorBody().setCode(10004).setMsg("用户不存在");
    public static final ErrorBody RECORD_NOT_FOUND_ERROR = new ErrorBody().setCode(10005).setMsg("请求数据条目不存在");
    public final static ErrorBody USER_ROLE_NOT_FOUND = new ErrorBody().setCode(10006).setMsg("用户角色未找到");
    public static final ErrorBody NOT_LEAF_DEPARTMENT = new ErrorBody().setCode(10007).setMsg("非叶子节点不允许添加人员");
    public static final ErrorBody USER_EXISTS_ERROR = new ErrorBody().setCode(10008).setMsg("用户电话号码已存在");
    public final static ErrorBody LOGIN_PASSWORD_EXPIRED_ERROR = new ErrorBody().setCode(10009).setMsg("密码过期");
    public static final ErrorBody NOT_AUTHORIZED = new ErrorBody().setCode(10010).setMsg("权限不足");


    // 组织
    public static final ErrorBody ORGANIZATION_NOT_FOUND = new ErrorBody().setCode(20000).setMsg("企业不存在");
    public static final ErrorBody DEPARTMENT_NOT_FOUND = new ErrorBody().setCode(20001).setMsg("部门不存在");
    public static final ErrorBody DEPARTMENT_HAS_MEMBERS = new ErrorBody().setCode(20002).setMsg("部门下有人员");
    public static final ErrorBody DEPARTMENT_NAME_REPEAT = new ErrorBody().setCode(20003).setMsg("部门名称重复");
    public static final ErrorBody DEPARTMENT_PARENT_HAS_STAFF = new ErrorBody().setCode(20004).setMsg("部门下有人员，无法创建下级");
    public static final ErrorBody EXCEL_GENERATE_FAILED = new ErrorBody().setCode(20101).setMsg("数据导出出错");
    public static final ErrorBody IMPORT_DATA_IS_EMPTY_ERROR = new ErrorBody().setCode(20102).setMsg("导入数据为空");
    public static final ErrorBody IMPORT_DATA_LAYER_ILLEGAL = new ErrorBody().setCode(20102).setMsg("有用户的部门级别不合法");
    public static final ErrorBody IMPORT_DATA_USERNAME_ILLEGAL = new ErrorBody().setCode(20103).setMsg("有用户的姓名列不合法");
    public static final ErrorBody IMPORT_DATA_TELEPHONE_ILLEGAL = new ErrorBody().setCode(20104).setMsg("有用户的电话不合法");
    public static final ErrorBody IMPORT_DATA_WORK_ILLEGAL = new ErrorBody().setCode(20105).setMsg("有用户的工号不合法");
    public static final ErrorBody IMPORT_DATA_TELEPHONE_REPEAT_ERROR = new ErrorBody().setCode(20106).setMsg("有用户的电话重复");
    public static final ErrorBody IMPORT_DATA_FILENAME_EMPTY_ERROR = new ErrorBody().setCode(20107).setMsg("文件名为空");
    public static final ErrorBody IMPORT_DATA_QUESTION_EMPTY_ERROR = new ErrorBody().setCode(20108).setMsg("问题为空");
    public static final ErrorBody IMPORT_DATA_NEED_ANSWER_EMPTY_ERROR = new ErrorBody().setCode(20108).setMsg("是否需要回答为空");
    public static final ErrorBody IMPORT_DATA_ANSWER_EMPTY_ERROR = new ErrorBody().setCode(20109).setMsg("该条目需要回答，但是回答内容为空");
    public static final ErrorBody IMPORT_DATA_TYPE_EMPTY_ERROR = new ErrorBody().setCode(20110).setMsg("类型为空");
    public static final ErrorBody IMPORT_DATA_RESOURCE_TYPE_EMPTY_ERROR = new ErrorBody().setCode(20110).setMsg("资源类型为空");
    public static final ErrorBody IMPORT_DATA_IMAGE_TYPE_CAN_NOT_ANSWER = new ErrorBody().setCode(20111).setMsg("图片类型提示不需要回答");
    public static final ErrorBody IMPORT_DATA_HAS_NOT_LEAF_DEPARTMENT = new ErrorBody().setCode(20112).setMsg("导入人员有非叶子节点部门人员");

    @Setter
    @Getter
    @Accessors(chain = true)
    public static class ErrorBody {
        private String msg;
        private Integer code;
    }
}

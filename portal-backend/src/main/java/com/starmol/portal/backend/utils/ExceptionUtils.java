package com.starmol.portal.backend.utils;

import com.starmol.portal.backend.exception.KnowException;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.NoRouteToHostException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLSyntaxErrorException;

public class ExceptionUtils {
    /**
     * 公共异常类 报错信息
     */
    public final static String ARITHMETIC_EXCEPTION_MSG = "异常的运算条件:例如，一个整数\"除以零\"";
    public final static String ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION_MSG = "用非索引访问数组异常: 例如,索引为负或大于等于数组大小";
    public final static String ARRAY_STORE_EXCEPTION_MSG = "错误类型的对象存储到一个对象数异常";
    public final static String CLASS_CAST_EXCEPTION_MSG = "对象强制转换为不是实例的子类异常";
    public final static String ILLEGAL_ARGUMENT_EXCEPTION_MSG = "方法传递了一个不合法或不正确的参数";
    public final static String ILLEGAL_MONITOR_STATE_EXCEPTION_MSG = "当前的线程不是此对象锁的所有者,却调用该对象的notify()，notify()，wait()方法";
    public final static String ILLEGAL_STATE_EXCEPTION_MSG = "在非法或不适当的时间调用方法时产生的信号";
    public final static String ILLEGAL_THREAD_STATE_EXCEPTION_MSG = "线程没有处于请求操作所要求的适当状态异常";
    public final static String INDEX_OUT_OF_BOUNDS_EXCEPTION_MSG = "排序索引（例如对数组、字符串或向量的排序）超出范围时抛出";
    public final static String NEGATIVE_ARRAY_SIZE_EXCEPTION_MSG = "应用程序试图创建大小为负的数组";
    public final static String NULL_POINTER_EXCEPTION_MSG = "应用程序试图在需要对象的地方使用 null";
    public final static String NUMBER_FORMAT_EXCEPTION_MSG = "应用程序试图将字符串转换成一种数值类型，但该字符串不能转换为适当格式";
    public final static String SECURITY_EXCEPTION_MSG = "安全管理器抛出的异常，可能存在安全侵犯";
    public final static String STRING_INDEX_OUT_OF_BOUNDS_EXCEPTION_MSG = "String操作异常: 索引或者为负，或者超出字符串的大小";
    public final static String UNSUPPORTED_OPERATION_EXCEPTION_MSG = "不支持当前请求的操作";

    public final static String CLASS_NOT_FOUND_EXCEPTION_MSG = "应用程序试图加载类时，找不到相应的类";
    public final static String CLONE_NOT_SUPPORTED_EXCEPTION_MSG = "调用 Object 类中的 clone 方法克隆对象，但该对象的类无法实现 Cloneable 接口";
    public final static String ILLEGAL_ACCESS_EXCEPTION_MSG = "拒绝访问一个类的时候";
    public final static String INSTANTIATION_EXCEPTION_MSG = "试图使用 Class 类中的 newInstance 方法创建一个类的实例，而指定的类对象因为是一个接口或是一个抽象类而无法实例化";
    public final static String INTERRUPTED_EXCEPTION_MSG = "一个线程被另一个线程中断异常";
    public final static String NO_SUCH_FIELD_EXCEPTION_MSG = "请求的变量不存在";
    public final static String NO_SUCH_METHOD_EXCEPTION_MSG = "请求的方法不存在";

    /**
     * 新添加异常类 报错信息
     */
    public final static String NO_ROUTE_TO_HOST_EXCEPTION_MSG = "服务器无法正常访问网络";
    public final static String SQL_INTEGRITY_CONSTRAINT_VIOLATION_EXCEPTION_MSG = "违反唯一约束条件";
    public final static String SQL_EXCEPTION_MSG = "操作数据库异常";
    public final static String SQL_SYNTAX_ERROR_EXCEPTION_MSG = "插入数据值过多";
    public final static String IO_EXCEPTION_MSG = "输入输出异常";
    public final static String EOF_EXCEPTION_MSG = "文件已结束";
    public final static String FILE_NOT_FOUND_EXCEPTION_MSG = "文件未找到";
    public final static String CLASS_CIRCULARITY_ERROR_MSG = "类循环依赖错误";
    public final static String OUT_OF_MEMORY_ERROR_MSG = "内存不足";
    public final static String INCOMPATIBLE_CLASS_CHANGE_ERROR_MSG = "不兼容的类变化错误";
    public final static String INSTANTIATION_ERROR_MSG = "实例化错误";
    public final static String LINKAGE_ERROR_MSG = "链接错误";
    public final static String STACK_OVERFLOW_ERROR_MSG = "堆栈溢出错误";
    public final static String ENUM_CONSTANT_NOT_PRESENT_EXCEPTION_MSG = "枚举常量不存在";
    public final static String TYPE_NOT_PRESENT_EXCEPTION_MSG = "类型不存在异常";
    public final static String DEFAULT_MSG = "未找到匹配的错误信息";


    public static String getLocalMessage(Exception e) {
        Throwable rootCause = getRootCause(e);
        String exceptionMsg = "";

        /**获取自定义异常的错误信息*/
        exceptionMsg = getKnownException(e);
        if (exceptionMsg.isEmpty()) {
            /**获取公用异常的错误信息*/
            exceptionMsg = getCommonException(rootCause);
        }
        if (exceptionMsg.isEmpty()) {
            /**获取新增异常的错误信息*/
            exceptionMsg = getAddedException(rootCause);
        }
        return exceptionMsg;
    }

    private static String getKnownException(Exception e) {
        if (e instanceof KnowException) {
            /**匹配自定义异常*/
            return e.getMessage();
        }
        return "";
    }

    private static String getCommonException(Throwable rootCause) {
        String exceptionMsg = "";

        if (rootCause instanceof ArithmeticException) {
            exceptionMsg = ARITHMETIC_EXCEPTION_MSG;
        } else if (rootCause instanceof ArrayIndexOutOfBoundsException) {
            exceptionMsg = ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION_MSG;
        } else if (rootCause instanceof ArrayStoreException) {
            exceptionMsg = ARRAY_STORE_EXCEPTION_MSG;
        } else if (rootCause instanceof ClassCastException) {
            exceptionMsg = CLASS_CAST_EXCEPTION_MSG;
        } else if (rootCause instanceof ArrayIndexOutOfBoundsException) {
            exceptionMsg = ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION_MSG;
        } else if (rootCause instanceof IllegalArgumentException) {
            exceptionMsg = ILLEGAL_ARGUMENT_EXCEPTION_MSG;
        } else if (rootCause instanceof IllegalMonitorStateException) {
            exceptionMsg = ILLEGAL_MONITOR_STATE_EXCEPTION_MSG;
        } else if (rootCause instanceof IllegalStateException) {
            exceptionMsg = ILLEGAL_STATE_EXCEPTION_MSG;
        } else if (rootCause instanceof IllegalThreadStateException) {
            exceptionMsg = ILLEGAL_THREAD_STATE_EXCEPTION_MSG;
        } else if (rootCause instanceof IndexOutOfBoundsException) {
            exceptionMsg = INDEX_OUT_OF_BOUNDS_EXCEPTION_MSG;
        } else if (rootCause instanceof NegativeArraySizeException) {
            exceptionMsg = NEGATIVE_ARRAY_SIZE_EXCEPTION_MSG;
        } else if (rootCause instanceof NullPointerException) {
            exceptionMsg = NULL_POINTER_EXCEPTION_MSG;
        } else if (rootCause instanceof NumberFormatException) {
            exceptionMsg = NUMBER_FORMAT_EXCEPTION_MSG;
        } else if (rootCause instanceof SecurityException) {
            exceptionMsg = SECURITY_EXCEPTION_MSG;
        } else if (rootCause instanceof StringIndexOutOfBoundsException) {
            exceptionMsg = STRING_INDEX_OUT_OF_BOUNDS_EXCEPTION_MSG;
        } else if (rootCause instanceof UnsupportedOperationException) {
            exceptionMsg = UNSUPPORTED_OPERATION_EXCEPTION_MSG;
        } else if (rootCause instanceof ClassNotFoundException) {
            exceptionMsg = CLASS_NOT_FOUND_EXCEPTION_MSG;
        } else if (rootCause instanceof CloneNotSupportedException) {
            exceptionMsg = CLONE_NOT_SUPPORTED_EXCEPTION_MSG;
        } else if (rootCause instanceof IllegalAccessException) {
            exceptionMsg = ILLEGAL_ACCESS_EXCEPTION_MSG;
        } else if (rootCause instanceof InstantiationException) {
            exceptionMsg = INSTANTIATION_EXCEPTION_MSG;
        } else if (rootCause instanceof InterruptedException) {
            exceptionMsg = INTERRUPTED_EXCEPTION_MSG;
        } else if (rootCause instanceof NoSuchFieldException) {
            exceptionMsg = NO_SUCH_FIELD_EXCEPTION_MSG;
        } else if (rootCause instanceof NoSuchMethodException) {
            exceptionMsg = NO_SUCH_METHOD_EXCEPTION_MSG;
        }
        if (!exceptionMsg.isEmpty()) {
            exceptionMsg = String.format("%s : [ %s ]", exceptionMsg, rootCause.getMessage());
        }
        return exceptionMsg;
    }

    private static String getAddedException(Throwable rootCause) {
        String exceptionMsg = "";
        if (rootCause instanceof NoRouteToHostException) {
            exceptionMsg = NO_ROUTE_TO_HOST_EXCEPTION_MSG;
        } else if (rootCause instanceof SQLIntegrityConstraintViolationException) {
            exceptionMsg = SQL_INTEGRITY_CONSTRAINT_VIOLATION_EXCEPTION_MSG;
        } else if (rootCause instanceof SQLException) {
            exceptionMsg = SQL_EXCEPTION_MSG;
        } else if (rootCause instanceof SQLSyntaxErrorException) {
            exceptionMsg = SQL_SYNTAX_ERROR_EXCEPTION_MSG;
        } else if (rootCause instanceof IOException) {
            exceptionMsg = IO_EXCEPTION_MSG;
        } else if (rootCause instanceof EOFException) {
            exceptionMsg = EOF_EXCEPTION_MSG;
        } else if (rootCause instanceof FileNotFoundException) {
            exceptionMsg = FILE_NOT_FOUND_EXCEPTION_MSG;
        } else if (rootCause instanceof ClassCircularityError) {
            exceptionMsg = CLASS_CIRCULARITY_ERROR_MSG;
        } else if (rootCause instanceof OutOfMemoryError) {
            exceptionMsg = OUT_OF_MEMORY_ERROR_MSG;
        } else if (rootCause instanceof IncompatibleClassChangeError) {
            exceptionMsg = INCOMPATIBLE_CLASS_CHANGE_ERROR_MSG;
        } else if (rootCause instanceof InstantiationError) {
            exceptionMsg = INSTANTIATION_ERROR_MSG;
        } else if (rootCause instanceof LinkageError) {
            exceptionMsg = LINKAGE_ERROR_MSG;
        } else if (rootCause instanceof StackOverflowError) {
            exceptionMsg = STACK_OVERFLOW_ERROR_MSG;
        } else if (rootCause instanceof EnumConstantNotPresentException) {
            exceptionMsg = ENUM_CONSTANT_NOT_PRESENT_EXCEPTION_MSG;
        } else if (rootCause instanceof TypeNotPresentException) {
            exceptionMsg = TYPE_NOT_PRESENT_EXCEPTION_MSG;
        } else {
            exceptionMsg = DEFAULT_MSG;
        }
        if (!exceptionMsg.isEmpty()) {
            exceptionMsg = String.format("%s : [ %s ]", exceptionMsg, rootCause.getMessage());
        }
        return exceptionMsg;
    }

    private static Throwable getRootCause(Exception e) {
        Throwable rootCause = null;
        if (e.getCause() != null) {
            Throwable cause = e.getCause();
            while (cause != null && cause != rootCause) {
                rootCause = cause;
                cause = cause.getCause();
            }
        } else {
            rootCause = e;
        }
        return rootCause;
    }

    public static void thrownException(String message) throws KnowException {
        throw new KnowException(message);
    }
}

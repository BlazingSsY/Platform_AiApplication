package com.starmol.circuitreview.backend.config.web;

import com.starmol.circuitreview.backend.common.AppErrorCode;
import com.starmol.circuitreview.backend.common.ResponseWrapper;
import com.starmol.circuitreview.backend.exception.AppException;
import com.starmol.circuitreview.backend.exception.FileException;
import com.starmol.circuitreview.backend.exception.KnowException;
import com.starmol.circuitreview.backend.exception.TokenExpiredException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;

import io.minio.errors.ErrorResponseException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理自定义的业务异常
     */
    @ExceptionHandler(value = AppException.class)
    @ResponseBody
    public ResponseWrapper<?> knowExceptionHandler(HttpServletRequest req, AppException e) {
        return ResponseWrapper.fail().setMsg(e.getMessage()).setCode(e.getCode());
    }

    /**
     * 处理KnowException业务异常
     */
    @ExceptionHandler(value = KnowException.class)
    @ResponseBody
    public ResponseWrapper<?> knowExceptionHandler(HttpServletRequest req, KnowException e) {
        log.warn("业务异常: {}", e.getMessage());
        return ResponseWrapper.fail().setMsg(e.getMessage()).setCode(AppErrorCode.RECORD_NOT_FOUND_ERROR.getCode());
    }

    @ExceptionHandler(value = TokenExpiredException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseWrapper<?> tokenExpired(HttpServletRequest req, AppException e) {
        return ResponseWrapper.fail().setMsg(e.getMessage()).setCode(e.getCode());
    }

    /**
     * 处理文件异常
     */
    @ExceptionHandler(value = FileException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseWrapper<?> exceptionHandler(HttpServletRequest req, FileException e) {
        return ResponseWrapper.fail(e.getMessage());
    }

    /**
     * 处理MinIO官方SDK异常
     */
    @ExceptionHandler(value = {io.minio.errors.MinioException.class})
    @ResponseBody
    public ResponseWrapper<?> minioSdkExceptionHandler(HttpServletRequest req, Exception e) {
        log.error("MinIO SDK异常: ", e);
        return ResponseWrapper.fail().setMsg("存储服务异常").setCode(AppErrorCode.SERVER_INTERNAL_ERROR.getCode());
    }

    /**
     * 处理ErrorResponseException异常 (包括文件不存在等)
     */
    @ExceptionHandler(value = ErrorResponseException.class)
    @ResponseBody
    public ResponseWrapper<?> errorResponseExceptionHandler(HttpServletRequest req, ErrorResponseException e) {
        log.error("MinIO响应异常: ", e);
        // 文件不存在返回404
        if (e.response().code() == 404) {
            return ResponseWrapper.fail().setMsg("文件不存在").setCode(AppErrorCode.NOT_FOUND.getCode());
        }
        return ResponseWrapper.fail().setMsg("存储服务异常: " + e.getMessage()).setCode(AppErrorCode.SERVER_INTERNAL_ERROR.getCode());
    }

    /**
     * 处理SHA-256哈希计算异常
     */
    @ExceptionHandler(value = NoSuchAlgorithmException.class)
    @ResponseBody
    public ResponseWrapper<?> noSuchAlgorithmExceptionHandler(HttpServletRequest req, NoSuchAlgorithmException e) {
        log.error("哈希计算异常: ", e);
        return ResponseWrapper.fail().setMsg("哈希计算失败").setCode(AppErrorCode.SERVER_INTERNAL_ERROR.getCode());
    }

    /**
     * 处理其他异常
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseWrapper<?> exceptionHandler(HttpServletRequest req, Exception e) {
        log.error("未知异常！原因是:", e);
        return ResponseWrapper.fail("服务器内部异常");
    }

    @ExceptionHandler(value = MaxUploadSizeExceededException.class)
    @ResponseBody
    public ResponseWrapper<?> maxUploadSizeExceptionHandler(HttpServletRequest req, MaxUploadSizeExceededException e) {
        log.error("Maximum upload size exceeded.");
        return ResponseWrapper.fail().setMsg(e.getMessage());
    }

    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    @ResponseBody
    public ResponseWrapper<?> parameterMissingException(HttpServletRequest req, MissingServletRequestParameterException e) {
        log.error("parameter missing", e);
        return ResponseWrapper.fail().setCode(AppErrorCode.PARAM_REQUIRED.getCode()).setMsg(AppErrorCode.PARAM_REQUIRED.getMsg());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseWrapper<?> parameterMissing2Exception(HttpServletRequest req, MissingServletRequestParameterException e) {
        log.error("parameter missing", e);
        return ResponseWrapper.fail().setCode(AppErrorCode.PARAM_REQUIRED.getCode()).setMsg(AppErrorCode.PARAM_REQUIRED.getMsg());
    }
}

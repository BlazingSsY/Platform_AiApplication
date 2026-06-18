package com.starmol.portal.backend.config.web;


import com.starmol.portal.backend.utils.IdWorker;

import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import javax.servlet.http.HttpServletRequest;

/**
 * @author huguojun
 */
@Component
public class LoggingFilterConfig extends CommonsRequestLoggingFilter implements UniAuthInterceptor {

    /**
     * 设置到 MDC 里的key（惯例使用小驼峰，为了统一这里也用大写）
     */
    public static final String REQUEST_ID = "RequestId";

    ThreadLocal<Long> startTime = new ThreadLocal<>();

    @Override
    protected int getMaxPayloadLength() {
        return 10000;
    }

    @Override
    protected boolean isIncludeClientInfo() {
        return true;
    }

    @Override
    protected boolean isIncludeHeaders() {
        return true;
    }

    @Override
    protected Predicate<String> getHeaderPredicate() {
        return i -> Objects.equals(i, "Authorization");
    }

    @Override
    protected boolean isIncludePayload() {
        return true;
    }

    @Override
    protected boolean isIncludeQueryString() {
        return true;
    }

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {

        String requestId = request.getHeader(REQUEST_ID);
        if (requestId == null) {
            requestId = Long.toString(IdWorker.getId());
        }

        MDC.put(REQUEST_ID, requestId);
        startTime.set(System.currentTimeMillis());
        super.beforeRequest(request, message);
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        final Long requestTime = startTime.get();
        final long execTime = System.currentTimeMillis() - requestTime;
        message += "; [Execution time]: " + execTime / 1000.0 + "s";
        super.afterRequest(request, message);
        MDC.clear();
    }

    @Override
    protected String getMessagePayload(HttpServletRequest request) {
        if (Optional.ofNullable(request.getContentType()).orElse("").toLowerCase().contains(MediaType.MULTIPART_FORM_DATA_VALUE)) {
            return "this request is multipart/form";
        }
        return super.getMessagePayload(request);
    }
}

package com.starmol.circuitreview.backend.service.common;

import com.alibaba.fastjson.JSONObject;
import com.starmol.circuitreview.backend.exception.KnowException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Http请求的封装
 *
 * @author Yuexiaopeng
 * @date 2019/10/11
 */

@Service
@Slf4j
public class HttpUtilsService {
    private static final int DEFAULT_TIMES = 1000;
    private static final int DEFAULT_CONNECT_TIMEOUT = 10;
    private static final int DEFAULT_SOCKET_TIME_OUT = 60;
    private static final String DEFAULT_CONTENT_TYPE = "application/json";
    private static final String DEFAULT_CHAR_SET = "UTF-8";

    /**
     * CONNECT_TIMEOUT 客户端发送请求到与目标uri建立起连接的最大时间
     * 超时时间 3s
     */
    private final int connectTimeout;

    private final String contentType;

    private final String charSet;

    private final CloseableHttpClient httpClient;

    public HttpUtilsService() {
        this(DEFAULT_CONNECT_TIMEOUT, DEFAULT_CONTENT_TYPE, DEFAULT_CHAR_SET);
    }

    public HttpUtilsService(int connectTimeout) {
        this(connectTimeout, DEFAULT_CONTENT_TYPE, DEFAULT_CHAR_SET);
    }

    public HttpUtilsService(int connectTimeout, String contentType) {
        this(connectTimeout, contentType, DEFAULT_CHAR_SET);
    }

    public HttpUtilsService(int connectTimeout, String contentType, String charSet) {
        connectTimeout = connectTimeout * DEFAULT_TIMES;
        if (connectTimeout <= 0) {
            connectTimeout = DEFAULT_CONNECT_TIMEOUT;
        }
        if (contentType == null || contentType.isEmpty()) {
            contentType = DEFAULT_CONTENT_TYPE;
        }
        if (charSet == null || charSet.isEmpty()) {
            charSet = DEFAULT_CHAR_SET;
        }
        this.connectTimeout = connectTimeout;
        this.contentType = contentType;
        this.charSet = charSet;
        httpClient = HttpClients.createDefault();
    }

    public String postJsonString(String jsonString, String uri, String token) throws IOException {
        Map<String, String> headerMap = new HashMap<>(1);
        headerMap.put("Authorization", token);
        return postJsonString(jsonString, uri, headerMap, charSet, connectTimeout);
    }
    
    public String postJsonString(String jsonString, String uri, Map<String, String> headerMap, String charset, int connectTimeout) throws IOException {
        CloseableHttpResponse httpResponse = null;
        HttpEntity entityResponse = null;
        HttpPost httpPost = new HttpPost(uri);
        try {
            if (connectTimeout == 0) {
                connectTimeout = DEFAULT_CONNECT_TIMEOUT * DEFAULT_TIMES;
            }
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(connectTimeout).build();
            httpPost.setConfig(requestConfig);

            if (headerMap != null && !headerMap.isEmpty()) {
                for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                    if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                        httpPost.addHeader(entry.getKey(), entry.getValue());
                    }
                }
            }
            String defaultJsonString = JSONObject.toJSONString("");
            if (jsonString == null) {
                jsonString = defaultJsonString;
            }
            if (charset == null || charset.isEmpty()) {
                charset = DEFAULT_CHAR_SET;
            }

            StringEntity stringEntity = new StringEntity(jsonString, charset);
            stringEntity.setContentType(contentType);
            httpPost.setEntity(stringEntity);
            httpResponse = httpClient.execute(httpPost);
            entityResponse = httpResponse.getEntity();
            log.debug("[Post] uri={}", uri);
            return EntityUtils.toString(entityResponse);
        } catch (IOException | ParseException e) {
            log.error(String.format("[Get] uri=%s", uri), e);
            throw new KnowException(String.format("远程调用:uri=%s 失败: %s", uri, e.getMessage()));
        } finally {
            if (entityResponse != null && !entityResponse.isStreaming()) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    log.error("关闭response失败: {}", e.getMessage(), e);
                }
            }
        }
    }
}

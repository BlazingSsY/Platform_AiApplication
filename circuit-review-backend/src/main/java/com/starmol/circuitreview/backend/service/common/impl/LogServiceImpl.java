package com.starmol.circuitreview.backend.service.common.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starmol.circuitreview.backend.bean.bo.ResponseDataBO;
import com.starmol.circuitreview.backend.exception.KnowException;
import com.starmol.circuitreview.backend.service.circuitreview.feign.ReviewClient;
import com.starmol.circuitreview.backend.service.common.LogService;
import com.starmol.circuitreview.backend.utils.HttpRequestUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * log服务实现类
 *
 * @author system
 * @date 2025-01-07
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {

    @Value("${log-file-path}")
    private String logFilePath;

    private final ObjectMapper objectMapper;
    private final ReviewClient reviewClient;


    @SneakyThrows
    @Override
    public List<String> getLogs(Integer num, String logName) {
        try {
            //调用第三方接口,获取log
            ResponseDataBO<Object> responseWrapper = reviewClient.getLog(HttpRequestUtil.getDebugId(), HttpRequestUtil.getLoginName(), num, logName);
            if (!responseWrapper.getCode().equals(0) && !responseWrapper.getCode().equals(200)) {
                log.error("获取log,调用接口失败: {}", responseWrapper.getMessage());
                throw new KnowException(responseWrapper.getMessage());
            } else {
                log.info("获取log成功");
                return objectMapper.convertValue(responseWrapper.getData(), new TypeReference<>() {});
            }
        } catch (KnowException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取log失败", e);
            throw new KnowException(e.getMessage(), e);
        }
    }


    @SneakyThrows
    @Override
    public List<String> getServiceLogs(Integer num) {
        File file = new File(logFilePath);
        if (!file.exists()) {
            return Collections.singletonList("Log file not found.");
        }
        List<String> result = new ArrayList<>();
        try (ReversedLinesFileReader reader = new ReversedLinesFileReader(file, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null && result.size() < num) {
                result.add(line);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return Collections.singletonList("Error reading log file.");
        }
        // 因为是从后往前读，所以需要反转
        Collections.reverse(result);
        return result;
    }

}

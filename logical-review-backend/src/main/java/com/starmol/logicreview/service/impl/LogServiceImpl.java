package com.starmol.logicreview.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starmol.logicreview.bean.bo.ResponseDataBO;
import com.starmol.logicreview.bean.dto.GetLogDTO;
import com.starmol.logicreview.exception.KnowException;
import com.starmol.logicreview.service.LogService;
import com.starmol.logicreview.service.feign.CodeReviewClient;
import com.starmol.logicreview.utils.HttpRequestUtil;

import org.apache.commons.io.input.ReversedLinesFileReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

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
    private final CodeReviewClient codeReviewClient;


    @SneakyThrows
    @Override
    public List<String> getLogs(Integer num) {
        try {
            //调用第三方接口,获取log
            ResponseDataBO<Object> responseWrapper = codeReviewClient.getLog(HttpRequestUtil.getDebugId(), HttpRequestUtil.getLoginName(),num);
            if (!responseWrapper.getCode().equals(200)) {
                log.error("获取log,调用接口失败: {}", responseWrapper.getMessage());
                throw new KnowException(responseWrapper.getMessage());
            } else {
                log.info("获取log成功");
                List<String> logList = objectMapper.convertValue(responseWrapper.getData(), new TypeReference<>() {});
                return logList;
            }
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
            e.printStackTrace();
            return Collections.singletonList("Error reading log file.");
        }
        // 因为是从后往前读，所以需要反转
        Collections.reverse(result);
        return result;
    }

} 
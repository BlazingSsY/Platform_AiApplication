package com.starmol.logicreview.service;

import com.starmol.logicreview.bean.dto.GetLogDTO;

import java.util.List;

/**
 * 代码审查统计服务接口
 *
 * @author system
 * @date 2025-01-07
 */
public interface LogService {

    /**
     * 获取Log
     *
     * @return Log
     */
    List<String> getLogs(Integer num);

    List<String> getServiceLogs(Integer num);
}
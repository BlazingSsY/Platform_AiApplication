package com.starmol.sourcecodereview.service.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.starmol.sourcecodereview.model.LogRecord;
import com.starmol.sourcecodereview.service.base.BaseService;

public interface LogRecordService extends BaseService<LogRecord> {

    /**
     * 插入用户操作日志
     *
     * @param modelName 模块名称
     * @param content   日志内容
     * @param userId    登录用户id
     * @param userName  登录用户名
     * @param loginName 用户登陆名
     * @return 操作日志对象
     */
    LogRecord insert(String modelName, String content, Long userId, String userName, String loginName);

    /**
     * 插入用户操作日志
     *
     * @param modelName 模块名称
     * @param content   日志内容
     * @return 操作日志对象
     */
    LogRecord insert(String modelName, String content);

    IPage<LogRecord> getLogRecordPages(IPage<LogRecord> page, String parameter, String startTime, String endTime);
}

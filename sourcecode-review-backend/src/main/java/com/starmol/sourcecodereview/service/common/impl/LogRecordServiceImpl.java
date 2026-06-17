package com.starmol.sourcecodereview.service.common.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starmol.sourcecodereview.common.UserMetaData;
import com.starmol.sourcecodereview.model.LogRecord;
import com.starmol.sourcecodereview.repository.LogRecordMapper;
import com.starmol.sourcecodereview.service.base.impl.BaseCascadeServiceImpl;
import com.starmol.sourcecodereview.service.common.LogRecordService;
import com.starmol.sourcecodereview.utils.HttpRequestUtil;
import com.starmol.sourcecodereview.utils.StringUtil;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LogRecordServiceImpl extends BaseCascadeServiceImpl<LogRecordMapper, LogRecord> implements LogRecordService {

    @Override
    public LogRecord insert(String modelName, String content, Long userId, String userName, String loginName) {
        try {
            LogRecord logRecord = new LogRecord();
            logRecord.setModelName(modelName);
            logRecord.setContent(content);
            logRecord.setUserId(userId);
            logRecord.setUserName(userName);
            logRecord.setLoginName(loginName);
            return saveAndReturnObject(logRecord);
        } catch (Exception e) {
            log.error(String.format("create log error:%s", e.getMessage()), e);
            return null;
        }
    }

    @Override
    public LogRecord insert(String modelName, String content) {
        try {
            LogRecord logRecord = new LogRecord();
            logRecord.setModelName(modelName);
            logRecord.setContent(content);
            UserMetaData user = HttpRequestUtil.getUser();
            if (user != null) {
                logRecord.setUserId(user.getId());
                logRecord.setUserName(user.getName());
                logRecord.setLoginName(user.getLoginName());
            } else {
                logRecord.setUserId(null);
                logRecord.setLoginName(StringUtil.EMPTY_STRING);
            }

            return saveAndReturnObject(logRecord);
        } catch (Exception e) {
            log.error(String.format("create log error:%s", e.getMessage()), e);
            return null;
        }
    }

    @Override
    public IPage<LogRecord> getLogRecordPages(IPage<LogRecord> page, String parameter, String startTime, String endTime) {
        LambdaQueryWrapper<LogRecord> lambdaQueryWrapper = Wrappers.lambdaQuery();
        if (StringUtils.isNotEmpty(startTime)) {
            lambdaQueryWrapper.ge(LogRecord::getOperateDate, startTime);
        }
        if (StringUtils.isNotEmpty(endTime)) {
            // 结束时间为当天的最后时间点
            lambdaQueryWrapper.le(LogRecord::getOperateDate, endTime + " 23:59:59.999000");
        }

        if (StringUtils.isNotEmpty(parameter)) {
            lambdaQueryWrapper.and(Wrapper -> Wrapper.like(LogRecord::getModelName, parameter)
                    .or().like(LogRecord::getContent, parameter));
        }
        lambdaQueryWrapper.orderByDesc(LogRecord::getOperateDate);
        return this.page(page, lambdaQueryWrapper);
    }
}

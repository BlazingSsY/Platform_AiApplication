package com.starmol.portal.backend.utils;

import com.starmol.portal.backend.model.base.IdBaseModel;

import java.time.LocalDateTime;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BaseModelBeanUtils {
    public static <T extends IdBaseModel> T initModelBaseInfo(T baseObject) {
        LocalDateTime currentTime = LocalDateTime.now();
        baseObject.setId(IdWorker.getId());
        baseObject.setCreateUser(HttpRequestUtil.getUserId());
        baseObject.setUpdateUser(HttpRequestUtil.getUserId());
        baseObject.setCreateDate(currentTime);
        baseObject.setUpdateDate(currentTime);
        return baseObject;
    }
}

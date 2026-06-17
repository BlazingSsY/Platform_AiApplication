package com.starmol.sso.server.service;

import com.google.common.collect.Lists;

import com.starmol.sso.server.pojo.dto.OauthUserInfo;
import com.starmol.sso.server.pojo.dto.ResponseWrapper;
import com.starmol.sso.server.pojo.dto.User;
import com.starmol.sso.server.pojo.dto.param.UserProfile;
import com.starmol.sso.server.util.feign.ManagementRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class OauthUserInfoService {

    /**
     * 这里必须采用 value 为 String 类型，不然 客户端和服务端都要读取该信息，但是又没有共同的类进行序列化，所以必须转换成 JSON 字符串 如果后面出现的公共类越来越多我再考虑独立出一个 jar 包出来维护
     */
    @Autowired
    private ManagementRequest managementRequest;

    public List<OauthUserInfo> getUserIdNameMapByIds(List<String> userIds) {
        final ResponseWrapper<List<User>> users = managementRequest.listUserByIds(userIds);
        if (Objects.isNull(users) || Objects.isNull(users.getContent())) {
            return Lists.newArrayList();
        }

        final List<User> content = users.getContent();
        return content.stream()
                .map(i -> new OauthUserInfo()
                        .setUserId(i.getId())
                        .setUserName(StringUtils.isBlank(i.getName()) ? i.getLoginName() : i.getName())
                ).collect(Collectors.toList());
    }

    public void updateUserProfile(UserProfile userProfile) {
    }
}

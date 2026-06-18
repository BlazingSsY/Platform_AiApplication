package com.starmol.portal.backend.service.oauth.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.starmol.portal.backend.bean.dto.oauth.OauthClientCreateRequestParam;
import com.starmol.portal.backend.bean.po.oauth.OauthClientRedisEventInfo;
import com.starmol.portal.backend.bean.vo.oauth.SsoClients;
import com.starmol.portal.backend.eventlistener.event.OauthClientRedisEvent;
import com.starmol.portal.backend.exception.KnowException;
import com.starmol.portal.backend.model.OauthClient;
import com.starmol.portal.backend.repository.OauthClientMapper;
import com.starmol.portal.backend.service.application.ApplicationService;
import com.starmol.portal.backend.service.base.impl.BaseCascadeServiceImpl;
import com.starmol.portal.backend.service.oauth.OauthClientService;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class OauthClientServiceImpl extends BaseCascadeServiceImpl<OauthClientMapper, OauthClient> implements OauthClientService {

    private static final int ENABLED = 1;
    private static final int DISABLED = 2;

    private final ObjectMapper objectMapper;

    private final ApplicationContext applicationContext;

    private final ApplicationService applicationService;

    @Override
    public IPage<OauthClient> findPageByParam(Page<OauthClient> objectPage, String clientName, String clientId, Integer stateEnum) {
        final LambdaQueryWrapper<OauthClient> wrapper = Wrappers.<OauthClient>lambdaQuery()
                .like(StringUtils.isNotBlank(clientName), OauthClient::getClientName, clientName)
                .like(StringUtils.isNotBlank(clientId), OauthClient::getClientId, clientId)
                .eq(Objects.nonNull(stateEnum), OauthClient::getStateEnum, stateEnum);
        return this.page(objectPage, wrapper);
    }

    @Override
    public OauthClient findOneById(Long applicationId) {
        final LambdaQueryWrapper<OauthClient> queryWrapper = Wrappers.lambdaQuery(OauthClient.class).eq(OauthClient::getApplicationId, applicationId);
        List<OauthClient> oauthClientList = this.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(oauthClientList)) {
            return oauthClientList.get(0);
        } else {
            return new OauthClient();
        }
    }

    @Override
    public synchronized OauthClient create(OauthClientCreateRequestParam param) {
        final OauthClient oauthClient = objectMapper.convertValue(param, OauthClient.class);
        oauthClient.setClientSecret(generateClientSecret(oauthClient.getClientId()));

        // 检查 applicationId 对应的 Application 是否存在
        if (applicationService.getById(oauthClient.getApplicationId()) == null) {
            throw new KnowException("applicationId 对应的应用不存在");
        }

        final LambdaQueryWrapper<OauthClient> clientIdQueryWrapper = Wrappers.lambdaQuery(OauthClient.class).eq(OauthClient::getClientId, oauthClient.getClientId());
        if (this.count(clientIdQueryWrapper) > 0) {
            throw new KnowException("clientId已存在");
        }
        final OauthClient oauthClientEntity = this.saveAndReturnObject(oauthClient);
        OauthClientRedisEventInfo oauthClientRedisEventInfo = new OauthClientRedisEventInfo();
        oauthClientRedisEventInfo.setOauthClientListByCreate(Lists.newArrayList(oauthClient));
        applicationContext.publishEvent(new OauthClientRedisEvent(this, oauthClientRedisEventInfo));

        return oauthClientEntity;
    }

    /**
     * 根据 clientId 和当前时间戳生成 32 位 clientSecret（MD5 十六进制）
     */
    private String generateClientSecret(String clientId) {
        String raw = clientId + System.currentTimeMillis();
        return DigestUtils.md5DigestAsHex(raw.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void batchDelete(List<Long> idList) {
        if (CollectionUtils.isEmpty(idList)) {
            return;
        }

        final List<OauthClient> oauthClients = this.listByIds(idList);
        if (CollectionUtils.isEmpty(oauthClients)) {
            return;
        }

        this.removeByIds(idList);

        OauthClientRedisEventInfo oauthClientRedisEventInfo = new OauthClientRedisEventInfo();
        oauthClientRedisEventInfo.setOauthClientListByDelete(oauthClients);
        applicationContext.publishEvent(new OauthClientRedisEvent(this, oauthClientRedisEventInfo));
    }

    @Override
    public void batchUpdateState(Integer stateEnum, List<Long> idList) {

        if (CollectionUtils.isEmpty(idList)) {
            return;
        }

        List<OauthClient> listByIdList = this.listByIds(idList);
        if (CollectionUtils.isEmpty(listByIdList)) {
            return;
        }

        final LambdaUpdateWrapper<OauthClient> updateWrapper = Wrappers.<OauthClient>lambdaUpdate()
                .set(OauthClient::getStateEnum, stateEnum).in(OauthClient::getId, idList);
        this.update(updateWrapper);


        OauthClientRedisEventInfo oauthClientRedisEventInfo = new OauthClientRedisEventInfo();
        if (ENABLED == stateEnum) {
            oauthClientRedisEventInfo.setOauthClientListByCreate(listByIdList);
        } else {
            oauthClientRedisEventInfo.setOauthClientListByDelete(listByIdList);
        }

        applicationContext.publishEvent(new OauthClientRedisEvent(this, oauthClientRedisEventInfo));
    }

    @Override
    public SsoClients getClientList() {
        final SsoClients ssoClients = new SsoClients();
        final List<OauthClient> list = this.list();
        list.forEach(i -> ssoClients.addClient(i.getClientName(), i.getClientId()));
        return ssoClients;
    }
}

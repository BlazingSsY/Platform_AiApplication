package com.starmol.portal.backend.eventlistener.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import com.starmol.portal.backend.bean.bo.oauth.OauthClientToRedisBO;
import com.starmol.portal.backend.bean.po.oauth.OauthClientRedisEventInfo;
import com.starmol.portal.backend.config.redis.RedisAvailableCondition;
import com.starmol.portal.backend.eventlistener.event.OauthClientRedisEvent;
import com.starmol.portal.backend.model.OauthClient;
import com.starmol.portal.backend.service.redis.StringRedisService;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;


/**
 * OAuth 客户端 Redis 事件监听器。
 * 监听 OauthClientRedisEvent，将客户端变更同步到 Redis 缓存。
 * 仅在 Redis 可用时才加载（host、port 配置完整且连接可达）。
 */
@Slf4j
@Component
@Conditional(RedisAvailableCondition.class)
public class OauthClientRedisEventListener {

    private static final String REDIS_CLIENT_ID_KEY_PREFIX = "OAUTH:CLIENT_ID:";

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private StringRedisService<String, String> clientRedisService;

    /**
     * 处理 OAuth 客户端创建/删除事件，同步到 Redis
     */
    @SneakyThrows
    @Async
    @EventListener(OauthClientRedisEvent.class)
    public void register(OauthClientRedisEvent oauthClientRedisEvent) {
        OauthClientRedisEventInfo oauthClientRedisEventInfo = oauthClientRedisEvent.getOauthClientRedisEventInfo();
        List<OauthClient> oauthClientListByCreate = oauthClientRedisEventInfo.getOauthClientListByCreate();
        List<OauthClient> oauthClientListByDelete = oauthClientRedisEventInfo.getOauthClientListByDelete();

        if (CollectionUtils.isNotEmpty(oauthClientListByCreate)) {
            final CollectionType collectionType = objectMapper.getTypeFactory()
                    .constructCollectionType(ArrayList.class, OauthClientToRedisBO.class);
            List<OauthClientToRedisBO> oauthClientToRedisBOList = objectMapper.convertValue(oauthClientListByCreate, collectionType);
            for (OauthClientToRedisBO oauthClientToRedisBO : oauthClientToRedisBOList) {
                clientRedisService.set(REDIS_CLIENT_ID_KEY_PREFIX + oauthClientToRedisBO.getClientId(),
                        objectMapper.writeValueAsString(oauthClientToRedisBO));
            }
        }

        if (CollectionUtils.isNotEmpty(oauthClientListByDelete)) {
            List<String> keyList = new ArrayList<>();
            for (OauthClient oauthClient : oauthClientListByDelete) {
                keyList.add(REDIS_CLIENT_ID_KEY_PREFIX + oauthClient.getClientId());
            }
            clientRedisService.deleteByKeys(keyList);
        }
    }

}

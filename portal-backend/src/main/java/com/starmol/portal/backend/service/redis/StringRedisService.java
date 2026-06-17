package com.starmol.portal.backend.service.redis;

import com.starmol.portal.backend.config.redis.RedisAvailableCondition;

import org.springframework.context.annotation.Conditional;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

/**
 * 限制 key 只能为 String 的 Redis 操作服务。
 * 仅在 Redis 可用时才加载（host、port 配置完整且连接可达）。
 */
@Service
@Conditional(RedisAvailableCondition.class)
public class StringRedisService<String, V> {

    @Resource
    private RedisTemplate<String, V> redisTemplate;

    // ===================================== common start =====================================

    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    public void deleteByKeys(Collection<String> keys) {
        redisTemplate.delete(keys);
    }

    // ===================================== common end =====================================

    // ===================================== key value start =====================================

    public void set(String key, V value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void set(String key, V value, long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    public void set(String key, V value, long timeout, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    public V get(String key) {
        ValueOperations<String, V> valueOperations = redisTemplate.opsForValue();
        return valueOperations.get(key);
    }

    // ===================================== key value end =====================================

}

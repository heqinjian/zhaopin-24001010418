package com.recruitment.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 保存数据，并设置过期时间。
     *
     * @param key     Redis 中的键
     * @param value   需要保存的数据
     * @param timeout 过期时间，单位为秒
     */
    public void set(String key, Object value, long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * 根据 Key 读取数据。
     * Key 不存在或已经过期时返回 null。
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除指定缓存。
     * 数据发生修改时，可以删除对应的旧缓存。
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }
}

package com.man.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public String getString(String redisKey) {
        Object value = redisTemplate.opsForValue().get(redisKey);
        if (null != value) {
            return value.toString();
        } else {
            return null;
        }
    }

    /**
     * 保存json字符串
     *
     * @param key   键值
     * @param value json对象字符串
     */
    public void setJson(String key, Object value) {
        redisTemplate.opsForValue().set(key, JSONObject.toJSONString(value));
    }

    /**
     * 保存json字符串
     *
     * @param key
     * @param value
     * @param seconds aaa
     */
    public void setStringToJsonEx(String key, Object value, int seconds) {
        setStringExpire(key, JSONObject.toJSONString(value), seconds);
    }

    /**
     * 更新key对象field的值
     *
     * @param key   缓存key
     * @param field 缓存对象field
     * @param value 缓存对象field值
     */
    public void setJsonField(String key, String field, String value) {
        JSONObject obj = JSON.parseObject(redisTemplate.boundValueOps(key).get());
        obj.put(field, value);
        redisTemplate.opsForValue().set(key, obj.toJSONString());
    }

    /**
     * 将map写入缓存
     *
     * @param key
     * @param map
     */
    public <T> void setMap(String key, Map<String, T> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * 向key对应的map中添加缓存对象
     *
     * @param key
     * @param map
     */
    public <T> void addMap(String key, Map<String, T> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * 向key对应的map中添加缓存对象
     *
     * @param key   cache对象key
     * @param field map对应的key
     * @param value 值
     */
    public void addMap(String key, String field, String value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    /**
     * 向key对应的map中添加缓存对象
     *
     * @param key   cache对象key
     * @param field map对应的key
     * @param obj   对象
     */
    public <T> void addMap(String key, String field, T obj) {
        redisTemplate.opsForHash().put(key, field, obj);
    }

    /**
     * 获取map缓存
     *
     * @param key
     * @param clazz
     * @return
     */
    public <T> Map<String, T> mget(String key, Class<T> clazz) {
        BoundHashOperations<String, String, T> boundHashOperations = redisTemplate.boundHashOps(key);
        return boundHashOperations.entries();
    }

    public <T> Set<String> mgetBySortToKeys(String key, Class<T> clazz) {
        BoundHashOperations<String, String, T> boundHashOperations = redisTemplate.boundHashOps(key);
        return boundHashOperations.keys();
    }

    public void setString(String redisKey, String value) {
        redisTemplate.opsForValue().set(redisKey, value);
    }

    public void delString(String redisKey) {
        log.info("Delete-redis-key:" + redisKey);
        redisTemplate.delete(redisKey);
    }

    /**
     * key 是否存在
     *
     * @param redisKey
     * @return
     */
    public boolean hasKey(String redisKey) {
        return redisTemplate.hasKey(redisKey);
    }

    public String getStringForHash(String redisKey, String field) {
        Object value = redisTemplate.opsForHash().get(redisKey, field);
        if (null != value) {
            return String.valueOf(value);
        } else {
            return null;
        }
    }

    public JSONObject getJsonForHash(String redisKey, String field) {
        Object value = redisTemplate.opsForHash().get(redisKey, field);
        if (null != value) {
            return JSONObject.parseObject(value.toString());
        } else {
            return null;
        }
    }

    public void setStringForHash(String redisKey, String field, String value) {
        redisTemplate.opsForHash().put(redisKey, field, value);
    }

    public void removeStringForHash(String redisKey, String field) {
        redisTemplate.opsForHash().delete(redisKey, field);
    }

    public boolean hasKeyForHash(String redisKey, String field) {
        return redisTemplate.opsForHash().hasKey(redisKey, field);
    }

    public Long incr(String redisKey) {
        return redisTemplate.opsForValue().increment(redisKey, 1);
    }

    public Long incrAndSetExpire(String redisKey, int seconds, TimeUnit timeUnit) {
        Long value = incr(redisKey);
        redisTemplate.opsForValue().set(redisKey, String.valueOf(value), seconds, timeUnit);
        return value;
    }

    public String setStringExpire(String redisKey, String value, int seconds, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(redisKey, value, seconds, timeUnit);
        return value;
    }

    public void setStringExpire(String redisKey, String value, int seconds) {
        redisTemplate.opsForValue().set(redisKey, value, seconds, TimeUnit.SECONDS);
    }

    public void reSetStringExpire(String redisKey, int seconds) {
        String value = redisTemplate.opsForValue().get(redisKey);
        setStringExpire(redisKey, value, seconds);
    }

    public Long generateInc(String key, int timeout) {
        RedisAtomicLong counter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        counter.expire(timeout, TimeUnit.DAYS);
        return counter.incrementAndGet();
    }

    private static final Integer REDIS_RETRY_LOCK_TIMEOUT_MINUTES = 10;

    /**
     * 重试锁
     *
     * @param redisKeyName
     * @param maxNum       最大重试次数
     * @return
     */
    public boolean reTry(String redisKeyName, Long maxNum) {
        Long nowNum = incrAndSetExpire(redisKeyName, REDIS_RETRY_LOCK_TIMEOUT_MINUTES, TimeUnit.MINUTES);
        if (maxNum <= nowNum) {
            return true;
        }
        return false;
    }

}

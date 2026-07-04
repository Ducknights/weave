package com.weave.redis.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import com.weave.redis.constant.CacheNullValue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class RedisUtil {

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    public RedisUtil(StringRedisTemplate redisTemplate,
                     @Qualifier("redisObjectMapper") ObjectMapper objectMapper) {
        this.stringRedisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * 设置缓存，自动序列化对象为JSON字符串
     */
    public <T> void set(String key, T value) {
        try {
            String json = objectMapper.writeValueAsString(value);
            stringRedisTemplate.opsForValue().set(key, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置缓存，自动序列化对象为JSON字符串，并设置过期时间(默认单位为秒)
     */
    public <T> void set(String key, T value, Duration duration) {
        try {
            String json = objectMapper.writeValueAsString(value);
            stringRedisTemplate.opsForValue().set(key, json, duration);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取缓存，自动将JSON字符串反序列化为对象
     */
    @SneakyThrows
    public <T> T get(String key, JavaType javaType) {
        String json = getJson(key);
        return json == null ? null : objectMapper.readValue(json, javaType);
    }

    @SneakyThrows
    public <T> T get(String key, Class<T> clazz) {
        String json = getJson(key);
        return json == null ? null : objectMapper.readValue(json, clazz);
    }

    @SneakyThrows
    public <T> T get(String key, TypeReference<T> typeReference) {
        String json = getJson(key);
        return json == null ? null : objectMapper.readValue(json, typeReference);
    }

    private String getJson(String key) {
        String json = stringRedisTemplate.opsForValue().get(key);
        if (json == null || CacheNullValue.NULL_VALUE.equals(json)) {return null;}
        return json;
    }

    /**
     * 设置缓存，序列化对象(List)为 SET
     */
    public void setForSet(String key, Set<?> obj, Duration duration) {
        // 如果对象为空或为空集合，则删除缓存
        if (obj == null || obj.isEmpty()){
            this.delete(key);
            return;
        }
        // 将对象转换为字符串数组
        String[] values = obj.stream()
                .map(this::convertToString)
                .filter(Objects::nonNull)
                .toArray(String[]::new);
        if (values.length == 0)
            this.delete(key);
        // 保存缓存并设置过期时间
        stringRedisTemplate.opsForSet().add(key, values);
        stringRedisTemplate.expire(key, duration);
    }
    @SneakyThrows
    private String convertToString(Object item) {
        if (item instanceof String) return (String) item;
        if (item instanceof Number) return String.valueOf(item);
        return objectMapper.writeValueAsString(item);
    }

    /**
      * 设置缓存，序列化对象为 Hash（确保对象中没有嵌套对象）
     */
    public <T> void setForHash(String key, T obj, Duration duration) {
        Map<String, Object> map = objectMapper.convertValue(obj, new TypeReference<>() {});
        stringRedisTemplate.opsForHash().putAll(key, map);
        stringRedisTemplate.expire(key, duration);
    }

    public void setForHash(String key, Map<String, Object> map, Duration duration) {
        stringRedisTemplate.opsForHash().putAll(key, map);
        stringRedisTemplate.expire(key, duration);
    }

    /**
     * 获取缓存，反序列化 Hash 为对象
     */
    public <T> T getForHash(String key, Class<T> clazz) {
        Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(key);
        return objectMapper.convertValue(map, clazz);
    }

    public Map<Object, Object> getForHash(String key) {
        return stringRedisTemplate.opsForHash().entries(key);
    }

    /**
     * 判断缓存是否存在
     */
    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }

    /**
     * 删除缓存
     */
    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }
}

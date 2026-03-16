package org.example.util;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class RedisSortedSetUtil {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 向有序集合添加一个或多个元素，以及其分数
     * @param key 有序集合的键
     * @param score 分数
     * @param value 值
     * @return 是否添加成功
     */
    public Boolean add(String key, Double score, Object value) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * 获取有序集合中元素的分数
     * @param key 有序集合的键
     * @param value 元素值
     * @return 元素的分数，如果元素不存在则返回null
     */
    public Double score(String key, Object value) {
        return redisTemplate.opsForZSet().score(key, value);
    }

    /**
     * 获取有序集合中指定分数范围的元素
     * @param key 有序集合的键
     * @param min 最小分数
     * @param max 最大分数
     * @return 元素集合
     */
    public Set<Object> rangeByScore(String key, Double min, Double max) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max);
    }

    /**
     * 获取有序集合中指定排名范围的元素（按分数从低到高）
     * @param key 有序集合的键
     * @param start 起始排名（0表示第一个元素）
     * @param end 结束排名（-1表示最后一个元素）
     * @return 元素集合
     */
    public Set<Object> range(String key, Long start, Long end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    /**
     * 获取有序集合中指定排名范围的元素（按分数从高到低）
     * @param key 有序集合的键
     * @param start 起始排名（0表示第一个元素）
     * @param end 结束排名（-1表示最后一个元素）
     * @return 元素集合
     */
    public Set<Object> reverseRange(String key, Long start, Long end) {
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    /**
     * 获取元素在有序集合中的排名（按分数从低到高，0表示第一个）
     * @param key 有序集合的键
     * @param value 元素值
     * @return 元素的排名，如果元素不存在则返回null
     */
    public Long rank(String key, Object value) {
        return redisTemplate.opsForZSet().rank(key, value);
    }

    /**
     * 获取元素在有序集合中的排名（按分数从高到低，0表示第一个）
     * @param key 有序集合的键
     * @param value 元素值
     * @return 元素的排名，如果元素不存在则返回null
     */
    public Long reverseRank(String key, Object value) {
        return redisTemplate.opsForZSet().reverseRank(key, value);
    }

    /**
     * 从有序集合中删除一个或多个元素
     * @param key 有序集合的键
     * @param values 要删除的元素值
     * @return 删除的元素个数
     */
    public Long remove(String key, Object... values) {
        return redisTemplate.opsForZSet().remove(key, values);
    }

    /**
     * 获取有序集合的大小
     * @param key 有序集合的键
     * @return 集合大小
     */
    public Long size(String key) {
        return redisTemplate.opsForZSet().size(key);
    }

    /**
     * 计算两个有序集合的交集并存储到新的集合
     * @param key 目标集合的键
     * @param otherKey 另一个集合的键
     * @return 交集元素个数
     */
    public Long intersectAndStore(String key, String otherKey, String destKey) {
        return redisTemplate.opsForZSet().intersectAndStore(key, otherKey, destKey);
    }

    /**
     * 计算两个有序集合的并集并存储到新的集合
     * @param key 目标集合的键
     * @param otherKey 另一个集合的键
     * @param destKey 结果集合的键
     * @return 并集元素个数
     */
    public Long unionAndStore(String key, String otherKey, String destKey) {
        return redisTemplate.opsForZSet().unionAndStore(key, otherKey, destKey);
    }
}

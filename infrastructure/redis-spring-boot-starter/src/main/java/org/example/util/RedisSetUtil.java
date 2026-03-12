package org.example.util;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class RedisSetUtil {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取两个集合的交集
     * @param key1 第一个集合的键
     * @param key2 第二个集合的键
     * @return 两个集合交集元素的Set集合
     */
    public Set<Object> intersect(String key1, String key2) {
        return redisTemplate.opsForSet().intersect(key1, key2);
    }

    /**
     * 获取两个集合的差集
     * @param key1 第一个集合的键
     * @param key2 第二个集合的键
     * @return 第一个集合中有,但第二个集合中没有的元素的集合
     */
    public Set<Object> difference(String key1, String key2) {
        return redisTemplate.opsForSet().difference(key1, key2);
    }

    /**
     * 获取两个集合的并集
     * @param key1 第一个集合的键
     * @param key2 第二个集合的键
     * @return 包含两个集合中所有元素的新集合
     */
    public Set<Object> union(String key1, String key2) {
        return redisTemplate.opsForSet().union(key1, key2);
    }

    /**
     * 检查指定值是否是集合中的成员
     * @param key 集合的键名
     * @param value 要检查的值
     * @return 如果值是集合成员则返回true，否则返回false
     */
    public boolean isMember(String key, Object value) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
    }
}

package org.example.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.strings.CacheKey;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis配置类，用于配置Redis连接、序列化方式和缓存管理
 * 使用@EnableCaching注解启用缓存功能
 */
@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        // 注册 Java 8 时间模块
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerModule(new Jdk8Module());

        // 禁用日期序列化为时间戳
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
        objectMapper.disable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);

        // 设置日期格式
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        // 配置多态类型支持，有优有劣，反正我暂时不用它
//        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
//                .allowIfBaseType(Object.class)
//                .allowIfSubType("org.example.model")
//                .allowIfSubType("org.example.model.vo")
//                .allowIfSubType("java.util")
//                .build();
//
//        objectMapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

        // 其他安全配置
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        return objectMapper;
    }

    @Bean
    public GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer(ObjectMapper objectMapper) {
        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }

    // 配置 RedisTemplate
    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory connectionFactory,
            GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer) {

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);

        // 使用配置好的序列化器
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);

        // 启用事务支持
        redisTemplate.setEnableTransactionSupport(true);

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    // 配置 CacheManager
    @Bean
    @Primary
    public CacheManager cacheManager(
            RedisConnectionFactory factory,
            GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer) {

        // 默认缓存配置
        RedisCacheConfiguration defaultConfig  = RedisCacheConfiguration.defaultCacheConfig()
                // 设置1小时过期时间
                .entryTtl(Duration.ofHours(1))
                // 不缓存null值
                .disableCachingNullValues()
                // 使用StringRedisSerializer序列化key
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                // 使用GenericJackson2JsonRedisSerializer序列化value
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(jackson2JsonRedisSerializer));

        // 自定义缓存区域配置
        Map<String, RedisCacheConfiguration> configMap = new HashMap<>();

        configMap.put(CacheKey.USER_AUTHORITY_AREA, defaultConfig.entryTtl(Duration.ofHours(24))); // 用户权限缓存，24小时
        configMap.put(CacheKey.USER_INFO_AREA, defaultConfig.entryTtl(Duration.ofHours(1))); // 用户信息缓存，1小时

        // 社团相关
        configMap.put(CacheKey.ACTIVITY_AREA, defaultConfig.entryTtl(Duration.ofHours(1))); // 活动信息缓存，1小时
        configMap.put(CacheKey.CLUB_AREA, defaultConfig.entryTtl(Duration.ofHours(1))); // 社团信息缓存，1小时
        configMap.put(CacheKey.POST_AREA, defaultConfig.entryTtl(Duration.ofHours(1))); // 帖子缓存，1小时
        configMap.put(CacheKey.POST_DETAIL_AREA, defaultConfig.entryTtl(Duration.ofHours(1))); // 帖子详情缓存，1小时

        // 用户相关
        configMap.put(CacheKey.USER_LIKED_POSTS, defaultConfig.entryTtl(Duration.ofMinutes(5))); // 用户点赞的帖子缓存，5
        configMap.put(CacheKey.USER_COLLECTED_POSTS, defaultConfig.entryTtl(Duration.ofMinutes(5))); // 用户收藏的帖子缓存，5分钟
        configMap.put(CacheKey.USER_SHARED_POSTS, defaultConfig.entryTtl(Duration.ofMinutes(5))); // 用户分享的帖子缓存，5分钟
        configMap.put(CacheKey.USER_FOLLOWERS, defaultConfig.entryTtl(Duration.ofMinutes(5))); // 用户关注的用户缓存，5分钟
        configMap.put(CacheKey.USER_FANS, defaultConfig.entryTtl(Duration.ofMinutes(5))); // 用户粉丝缓存，5分钟
        configMap.put(CacheKey.USER_MUTED_USERS, defaultConfig.entryTtl(Duration.ofMinutes(5))); // 用户屏蔽的用户缓存，5分钟
        configMap.put(CacheKey.USER_BLOCKED_USERS, defaultConfig.entryTtl(Duration.ofMinutes(5))); // 用户拉黑的用户缓存，5分钟

        return RedisCacheManager.builder(factory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(configMap)
                .build();
    }
}



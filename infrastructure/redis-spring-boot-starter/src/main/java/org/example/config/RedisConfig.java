package org.example.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.constant.CacheKey;
import org.springframework.beans.factory.annotation.Qualifier;
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

@Configuration
@EnableCaching
public class RedisConfig {

    /**
     * Redis 专用的 ObjectMapper（启用多态类型）
     */
    @Bean("redisObjectMapper")
    public ObjectMapper redisObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        // 注册 Java 8 时间模块
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerModule(new Jdk8Module());
        // 禁用将日期写成时间戳
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
        // 设置日期格式
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        // 配置多态类型支持
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator.builder()
        .allowIfBaseType(Object.class)
        .build();
        objectMapper.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        // 其他安全设置
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    }

    /**
     * 默认的 ObjectMapper
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    }

    /**
     * 用于 Redis 的序列化器，使用专门的 redisObjectMapper
     */
    @Bean
    public GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer(
            @Qualifier("redisObjectMapper") ObjectMapper objectMapper) {
        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }

    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory connectionFactory,
            GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        // 配置序列化器
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        // 启用事务支持
        redisTemplate.setEnableTransactionSupport(true);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    @Bean
    @Primary
    public CacheManager cacheManager(
            RedisConnectionFactory factory,
            GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer) {
        RedisCacheConfiguration defaultConfig  = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(jackson2JsonRedisSerializer));

        Map<String, RedisCacheConfiguration> configMap = new HashMap<>();
        // 用户认证相关
        configMap.put(CacheKey.USER_AUTHORITY, defaultConfig.entryTtl(Duration.ofHours(24)));
        configMap.put(CacheKey.USER_BRIEF_INFO, defaultConfig.entryTtl(Duration.ofHours(1)));
        configMap.put(CacheKey.USER_ONLINE, defaultConfig.entryTtl(Duration.ofMinutes(5)));
        configMap.put(CacheKey.CAPTCHA, defaultConfig.entryTtl(Duration.ofMinutes(5)));
        // 社区相关
        configMap.put(CacheKey.ACTIVITY, defaultConfig.entryTtl(Duration.ofHours(1)));
        configMap.put(CacheKey.CLUB, defaultConfig.entryTtl(Duration.ofHours(1)));
        configMap.put(CacheKey.POST, defaultConfig.entryTtl(Duration.ofHours(1)));
        configMap.put(CacheKey.POST_DETAIL, defaultConfig.entryTtl(Duration.ofHours(1)));
        // 用户行为相关
        configMap.put(CacheKey.USER_LIKED_POSTS, defaultConfig.entryTtl(Duration.ofMinutes(5)));
        configMap.put(CacheKey.USER_COLLECTED_POSTS, defaultConfig.entryTtl(Duration.ofMinutes(5)));
        configMap.put(CacheKey.USER_SHARED_POSTS, defaultConfig.entryTtl(Duration.ofMinutes(5)));
        configMap.put(CacheKey.USER_FOLLOWERS, defaultConfig.entryTtl(Duration.ofMinutes(5)));
        configMap.put(CacheKey.USER_FANS, defaultConfig.entryTtl(Duration.ofMinutes(5)));
        configMap.put(CacheKey.USER_MUTED_USERS, defaultConfig.entryTtl(Duration.ofMinutes(5)));
        configMap.put(CacheKey.USER_BLOCKED_USERS, defaultConfig.entryTtl(Duration.ofMinutes(5)));
        // 资源相关
        configMap.put(CacheKey.AVATAR_URL, defaultConfig.entryTtl(Duration.ofHours(2)));

        return RedisCacheManager.builder(factory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(configMap)
                .build();
    }
}

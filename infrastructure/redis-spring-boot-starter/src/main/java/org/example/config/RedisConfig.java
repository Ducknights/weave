package org.example.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.constant.CacheKey;
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

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
        objectMapper.disable(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return objectMapper;
    }

    @Bean
    public GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer(ObjectMapper objectMapper) {
        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }

    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory connectionFactory,
            GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
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
        configMap.put(CacheKey.USER_AUTHORITY_AREA, defaultConfig.entryTtl(Duration.ofHours(24)));
        configMap.put(CacheKey.USER_INFO_AREA, defaultConfig.entryTtl(Duration.ofHours(1)));
        configMap.put(CacheKey.CAPTCHA_AREA, defaultConfig.entryTtl(Duration.ofMinutes(5)));
        configMap.put(CacheKey.ACTIVITY_AREA, defaultConfig.entryTtl(Duration.ofHours(1)));
        configMap.put(CacheKey.CLUB_AREA, defaultConfig.entryTtl(Duration.ofHours(1)));
        configMap.put(CacheKey.POST_AREA, defaultConfig.entryTtl(Duration.ofHours(1)));
        configMap.put(CacheKey.POST_DETAIL_AREA, defaultConfig.entryTtl(Duration.ofHours(1)));
        configMap.put(CacheKey.USER_LIKED_POSTS, defaultConfig.entryTtl(Duration.ofMinutes(5)));
        configMap.put(CacheKey.USER_COLLECTED_POSTS, defaultConfig.entryTtl(Duration.ofMinutes(5)));
        configMap.put(CacheKey.USER_SHARED_POSTS, defaultConfig.entryTtl(Duration.ofMinutes(5)));
        configMap.put(CacheKey.USER_FOLLOWERS, defaultConfig.entryTtl(Duration.ofMinutes(5)));
        configMap.put(CacheKey.USER_FANS, defaultConfig.entryTtl(Duration.ofMinutes(5)));
        configMap.put(CacheKey.USER_MUTED_USERS, defaultConfig.entryTtl(Duration.ofMinutes(5)));
        configMap.put(CacheKey.USER_BLOCKED_USERS, defaultConfig.entryTtl(Duration.ofMinutes(5)));
        configMap.put(CacheKey.PRESENTED_URL_AREA, defaultConfig.entryTtl(Duration.ofHours(1)));

        return RedisCacheManager.builder(factory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(configMap)
                .build();
    }
}

package org.example.annotation;

import org.intellij.lang.annotations.Language;
import org.springframework.cache.annotation.Cacheable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解：缓存清除（类比 @CacheEvict）
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Cacheable(cacheNames = "unused", condition = "false") // 仅为了让 IDEA 识别 SpEL 上下文，condition=false 防止 Spring 原生缓存生效
public @interface RedisCacheEvent {

    /** 缓存 key 前缀 */
    String value();

    /** 缓存 key（支持 SpEL 表达式，如 #id） */
    @Language("SpEL")
    String key();
}

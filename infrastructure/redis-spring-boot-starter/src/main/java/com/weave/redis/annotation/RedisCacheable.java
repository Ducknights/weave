package com.weave.redis.annotation;

import org.intellij.lang.annotations.Language;
import org.springframework.cache.annotation.Cacheable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 自定义注解：方法级缓存
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Cacheable(cacheNames = "unused", condition = "false")
public @interface RedisCacheable  {

    /** 缓存 key 前缀 */
    String value();

    /** 缓存 key（支持 SpEL 表达式，如 #id、#user.name） */
    @Language("SpEL")
    String key();

    /** 过期时间 */
    long expire() default 3600;
}

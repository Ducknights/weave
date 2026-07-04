package com.weave.redis.annotation;

import org.intellij.lang.annotations.Language;
import org.springframework.cache.annotation.CachePut;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解：更新缓存
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@CachePut(cacheNames = "unused", condition = "false")
public @interface RedisCachePut {

    String value();

    @Language("SpEL")
    String key();

    long expire() default 36000;
}

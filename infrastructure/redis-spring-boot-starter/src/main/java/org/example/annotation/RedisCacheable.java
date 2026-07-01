package org.example.annotation;

import org.intellij.lang.annotations.Language;
import org.springframework.cache.annotation.Cacheable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解：方法级缓存
 * <p>
 * AOP 学习点：
 * 1. 自定义注解 + 切面 = 声明式缓存
 * 2. 通过 SpEL 表达式灵活指定缓存 key
 * 3. 支持过期时间，模拟真实缓存场景
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

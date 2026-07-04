package com.weave.redis.aspect;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weave.redis.annotation.RedisCachePut;
import com.weave.redis.util.RedisUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import com.weave.redis.annotation.RedisCacheEvent;
import com.weave.redis.annotation.RedisCacheable;
import com.weave.redis.constant.CacheNullValue;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import java.lang.reflect.Type;
import java.time.Duration;

/**
 * 缓存切面 —— 演示 AOP 实现声明式缓存
 * 
 * AOP 学习点：
 * 1. @Around 环绕通知的实战应用
 * 2. 通过反射读取方法注解和参数
 * 3. 实现简单的"先查缓存，缓存没有则执行方法并存入缓存"
 * 
 * 注意：这里用 ConcurrentHashMap 模拟缓存，生产环境应使用 Redis
 */
@Aspect
public class CacheAspect {

    private final RedisUtil redisUtil;
    private final ObjectMapper objectMapper;

    public CacheAspect(RedisUtil redisUtil, ObjectMapper objectMapper) {
        this.redisUtil = redisUtil;
        this.objectMapper = objectMapper;
    }

    /**
     * 基于 @RedisCacheable 的缓存切面
     */
    @Around("@annotation(redisCacheable)")
    public Object around(
            ProceedingJoinPoint joinPoint,
            RedisCacheable redisCacheable) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String redisKey = buildRedisKey(joinPoint, redisCacheable.value(), redisCacheable.key());

        // 获取方法的返回类型
        Type type = signature.getMethod().getGenericReturnType();
        JavaType javaType = objectMapper
                .getTypeFactory()
                .constructType(type);

        Object cache = redisUtil.get(redisKey, javaType);
        // 缓存命中直接返回
        if (cache != null) {
            return cache;
        }

        // Redis 有 Key，但值是 NULL_VALUE
        if (redisUtil.hasKey(redisKey)) {
            return null;
        }

        // 缓存未命中，执行目标方法
        // TODO: 后续可以添加分布式锁，防止缓存击穿
        Object result = joinPoint.proceed();

        // 如果结果为 null，缓存一个“空值”
        if (result == null){
            redisUtil.set(redisKey, CacheNullValue.NULL_VALUE, Duration.ofMinutes(5));
            return null;
        }else{
            Duration ttl = redisCacheable.expire() > 0
                    ? Duration.ofSeconds(redisCacheable.expire())
                    : Duration.ofSeconds(3600);
            redisUtil.set(redisKey, result, ttl);
        }
        return result;
    }

    /**
     * 基于 @RedisCachePut 的缓存切面
     */
    @Around("@annotation(redisCachePut)")
    public Object aroundCachePut(
            ProceedingJoinPoint joinPoint,
            RedisCachePut redisCachePut) throws Throwable {

        // 先执行目标方法
        Object result = joinPoint.proceed();

        // 将结果更新到缓存
        String redisKey = buildRedisKey(joinPoint, redisCachePut.value(), redisCachePut.key());

        if (result == null) {
            redisUtil.set(redisKey, CacheNullValue.NULL_VALUE, Duration.ofMinutes(5));
        } else {
            Duration ttl = redisCachePut.expire() > 0
                    ? Duration.ofSeconds(redisCachePut.expire())
                    : Duration.ofSeconds(3600);
            redisUtil.set(redisKey, result, ttl);
        }

        return result;
    }
    /**
     * 基于 @RedisCacheEvent 的缓存切面
     */
    @Around("@annotation(redisCacheEvent)")
    public Object aroundCacheEvent(
            ProceedingJoinPoint joinPoint,
            RedisCacheEvent redisCacheEvent) throws Throwable {

        // 先执行目标方法
        Object result = joinPoint.proceed();

        // 清除缓存
        String redisKey = buildRedisKey(joinPoint, redisCacheEvent.value(), redisCacheEvent.key());
        redisUtil.delete(redisKey);

        return result;
    }

    /**
     * 解析 SpEL 表达式，构建完整的 Redis key
     */
    private String buildRedisKey(ProceedingJoinPoint joinPoint, String prefix, String spelExpression) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();

        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new StandardEvaluationContext();

        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i], args[i]);
        }

        String key = parser.parseExpression(spelExpression).getValue(context, String.class);
        return prefix + ":" + key;
    }
}

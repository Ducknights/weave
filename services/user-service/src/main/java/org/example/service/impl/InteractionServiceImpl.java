package org.example.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.example.constant.CacheKey;
import org.example.dto.InteractionDto;
import org.example.mapper.InteractionMapper;
import org.example.service.InteractionService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Log4j2
@Service
public class InteractionServiceImpl implements InteractionService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private InteractionMapper interactionMapper;

    @Override
    @Transactional
    public void addRecord(InteractionDto dto) {
        try{
            interactionMapper.insert(dto);
        }catch (DuplicateKeyException e) {
            log.debug("用户 {} 重复执行操作，目标ID: {}，操作类型: {}", dto.userId(), dto.targetId(), dto.type());
        }catch(DataIntegrityViolationException e){
            log.error("执行操作失败，用户ID: {}, 目标ID: {}，操作类型: {}", dto.userId(), dto.targetId(), dto.type(), e);
        }
    }

    @Override
    @Transactional
    public void deleteRecord(InteractionDto dto) {
        interactionMapper.deleteRecord(dto);
    }

    @Override
    public Set<Long> getRecord(InteractionDto dto, int page, int size) {
        Set<Long> result = interactionMapper.getRecord(dto, page, size);
        String key = switch (dto.type()) {
            case LIKE -> CacheKey.buildCacheKey(CacheKey.USER_LIKED_POSTS, dto.userId());
            case FAVORITE -> CacheKey.buildCacheKey(CacheKey.USER_FAVORITE_POSTS, dto.userId());
            case SHARE -> CacheKey.buildCacheKey(CacheKey.USER_SHARED_POSTS, dto.userId());
            case FOLLOW -> CacheKey.buildCacheKey(CacheKey.USER_FOLLOWERS, dto.userId());
            case MUTE -> CacheKey.buildCacheKey(CacheKey.USER_MUTED_USERS, dto.userId());
            case BLOCK -> CacheKey.buildCacheKey(CacheKey.USER_BLOCKED_USERS, dto.userId());
        };
        if (!result.isEmpty()) {
            redisTemplate.opsForSet().add(key, result.toArray(new Long[0]));
        }
        return result;
    }
}

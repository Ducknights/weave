package org.example.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.example.constant.CacheKey;
import org.example.dto.RelationDto;
import org.example.entity.UserRelations;
import org.example.mapper.RelationMapper;
import org.example.service.RelationService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

@Log4j2
@Service
public class RelationServiceImpl implements RelationService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private RelationMapper relationMapper;

    @Override
    public void addRecord(RelationDto dto) {
        UserRelations userRelations = new UserRelations(null, dto.userId(), dto.targetId(), dto.type(), LocalDateTime.now());
        try{
            relationMapper.insert(userRelations);
            redisTemplate.opsForSet().add(buildCacheKey(dto), dto.targetId());
        }catch (DuplicateKeyException e) {
            log.debug("用户 {} 重复执行操作，目标ID: {}，操作类型: {}", dto.userId(), dto.targetId(), dto.type());
        }catch(DataIntegrityViolationException e){
            log.error("执行操作失败，用户ID: {}, 目标ID: {}，操作类型: {}", dto.userId(), dto.targetId(), dto.type(), e);
        }
    }

    @Override
    public void deleteRecord(RelationDto dto) {
        relationMapper.deleteRecord(dto);
        redisTemplate.opsForSet().remove(buildCacheKey(dto), dto.targetId());
    }

    @Override
    public Set<Long> getRecord(RelationDto dto, int page, int size) {
        Set<Long> result = relationMapper.getRecord(dto, page, size);
        if (!result.isEmpty()) {
            redisTemplate.opsForSet().add(buildCacheKey(dto), result.toArray(new Long[0]));
        }
        return result;
    }

    private String buildCacheKey(RelationDto dto) {
        return switch (dto.type()){
           case FOLLOW -> CacheKey.buildCacheKey(CacheKey.USER_FOLLOWERS, dto.userId());
           case MUTE -> CacheKey.buildCacheKey(CacheKey.USER_MUTED_USERS, dto.userId());
           case BLOCK -> CacheKey.buildCacheKey(CacheKey.USER_BLOCKED_USERS, dto.userId());
       };
    }
}

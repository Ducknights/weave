package com.weave.user.service.impl;

import com.weave.user.mapper.RelationMapper;
import com.weave.user.service.RelationService;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import com.weave.redis.constant.CacheKey;
import com.weave.user.model.dto.RelationDto;
import com.weave.user.model.entity.UserRelations;
import com.weave.user.model.eunms.RelationEnum;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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
        try {
            relationMapper.insert(userRelations);
            redisTemplate.opsForSet().add(buildCacheKey(dto), dto.targetId());
        } catch (DuplicateKeyException e) {
            log.debug("用户 {} 重复执行操作，目标ID: {}，操作类型: {}", dto.userId(), dto.targetId(), dto.type());
        } catch (DataIntegrityViolationException e) {
            log.error("执行操作失败，用户ID: {}, 目标ID: {}，操作类型: {}", dto.userId(), dto.targetId(), dto.type(), e);
        }
    }

    @Override
    public void deleteRecord(RelationDto dto) {
        relationMapper.deleteRecord(dto);
        redisTemplate.opsForSet().remove(buildCacheKey(dto), dto.targetId());
    }

    @Override
    public List<Long> getRecord(RelationDto dto, int page, int size) {
        List<Long> result = relationMapper.getRecord(dto, page, size);
        if (!result.isEmpty()) {
            redisTemplate.opsForSet().add(buildCacheKey(dto), result.toArray(new Long[0]));
        }
        return result;
    }

    @Override
    public void cacheUserRelation(Long userId) {
        for (RelationEnum relation : RelationEnum.values()) {
            RelationDto relationDto = new RelationDto(userId, null, relation);
            Set<Long> targetIds = relationMapper.getAllTargetIdsByUserAndType(relationDto);
            if (targetIds != null && !targetIds.isEmpty()) {
                String key = switch (relation) {
                    case FOLLOW -> CacheKey.buildCacheKey(CacheKey.USER_FOLLOWERS, userId);
                    case MUTE -> CacheKey.buildCacheKey(CacheKey.USER_MUTED_USERS, userId);
                    case BLOCK -> CacheKey.buildCacheKey(CacheKey.USER_BLOCKED_USERS, userId);
                };
                try {
                    redisTemplate.opsForSet().add(key, targetIds.toArray(new Long[0]), 1, TimeUnit.DAYS);
                } catch (Exception e) {
                    log.error("缓存用户关系记录失败", e);
                }
            }
        }
    }

    private String buildCacheKey(RelationDto dto) {
        return switch (dto.type()) {
            case FOLLOW -> CacheKey.buildCacheKey(CacheKey.USER_FOLLOWERS, dto.userId());
            case MUTE -> CacheKey.buildCacheKey(CacheKey.USER_MUTED_USERS, dto.userId());
            case BLOCK -> CacheKey.buildCacheKey(CacheKey.USER_BLOCKED_USERS, dto.userId());
        };
    }

}
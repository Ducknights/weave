package org.example.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.example.constant.CacheKey;
import org.example.dto.ActionDto;
import org.example.entity.UserActions;
import org.example.mapper.ActionMapper;
import org.example.service.ActionService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Log4j2
@Service
public class ActionServiceImpl implements ActionService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private ActionMapper actionMapper;

    @Override
    public void addRecord(ActionDto dto) {
        UserActions userActions = new UserActions(null, dto.userId(), dto.targetId(), dto.type(), LocalDateTime.now());
        try {
            actionMapper.insert(userActions);
            redisTemplate.delete(buildCacheKey(dto));
        } catch (DuplicateKeyException e) {
            log.error("重复添加记录", e);
        } catch (Exception e) {
            log.error("添加记录时发生错误", e);
        }
    }

    @Override
    public void deleteRecord(ActionDto dto) {
        try {
            actionMapper.deleteRecord(dto);
            redisTemplate.delete(buildCacheKey(dto));
        } catch (Exception e) {
            log.error("删除记录时发生错误", e);
        }
    }

    @Override
    public Set<Long> getRecord(ActionDto dto, int page, int size) {
        Set<Long> result = actionMapper.getRecord(dto, page, size);
        if (!result.isEmpty()){
            redisTemplate.opsForSet().add(buildCacheKey(dto), result.toArray(new Long[0]));
        }
        return result;
    }

    private String buildCacheKey(ActionDto dto) {
        return switch (dto.type()) {
            case LIKE -> CacheKey.buildCacheKey(CacheKey.USER_LIKED_POSTS, dto.userId());
            case FAVORITE -> CacheKey.buildCacheKey(CacheKey.USER_FAVORITE_POSTS, dto.userId());
            case SHARE -> CacheKey.buildCacheKey(CacheKey.USER_SHARED_POSTS, dto.userId());
        };
    }
}

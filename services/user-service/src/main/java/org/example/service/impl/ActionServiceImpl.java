package org.example.service.impl;

import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.example.constant.CacheKey;
import org.example.dto.PostDetailVo;
import org.example.feign.PostFeignClient;
import org.example.model.dto.ActionDto;
import org.example.model.entity.UserActions;
import org.example.mapper.ActionMapper;
import org.example.model.eunms.ActionEnum;
import org.example.service.ActionService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
public class ActionServiceImpl implements ActionService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private ActionMapper actionMapper;
    @Resource
    private PostFeignClient postFeignClient;

    @Override
    public void addRecord(ActionDto dto) {
        UserActions userActions = new UserActions(null, dto.userId(), dto.targetId(), dto.type(), LocalDateTime.now());
        try {
            // 添加记录
            actionMapper.insert(userActions);
            // 删除缓存
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
            // 删除记录
            actionMapper.deleteRecord(dto);
            // 删除缓存
            redisTemplate.delete(buildCacheKey(dto));
        } catch (Exception e) {
            log.error("删除记录时发生错误", e);
        }
    }

    @Override
    public List<PostDetailVo> getRecord(ActionDto dto, int page, int size) {
        List<Long> postIds = actionMapper.getRecord(dto, page, size);
        if (postIds.isEmpty()) {
            return Collections.emptyList();
        }
        // 缓存帖子ID集合
        redisTemplate.opsForSet().add(buildCacheKey(dto), postIds.toArray(new Long[0]));
        // 通过 Feign 批量获取帖子详情
        return postFeignClient.getPostsByIds(postIds);
    }

    private String buildCacheKey(ActionDto dto) {
        return switch (dto.type()) {
            case LIKE -> CacheKey.buildCacheKey(CacheKey.USER_LIKED_POSTS, dto.userId());
            case COLLECT -> CacheKey.buildCacheKey(CacheKey.USER_COLLECTED_POSTS, dto.userId());
            case VIEW -> CacheKey.buildCacheKey(CacheKey.USER_VIEWED_POSTS, dto.userId());
        };
    }

    @Override
    public void cacheUserAction(Long userId) {
        // 缓存用户操作记录
        for (ActionEnum action : ActionEnum.values()) {
            ActionDto actionDto = new ActionDto(userId,null, action);
            Set<Long> postIds = actionMapper.getAllTargetIdsByUserAndType(actionDto);
            if (postIds != null && !postIds.isEmpty()) {
                String key = switch (action) {
                    case LIKE -> CacheKey.buildCacheKey(CacheKey.USER_LIKED_POSTS, userId);
                    case COLLECT -> CacheKey.buildCacheKey(CacheKey.USER_COLLECTED_POSTS, userId);
                    case VIEW -> CacheKey.buildCacheKey(CacheKey.USER_VIEWED_POSTS, userId);
                };
                try {
                    redisTemplate.opsForSet().add(key, postIds.toArray(new Long[0]), 1, TimeUnit.DAYS);
                } catch (Exception e) {
                    log.error("缓存用户操作记录失败", e);
                }
            }
        }
    }
}

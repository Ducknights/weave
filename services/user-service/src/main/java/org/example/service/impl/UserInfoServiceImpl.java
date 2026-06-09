package org.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.example.constant.CacheKey;
import org.example.model.dto.ActionDto;
import org.example.dto.AuthUserDto;
import org.example.model.dto.RelationDto;
import org.example.dto.UserBriefDto;
import org.example.mapper.ActionMapper;
import org.example.mapper.RelationMapper;
import org.example.mapper.UserInfoMapper;
import org.example.model.entity.UserInfo;
import org.example.model.eunms.ActionEnum;
import org.example.model.eunms.RelationEnum;
import org.example.service.UserInfoService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private ActionMapper actionMapper;
    @Resource
    private RelationMapper relationMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 根据用户ID集合批量获取用户信息
     * @param ids 用户ID集合
     * @return 返回用户ID与用户信息的映射Map
     */
    @Override
    public Map<Long, UserBriefDto> getUserInfosByIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new HashMap<>();
        }

        Map<Long, UserBriefDto> result = new HashMap<>();
        Set<Long> needQueryIds = new HashSet<>();

        // 从缓存中获取用户信息
        ids.forEach(id -> {
            // 尝试从redis中获取用户信息
            String key = CacheKey.buildCacheKey(CacheKey.USER_BRIEF_INFO, id);
            UserBriefDto cachedUser = (UserBriefDto) redisTemplate.opsForValue().get(key);
            // 缓存命中，直接使用缓存中的数据
            if (cachedUser != null) {
                result.put(id, cachedUser);
            } else {
                // 缓存未命中，需要从数据库中查询的id
                needQueryIds.add(id);
            }
        });

        if (!needQueryIds.isEmpty()) {
            // 从数据库中查询
            List<UserInfo> userList = this.listByIds(needQueryIds);
            
            // 记录未找到的用户ID
            Set<Long> notFoundIds = new HashSet<>(needQueryIds);
            
            // 处理查询到的用户
            if (userList != null && !userList.isEmpty()) {
                for (UserInfo user : userList) {
                    // 创建用户简要信息对象并缓存
                    UserBriefDto userBriefDto = new UserBriefDto(user.getId(), user.getName(), user.getAvatar());
                    String key = CacheKey.buildCacheKey(CacheKey.USER_BRIEF_INFO, user.getId());
                    redisTemplate.opsForValue().set(key, userBriefDto);
                    // 将用户信息添加到结果集中
                    result.put(user.getId(), userBriefDto);
                    notFoundIds.remove(user.getId());
                }
            }
            
            // 处理未找到的用户，构建空对象并缓存
            for (Long id : notFoundIds) {
                UserBriefDto emptyUser = UserBriefDto.buildEmpty(id);
                String key = CacheKey.buildCacheKey(CacheKey.USER_BRIEF_INFO, id);
                redisTemplate.opsForValue().set(key, emptyUser);
                result.put(id, emptyUser);
            }
        }
        return result;
    }

    /**
     * 创建用户
     * @param userDto 用户信息
     * @return 返回创建的用户信息
     */
    @Override
    @CacheEvict(value = CacheKey.USER_BRIEF_INFO, key = "#userDto.id")
    public UserInfo createUser(AuthUserDto userDto) {
        UserInfo user = new UserInfo();
        user.setId(userDto.id());
        user.setName("用户"+userDto.id());
        user.setEmail(userDto.email());
        userInfoMapper.insert(user);
        return user;
    }

    /**
     * 根据用户ID获取用户信息
     * @param id 用户ID
     * @return 返回用户信息
     */
    @Override
    @Cacheable(value = CacheKey.USER_BRIEF_INFO, key = "#id")
    public UserBriefDto getUserBriefDtoById(Long id) {
        UserInfo userInfo = userInfoMapper.selectById(id);
        if (userInfo == null){
            return UserBriefDto.buildEmpty(id);
        }
        return new UserBriefDto(
                userInfo.getId(),
                userInfo.getName(),
                userInfo.getAvatar()
        );
    }

    @Override
    public UserInfo getSelfInfo(Long id) {
        return userInfoMapper.selectById(id);
    }

    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 返回更新后的用户信息
     */
    @Override
    @Transactional
    @CacheEvict(value = CacheKey.USER_BRIEF_INFO, key = "#user.id")
    public UserInfo updateUser(UserInfo user) {
        if(userInfoMapper.updateInfo(user) > 0){
            return user;
        }else {
            throw new RuntimeException("用户信息更新失败");
        }
    }

    /**
     * 刷新用户在线状态
     * @param id 用户ID
     * @return 返回是否刷新成功
     */
    @Override
    @CachePut(value = CacheKey.USER_ONLINE,key = "#id")
    public Boolean refresh(Long id) {
        log.info("id:{}的用户维持心跳",id);
        return true;
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
        // 缓存用户关系记录
        for (RelationEnum relation : RelationEnum.values()) {
            RelationDto relationDto = new RelationDto(userId,null, relation);
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
}
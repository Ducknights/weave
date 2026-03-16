package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.log4j.Log4j2;
import org.example.constant.CacheKey;
import org.example.dto.AuthUserDto;
import org.example.feign.UserAvatarFeign;
import org.example.mapper.UserInfoMapper;
import org.example.entity.UserInfo;
import org.example.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Resource
    private UserInfoMapper userInfoMapper;
    @Resource
    private UserAvatarFeign userAvatarFeign;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 根据用户ID集合批量获取用户信息
     * @param ids 用户ID集合
     * @return 返回用户ID与用户信息的映射Map
     */
    @Override
    public Map<Long, UserInfo> getUserInfosByIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new HashMap<>();
        }

        Map<Long, UserInfo> result = new HashMap<>();
        Set<Long> idsToQuery = new HashSet<>();

        ids.forEach(id -> {
            // 尝试从redis中获取用户信息
            String key = CacheKey.buildCacheKey(CacheKey.USER_INFO_AREA, id);
            UserInfo cachedUser = (UserInfo) redisTemplate.opsForValue().get(key);
            if (cachedUser != null) {
                result.put(id, cachedUser);
            } else {
                // 缓存未命中，需要从数据库中查询的id
                idsToQuery.add(id);
            }
        });

        if (!idsToQuery.isEmpty()) {
            // 从数据库中查询
            List<UserInfo> userList = userInfoMapper.selectList(
                new LambdaQueryWrapper<UserInfo>().in(UserInfo::getId, idsToQuery)
            );
            // TODO: 2023/3/20 批量操作（用户信息和 redis）
            if (userList != null && !userList.isEmpty()) {
                for (UserInfo user : userList) {
                    user.setAvatar(userAvatarFeign.getFileUrl(user.getAvatar(),3600));
                    String key = CacheKey.buildCacheKey(CacheKey.USER_INFO_AREA, user.getId());
                    redisTemplate.opsForValue().set(key, user, 1, TimeUnit.HOURS);
                    result.put(user.getId(), user);
                }
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
    @Cacheable(value = CacheKey.USER_INFO_AREA,key = "#id")
    public UserInfo getUserById(Long id) {
        UserInfo user = userInfoMapper.selectById(id);
        user.setAvatar(userAvatarFeign.getFileUrl(user.getAvatar(),3600));
        return user;
    }

    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 返回更新后的用户信息
     */
    @Override
    @CacheEvict(value = CacheKey.USER_INFO_AREA,key = "#user.id")
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
    @CachePut(value = CacheKey.USER_ONLINE_AREA,key = "#id")
    public Boolean refresh(Long id) {
        log.info("id:{}的用户维持心跳",id);
        return true;
    }

}
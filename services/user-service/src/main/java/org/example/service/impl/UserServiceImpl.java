package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.example.strings.CacheKey;
import org.example.dto.AuthUserDto;
import org.example.mapper.UserMapper;
import org.example.entity.UserInfo;
import org.example.service.UserService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public Map<Long, UserInfo> getUserInfosByIds(Set<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return new HashMap<>();
        }

        // 使用MyBatis-Plus的内置方法根据ID列表查询（使用LambdaQueryWrapper）
        List<UserInfo> userList = userMapper.selectList(
            new LambdaQueryWrapper<UserInfo>().in(UserInfo::getId, userIds)
        );

        // 将查询结果转换为Map形式
        Map<Long, UserInfo> userMap = new HashMap<>();
        if (userList != null && !userList.isEmpty()) {
            for (UserInfo user : userList) {
                // 将用户信息缓存到Redis，设置缓存时间为1小时
                String key = CacheKey.buildCacheKey(CacheKey.USER_INFO_AREA, user.getId());
                redisTemplate.opsForValue().set(key, user, 1, TimeUnit.HOURS);
                userMap.put(user.getId(), user);
            }
        }
        return userMap;
    }

    @Override
    public UserInfo createUser(AuthUserDto userDto) {
        UserInfo user = new UserInfo();
        user.setId(userDto.getId());
        user.setName("用户"+userDto.getId());
        user.setEmail(userDto.getEmail());
        userMapper.insert(user);
        return user;
    }

    @Override
    @Cacheable(value = CacheKey.USER_INFO_AREA, key = "#id")
    public UserInfo getUserById(Long id) {
        return userMapper.selectById(id);
    }
}
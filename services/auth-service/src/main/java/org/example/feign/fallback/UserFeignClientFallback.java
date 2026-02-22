package org.example.feign.fallback;

import org.example.dto.UserDto;
import org.example.entity.UserAuth;
import org.example.feign.UserFeignClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * UserFeignClient的熔断器实现类
 */
@Component
public class UserFeignClientFallback implements UserFeignClient {

    private static final Logger logger = LoggerFactory.getLogger(UserFeignClientFallback.class);

    @Override
    public void createUser(UserAuth user) {
        logger.error("调用user-service创建用户失败，已熔断");
        // 可以记录到数据库或消息队列，稍后重试
    }

    @Override
    public UserDto getUserById(Long id) {
        logger.error("调用user-service获取用户信息失败，已熔断，用户ID: {}", id);
        // 返回默认的用户信息或抛出异常
        UserDto userDto = new UserDto();
        userDto.setId(id);
        userDto.setName("未知用户");
        return userDto;
    }
}
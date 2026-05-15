package org.example.feign;

import org.example.dto.UserBriefDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 用户信息 Feign 客户端
 * 用于调用 user-service 获取用户信息
 */
@FeignClient(name = "user-service")
public interface UserInfoFeign {
    
    /**
     * 批量获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    @PostMapping("/internal/user")
    UserBriefDto getUserInfosById(@RequestBody Long userId);
}

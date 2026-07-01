package org.example.feign;

import org.example.model.dto.UserBriefDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;
import java.util.Set;

/**
 * 用户信息 Feign 客户端
 * 用于调用 user-service 获取用户信息
 */
@FeignClient(name = "user-service")
public interface UserFeignClient {
    
    /**
     * 批量获取用户信息
     * @param Ids 用户ID
     * @return 用户信息
     */
    @PostMapping("/api/user/info/batch")
    Map<Long, UserBriefDto> getUserBriefInfosByIds(@RequestBody Set<Long> Ids);
}

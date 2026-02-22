package org.example.feign;

import org.example.dto.UserDto;
import org.example.entity.UserAuth;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.example.feign.fallback.UserFeignClientFallback;

@FeignClient(name = "user-service", fallback = UserFeignClientFallback.class)
public interface UserFeignClient {
    /**
     * 创建用户
     *
     * @param user 用户信息
     */
    @PostMapping("/user/register")
    void createUser(UserAuth user);

    /**
     * 根据用户ID获取用户信息
     *
     * @param id 用户ID，通过路径变量传递
     * @return 返回UserDto对象，包含用户详细信息
     */
    @GetMapping("/api/user/info/{id}")
    UserDto getUserById(@PathVariable Long id);
}

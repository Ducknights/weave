package org.example.feign;

import org.example.model.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;
import java.util.Set;

@FeignClient(name = "user-service")
public interface UserInfoFeign {
    /**
     * 批量获取用户信息
     * @param userIds 用户ID集合
     * @return 用户信息Map，key为用户ID，value为用户信息
     */
    @PostMapping("/batch")
    Map<Long, User> getUserInfosByIds(@RequestBody Set<Long> userIds);
}

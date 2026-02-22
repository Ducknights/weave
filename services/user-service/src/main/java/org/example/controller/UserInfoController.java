package org.example.controller;

import jakarta.annotation.Resource;
import org.example.dto.AuthUserDto;
import org.example.entity.UserInfo;
import org.example.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/user/info")
public class UserInfoController {

    @Resource
    private UserService userService;

    /**
     * 处理用户注册请求的接口方法
     *
     * @param user 包含用户注册信息的AuthUserDto对象，通过请求体传递
     * @return UserInfo 返回创建成功的用户信息对象
     */
    @PostMapping()
    public UserInfo createUser(@RequestBody AuthUserDto user) {
        return userService.createUser(user);
    }

    /**
     * 根据用户ID获取用户信息的接口方法
     *
     * @param id 用户ID，通过路径变量传递
     * @return UserInfo 返回用户信息对象
     */
    @GetMapping("/{id}")    // HTTP GET请求映射，用于处理获取指定ID的用户信息请求
    public UserInfo getUserById(@PathVariable Long id) {    // 方法参数id通过路径变量绑定
        return userService.getUserById(id);    // 调用userService中的getUserById方法获取用户信息并返回
    }

    /**
     * 根据用户ID批量获取用户信息
     *
     * @param userIds 用户ID集合
     * @return 返回一个Map，键为用户ID，值为对应的用户信息对象
     */
    @PostMapping("/batch")
    public Map<Long, UserInfo> getUserInfosByIds(@RequestBody Set<Long> userIds) {
        // 调用userService的getUserInfosByIds方法获取用户信息
        return userService.getUserInfosByIds(userIds);
    }
}

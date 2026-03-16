package org.example.controller;

import jakarta.annotation.Resource;
import org.example.dto.AuthUserDto;
import org.example.entity.UserInfo;
import org.example.service.UserInfoService;
import org.example.util.SecurityUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Resource
    private UserInfoService userInfoService;

    /**
     * 处理用户注册请求的接口方法
     *
     * @param user 包含用户注册信息的AuthUserDto对象，通过请求体传递
     * @return UserInfo 返回创建成功的用户信息对象
     */
    @PostMapping()
    public UserInfo createUser(@RequestBody AuthUserDto user) {
        return userInfoService.createUser(user);
    }

    /**
     * 获取当前用户信息
     *
     * @return UserInfo 返回用户信息对象
     */
    @GetMapping()
    public UserInfo getSelfUserInfo() {
        Long id = SecurityUtils.getCurrentUserId();
        return userInfoService.getUserById(id);
    }

    /**
     * 根据ID获取用户信息的接口方法
     *
     * @param id 用户ID，通过路径变量传递
     * @return UserInfo 返回用户信息对象
     */
    @GetMapping("/{id}")
    public UserInfo getUserById(@PathVariable Long id) {
        return userInfoService.getUserById(id);
    }

    /**
     * 根据用户ID批量获取用户信息
     *
     * @param ids 用户ID集合
     * @return 返回一个Map，键为用户ID，值为对应的用户信息对象
     */
    @PostMapping("/batch")
    public Map<Long, UserInfo> getUserInfosByIds(@RequestBody Set<Long> ids) {
        return userInfoService.getUserInfosByIds(ids);
    }

    /**
     * 更新用户信息
     *
     */
    @PutMapping()
    public UserInfo updateUser(@RequestBody UserInfo user) {
        Long userId = SecurityUtils.getCurrentUserId();
        user.setId(userId);
        return userInfoService.updateUser(user);
    }

    /**
     * 处理用户在线心跳请求的方法
     * 保持用户在线状态
     */
    @PostMapping("/online")
    public Boolean heartBeat () {
        Long userId = SecurityUtils.getCurrentUserId();
        return userInfoService.refresh(userId);
    }
}

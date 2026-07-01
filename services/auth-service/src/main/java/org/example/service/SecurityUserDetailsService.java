package org.example.service;

import jakarta.annotation.Resource;
import org.example.model.dto.AuthUserDto;
import org.example.model.CustomUserDetails;
import org.example.model.UserAuth;
import org.example.feign.UserFeignClient;
import org.example.mapper.AuthMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SecurityUserDetailsService implements UserDetailsManager {

    @Resource
    private AuthMapper authMapper;
    @Resource
    private UserFeignClient userFeignClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CustomUserDetails userDetails = authMapper.selectUserDetailsByEmail(username);
        if (userDetails == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        return userDetails;
    }

    @Override
    public void createUser(UserDetails user) {
        UserAuth userAuth = new UserAuth();
        userAuth.setEmail(user.getUsername());
        userAuth.setPassword(user.getPassword());
        // 插入用户信息
        authMapper.insert(userAuth);
        // 插入用户角色
        authMapper.insertUserRole(userAuth.getId());
        // 调用用户服务插入用户信息
        userFeignClient.createUser(new AuthUserDto(userAuth.getId(), userAuth.getEmail()));
    }

    @Override
    public void updateUser(UserDetails user) {

    }

    @Override
    public void deleteUser(String username) {

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }

    @Override
    public boolean userExists(String username) {
        return false;
    }

}

package org.example.service;

import jakarta.annotation.Resource;
import org.example.entity.MyUserDetails;
import org.example.entity.UserAuth;
import org.example.feign.UserFeignClient;
import org.example.mapper.AuthMapper;
import org.example.mapper.AuthorityMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SecurityUserDetailsService implements UserDetailsManager {

    @Resource
    private AuthMapper authMapper;
    @Resource
    private AuthorityMapper authorityMapper;
    @Resource
    private UserFeignClient userFeignClient;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAuth userAuth = authMapper.selectUserByEmail(username);
        if (userAuth == null) {
            throw new UsernameNotFoundException("用户不存在");
        }
        List<String> authorities = authorityMapper.selectUserPermissionById(userAuth.getId());
        return new MyUserDetails(userAuth, authorities);
    }

    @Override
    public void createUser(UserDetails user) {
        UserAuth userAuth = new UserAuth();
        userAuth.setEmail(user.getUsername());
        userAuth.setPassword(user.getPassword());
        UserAuth newUser = authMapper.insertUser(userAuth);
        userFeignClient.createUser(newUser);
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

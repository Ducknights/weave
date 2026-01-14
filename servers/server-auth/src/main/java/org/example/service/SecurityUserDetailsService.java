package org.example.service;

import jakarta.annotation.Resource;
import org.example.entity.MyUserDetails;
import org.example.entity.UserAuth;
import org.example.mapper.AuthMapper;
import org.example.mapper.AuthorityMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.example.config.RabbitMQConfig.TOPIC_EXCHANGE;
import static org.example.config.RabbitMQConfig.USER_ROUTING_KEY;

@Service
public class SecurityUserDetailsService implements UserDetailsManager {

    @Resource
    private AuthMapper authMapper;
    @Resource
    private AuthorityMapper authorityMapper;
    @Resource
    private RabbitTemplate rabbitTemplate;

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
        authMapper.insertUser(userAuth);
        rabbitTemplate.convertAndSend(TOPIC_EXCHANGE, USER_ROUTING_KEY, userAuth.getEmail());
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

package org.example.service;

import jakarta.annotation.Resource;
import org.example.entity.MyUserDetails;
import org.example.entity.UserAuth;
import org.example.mapper.AuthMapper;
import org.example.mapper.AuthorityMapper;
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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAuth userAuth = authMapper.selectUserByEmail(username);
        if (userAuth == null) {
            throw new UsernameNotFoundException("User not found");
        }
        List<String> authorities = authorityMapper.selectUserPermissionById(userAuth.getId());
        return new MyUserDetails(userAuth, authorities);
    }

    @Override
    public void createUser(UserDetails user) {
        UserAuth userAuth = new UserAuth();
        userAuth.setEmail(user.getUsername());
        userAuth.setPassword(user.getPassword());
        System.out.println(userAuth);
        authMapper.insertUser(userAuth);
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

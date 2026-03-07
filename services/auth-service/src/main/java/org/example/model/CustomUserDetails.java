package org.example.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomUserDetails implements UserDetails {

    @TableId  // 指定主键
    private Long userId;

    @TableField("email") // 邮箱字段
    private String username;

    @JsonIgnore // 忽略密码字段，防止序列化时输出
    private String password;

    @TableField(exist = false) // 非数据库字段，角色
    private List<String> roles;

    @TableField(exist = false) // 非数据库字段，权限列表
    private List<String> authorities;

    @JsonIgnore
    @TableField(exist = false)
    private String rolesStr;

    @JsonIgnore
    @TableField(exist = false)
    private String authoritiesStr;

    // 获取 roles，如果为空则从 rolesStr 转换
    public List<String> getRoles() {
        if (roles == null && rolesStr != null) {
            roles = parseStringToList(rolesStr);
        }
        return roles != null ? roles : Collections.emptyList();
    }

    // 获取 authorities，如果为空则从 authoritiesStr 转换
    @JsonProperty("authorities")
    public List<String> getAuthoritiesForJson() {
        return getAuthoritiesList();
    }
    private List<String> getAuthoritiesList() {
        if (authorities == null && authoritiesStr != null) {
            authorities = parseStringToList(authoritiesStr);
        }
        return authorities != null ? authorities : Collections.emptyList();
    }

    private List<String> parseStringToList(String str) {
        if (str == null || str.isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.asList(str.split(","));
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<String> authorities = getAuthoritiesList();
        return authorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }
}

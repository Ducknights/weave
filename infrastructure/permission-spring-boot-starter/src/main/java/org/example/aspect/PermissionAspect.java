package org.example.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.annotation.RequirePermission;
import org.example.context.UserContext;
import org.example.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.HashSet;

@Aspect
@Component
public class PermissionAspect {

    @Autowired
    private UserContext userContext;

    @Around("@annotation(requirePermission)")
    public Object checkPermission(
            ProceedingJoinPoint joinPoint,
            RequirePermission requirePermission)
            throws Throwable {

        // 检查角色
        String[] roles = requirePermission.roles();
        if (roles.length > 0) {
            boolean hasRole = checkRoles(roles, requirePermission.logical());
            if (!hasRole) {
                throw new UnauthorizedException("没有所需角色");
            }
        }

        // 检查权限
        String[] permissions = requirePermission.permissions();
        if (permissions.length > 0) {
            boolean hasPermission = checkPermissions(permissions, requirePermission.logical());
            if (!hasPermission) {
                throw new UnauthorizedException("没有所需权限");
            }
        }

        return joinPoint.proceed();
    }

    /**
     * 检查角色
     *
     * @param requiredRoles 需要的角色
     * @param logical       逻辑操作类型
     * @return 是否有权限
     */
    private boolean checkRoles(String[] requiredRoles, RequirePermission.Logical logical) {
        if (userContext == null || CollectionUtils.isEmpty(userContext.getRoles())) {
            return false;
        }

        if (logical == RequirePermission.Logical.AND) {
            // 需要所有角色
            return new HashSet<>(userContext.getRoles()).containsAll(Arrays.asList(requiredRoles));
        } else {
            // 只需任一角色
            return Arrays.stream(requiredRoles)
                    .anyMatch(role -> userContext.getRoles().contains(role));
        }
    }

    /**
     * 检查权限
     *
     * @param requiredPermissions 需要的权限
     * @param logical             逻辑操作类型
     * @return 是否有权限
     */
    private boolean checkPermissions(String[] requiredPermissions, RequirePermission.Logical logical) {
        if (userContext == null || CollectionUtils.isEmpty(userContext.getPermissions())) {
            return false;
        }

        if (logical == RequirePermission.Logical.AND) {
            return new HashSet<>(userContext.getPermissions()).containsAll(Arrays.asList(requiredPermissions));
        } else {
            return Arrays.stream(requiredPermissions)
                    .anyMatch(permission -> userContext.getPermissions().contains(permission));
        }
    }
}

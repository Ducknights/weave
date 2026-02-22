package org.example.annotation;

import java.lang.annotation.*;

/**
 * 自定义权限注解
 * 用于标记需要特定角色才能访问的方法
 */
@Target({ElementType.METHOD, ElementType.TYPE})  // 可以用在方法或类上
@Retention(RetentionPolicy.RUNTIME)               // 运行时保留
@Documented
public @interface RequireRole {

    /**
     * 需要的角色
     * 可以指定多个角色，满足其中一个即可
     */
    String[] value() default {};

    /**
     * 逻辑操作类型
     * AND: 需要满足所有角色
     * OR: 满足其中一个角色即可
     */
    Logical logical() default Logical.OR;

    /**
     * 权限不足时的提示信息
     */
    String message() default "权限不足，无法访问";
}

package org.example.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})  // 可以用在方法或类上
@Retention(RetentionPolicy.RUNTIME)               // 运行时保留
@Documented
public @interface RequirePermission {

    /**
     * 需要的权限
     * 可以指定多个权限
     */
    String[] permissions() default {};

    /**
     * 逻辑操作类型
     * AND: 需要满足所有
     * OR: 只需满足一个（默认）
     */
    Logical logical() default Logical.OR;

    /**
     * 需要的角色
     * 可以指定多个角色
     */
    String[] roles() default {};


    enum Logical {
        AND,
        OR
    }
}

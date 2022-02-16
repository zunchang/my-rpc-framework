package com.framework.rpc.annotation;

import java.lang.annotation.*;

/**
 * 消费服务注解
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface RpcReference {
    // 服务版本，默认值为空字符串
    String version() default "";

    // 服务组，默认值为空字符串
    String group() default "";
}

package com.framework.rpc.annotation;

import java.lang.annotation.*;

/**
 * RPC服务注解，标注在服务实现类上
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface RpcService {

    /**
     * 服务版本，默认值为空字符串
     *
     * @return
     */
    String version() default "";

    /**
     * 服务组，默认值为空字符串
     *
     * @return
     */
    String group() default "";
}

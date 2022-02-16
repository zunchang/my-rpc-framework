package com.framework.rpc.test;

import java.lang.reflect.Proxy;

public class JdkProxyFactory {
    public static Object getProxy(Object target){
        return Proxy.newProxyInstance(
                // 目标类加载
                target.getClass().getClassLoader(),
                // 代理需要实现的接口
                target.getClass().getInterfaces(),
                // 代理对象对应的定义
                new DebugInvocationHandler(target)
        );
    }
}

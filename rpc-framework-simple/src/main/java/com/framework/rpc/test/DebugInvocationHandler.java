package com.framework.rpc.test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class DebugInvocationHandler implements InvocationHandler {

    // 代理类中的真实对象
    private final Object target;

    public DebugInvocationHandler(Object target){
        this.target=target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 调用方法之前 可以加入自己的操作
        System.out.println("before method "+method.getName());
        Object invoke = method.invoke(target, args);
        System.out.println("调用之后 "+method.getName());
        return invoke;
    }
}

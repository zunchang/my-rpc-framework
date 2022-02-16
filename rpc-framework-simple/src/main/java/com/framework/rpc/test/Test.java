package com.framework.rpc.test;

public class Test {
    public static void main(String[] args) {
        SmsService proxy = (SmsService)JdkProxyFactory.getProxy(new SmsServiceImpl());
        proxy.send("java");
    }
}

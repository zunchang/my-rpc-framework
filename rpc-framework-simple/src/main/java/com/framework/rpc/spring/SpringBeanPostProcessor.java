package com.framework.rpc.spring;

import com.framework.common.extension.ExtensionLoader;
import com.framework.common.factory.SingletonFactory;
import com.framework.rpc.annotation.RpcReference;
import com.framework.rpc.annotation.RpcService;
import com.framework.rpc.config.RpcServiceConfig;
import com.framework.rpc.provider.ServiceProvider;
import com.framework.rpc.provider.impl.ZkServiceProviderImpl;
import com.framework.rpc.proxy.RpcClientProxy;
import com.framework.rpc.remoting.transprot.RpcRequestTransport;
import io.protostuff.Rpc;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * 创建Bean之前调用该方法
 */
@Slf4j
@Component
public class SpringBeanPostProcessor implements BeanPostProcessor {

    private final ServiceProvider serviceProvider;
    private final RpcRequestTransport rpcClient;

    public SpringBeanPostProcessor() {
        this.serviceProvider = SingletonFactory.getInstance(ZkServiceProviderImpl.class);
        this.rpcClient = ExtensionLoader.getExtensionLoader(RpcRequestTransport.class).getExtension("netty");
    }

    /**
     * 初始化前调用
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @SneakyThrows
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(RpcService.class)) {
            log.info("[{}] 注解为 [{}]", bean.getClass().getName(), RpcService.class.getCanonicalName());
            // 获取RpcService 注解
            RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
            // 构建 RpcServiceProperties
            RpcServiceConfig rpcServiceConfig = RpcServiceConfig.builder()
                    .group(rpcService.group())
                    .version(rpcService.version())
                    .service(bean)
                    .build();
            serviceProvider.publishService(rpcServiceConfig);
        }
        return bean;
    }

    /**
     * 初始化后调用
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = bean.getClass();
        Field[] declaredFields = targetClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            RpcReference rpcReference = declaredField.getAnnotation(RpcReference.class);
            if(rpcReference!=null){
                RpcServiceConfig rpcServiceConfig= RpcServiceConfig.builder()
                        .group(rpcReference.group())
                        .version(rpcReference.version())
                        .build();
                RpcClientProxy rpcClientProxy=new RpcClientProxy(rpcClient,rpcServiceConfig);
                Object clientProxy=rpcClientProxy.getProxy(declaredField.getType());
                declaredField.setAccessible(true);
                try {
                    declaredField.set(bean, clientProxy);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

        }
        return bean;
    }
}

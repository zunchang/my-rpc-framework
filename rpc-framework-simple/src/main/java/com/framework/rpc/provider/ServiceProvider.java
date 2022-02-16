package com.framework.rpc.provider;

import com.framework.rpc.config.RpcServiceConfig;

/**
 * 存储和提供服务
 */
public interface ServiceProvider {

    /**
     * @param rpcServiceConfig rpc服务相关属性
     */
    void addService(RpcServiceConfig rpcServiceConfig);

    /**
     * @param rpcServiceName rpc服务名称
     * @return service object
     */
    Object getService(String rpcServiceName);

    /**
     * @param rpcServiceConfig rpc服务相关属性
     */
    void publishService(RpcServiceConfig rpcServiceConfig);


}

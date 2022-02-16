package com.framework.rpc.registry;

import com.framework.common.extension.SPI;
import com.framework.rpc.remoting.dto.RpcRequest;

import java.net.InetSocketAddress;

/**
 * 服务注册
 */
@SPI
public interface ServiceRegistry {

    /**
     * 服务注册
     *
     * @param rpcService 服务名称
     * @param inetSocketAddress 服务地址
     */

    void registerService(String rpcService,InetSocketAddress inetSocketAddress);
}

package com.framework.rpc.registry;

import com.framework.common.extension.SPI;
import com.framework.rpc.remoting.dto.RpcRequest;

import java.net.InetSocketAddress;

/**
 * 服务发现
 */

@SPI
public interface ServiceDiscovery {
    /**
     * 通过RpcServiceName查找服务
     * @param request
     * @return
     */
    InetSocketAddress lookupService(RpcRequest request);
}

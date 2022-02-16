package com.framework.rpc.loadbalance;

import com.framework.common.extension.SPI;
import com.framework.rpc.remoting.dto.RpcRequest;

import java.util.List;

/**
 * 负载均衡策略接口
 */

@SPI
public interface LoadBalance {

    /**
     * 从现有的服务列表中选择一个
     * @param serviceAddresses
     * @param rpcRequest
     * @return 目标服务地址
     */
    String selectServiceAddress(List<String> serviceAddresses, RpcRequest rpcRequest);
}

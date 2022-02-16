package com.framework.rpc.loadbalance;

import com.framework.rpc.remoting.dto.RpcRequest;

import java.util.List;

/**
 * 负载均衡策略的抽象类
 */
public abstract class AbstractLoadBalance implements LoadBalance {
    @Override
    public String selectServiceAddress(List<String> serviceAddresses, RpcRequest rpcRequest) {
        if (serviceAddresses == null || serviceAddresses.size() == 0) {
            return null;
        }
        if (serviceAddresses.size() == 1) {
            return serviceAddresses.get(0);
        }
        return doSelect(serviceAddresses,rpcRequest);

    }

    protected abstract String doSelect(List<String> serviceAddresses, RpcRequest rpcRequest);

}

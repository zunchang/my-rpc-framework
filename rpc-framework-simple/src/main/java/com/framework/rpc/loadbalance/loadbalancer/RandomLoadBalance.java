package com.framework.rpc.loadbalance.loadbalancer;

import com.framework.rpc.loadbalance.AbstractLoadBalance;
import com.framework.rpc.remoting.dto.RpcRequest;

import java.util.List;
import java.util.Random;

public class RandomLoadBalance extends AbstractLoadBalance {
    @Override
    protected String doSelect(List<String> serviceAddresses, RpcRequest rpcRequest) {
        Random random=new Random();
        return serviceAddresses.get(random.nextInt(serviceAddresses.size()));
    }
}

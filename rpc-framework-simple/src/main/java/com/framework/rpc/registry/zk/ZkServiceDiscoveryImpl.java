package com.framework.rpc.registry.zk;

import com.framework.common.enums.RpcErrorMessageEnum;
import com.framework.common.exception.RpcException;
import com.framework.common.extension.ExtensionLoader;
import com.framework.rpc.loadbalance.LoadBalance;
import com.framework.rpc.registry.ServiceDiscovery;
import com.framework.rpc.registry.zk.utils.CuratorUtils;
import com.framework.rpc.remoting.dto.RpcRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 基于zookeeper的服务发现
 */

@Slf4j
public class ZkServiceDiscoveryImpl implements ServiceDiscovery {


    private final LoadBalance loadBalance;

    public ZkServiceDiscoveryImpl() {
        this.loadBalance = ExtensionLoader.getExtensionLoader(LoadBalance.class).getExtension("loadBalance");
    }

    @Override
    public InetSocketAddress lookupService(RpcRequest request) {
        String rpcServiceName = request.getRpcServiceName();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        // 获取子节点
        List<String> serviceUrlList = CuratorUtils.getChildrenNodes(zkClient, rpcServiceName);
        if (serviceUrlList == null || serviceUrlList.size() == 0) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND, rpcServiceName);
        }
        String loadBalanceUrl = loadBalance.selectServiceAddress(serviceUrlList, request);
        log.info("获取服务列表 [{}]", loadBalanceUrl);
        String[] socketAddressArray = loadBalanceUrl.split(":");
        String host = socketAddressArray[0];
        // 端口号
        int port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host, port);
    }
}

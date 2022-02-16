package com.framework.rpc.registry.zk;

import com.framework.rpc.registry.ServiceRegistry;
import com.framework.rpc.registry.zk.utils.CuratorUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;

/**
 * 服务注册 基于zookeeper
 */
@Slf4j
public class ZkServiceRegistryImpl implements ServiceRegistry {

    @Override
    public void registerService(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        String servicePath= CuratorUtils.ZK_REGISTER_ROOT_PATH+"/"+rpcServiceName+inetSocketAddress.toString();
        CuratorFramework zkClient=CuratorUtils.getZkClient();
        CuratorUtils.createPersistentNode(zkClient,servicePath);
    }
}

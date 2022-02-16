package com.framework.rpc.remoting.transprot.socket;

import com.framework.common.factory.SingletonFactory;
import com.framework.common.utils.threadpool.ThreadPoolFactoryUtils;
import com.framework.rpc.config.CustomShutdownHook;
import com.framework.rpc.config.RpcServiceConfig;
import com.framework.rpc.provider.ServiceProvider;
import com.framework.rpc.provider.impl.ZkServiceProviderImpl;
import com.framework.rpc.remoting.dto.RpcRequest;
import com.framework.rpc.remoting.transprot.RpcRequestTransport;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

@Slf4j
public class SocketRpcServer {

    public static final int PORT = 9998;

    private final ExecutorService threadPool;
    private final ServiceProvider serviceProvider;

    public SocketRpcServer() {
        this.threadPool = ThreadPoolFactoryUtils.createCustomThreadPoolIfAbsent("socket-server-rpc-poot");
        this.serviceProvider = SingletonFactory.getInstance(ZkServiceProviderImpl.class);
    }

    // 注册服务
    public void registerService(RpcServiceConfig rpcServiceConfig) {
        // 发布服务
        serviceProvider.publishService(rpcServiceConfig);
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket()) {
            String host = InetAddress.getLocalHost().getHostAddress();
            serverSocket.bind(new InetSocketAddress(host, PORT));
            CustomShutdownHook.getCustomShutdownHook().clearAll();
            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                log.info("客户端已连接 [{}]", socket.getInetAddress());
                threadPool.execute(new SocketRpcRequestHandlerRunnable(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

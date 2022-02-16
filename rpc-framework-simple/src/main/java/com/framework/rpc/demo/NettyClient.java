package com.framework.rpc.demo;

import com.framework.rpc.remoting.dto.RpcRequest;
import com.framework.rpc.remoting.dto.RpcResponse;
import com.framework.rpc.serialize.kyro.KryoSerializer;
import com.framework.rpc.test.NettyKryoDecoder;
import com.framework.rpc.test.NettyKryoEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    private final String host;
    private final int port;
    private static final Bootstrap b;

    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }
    // 初始化相关资源比如 EventLoopGroup,Bootstrap

    static {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        b = new Bootstrap();
        KryoSerializer kryoSerializer = new KryoSerializer();
        b.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                // 连接的超时时间，超过这个时间还是建立不上的话就代表连接失败
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        /*
                         自定义序列化编解码器
                         */
                        // RpcResponse -> ByteBuf
                        ch.pipeline().addLast(new NettyKryoDecoder(kryoSerializer, RpcResponse.class));
                        // ByteBuf -> RpcRequest
                        ch.pipeline().addLast(new NettyKryoEncoder(kryoSerializer, RpcRequest.class));
                        ch.pipeline().addLast(new NettyClientHandler());
                    }
                });
    }
    public RpcResponse sendMessage(RpcRequest request){
        try {
            ChannelFuture future=b.connect(host,port).sync();
            logger.info("client connect {}",host+":"+port);
            Channel futureChannel= future.channel();
            logger.info("发送信息");
            if(futureChannel!=null){
                futureChannel.writeAndFlush(request).addListener(future1 -> {
                    if(future1.isSuccess()){
                        logger.info("client send message:[{}]",request.toString());
                    }else {
                        logger.error("Send failed:",future1.cause());
                    }
                });
                futureChannel.closeFuture().sync();
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                return futureChannel.attr(key).get();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

//    public static void main(String[] args) {
//        RpcRequest rpcRequest= RpcRequest.builder().interfaceName("interface").methodName("hello").build();
//        NettyClient nettyClient = new NettyClient("127.0.0.1",8889);
//        for (int i=0;i<3;i++){
//            nettyClient.sendMessage(rpcRequest);
//        }
//        RpcResponse rpcResponse = nettyClient.sendMessage(rpcRequest);
//        System.out.println(rpcRequest.toString());
//    }
}
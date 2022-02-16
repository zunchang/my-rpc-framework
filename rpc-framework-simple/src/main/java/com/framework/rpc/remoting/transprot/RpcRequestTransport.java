package com.framework.rpc.remoting.transprot;

import com.framework.common.extension.SPI;
import com.framework.rpc.remoting.dto.RpcRequest;

/**
 * 请求传输接口
 */
@SPI
public interface RpcRequestTransport {

    /**
     * 向服务器发送RPC请求并获取结果
     * @param rpcRequest
     * @return 来自服务器的数据
     */
    Object sendRpcRequest(RpcRequest rpcRequest);
}

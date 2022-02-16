package com.framework.rpc.remoting.dto;


import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RpcMessage {

    // 消息类型
    private byte messageType;
    // 序列化类型
    private byte codec;
    // 压缩类型
    private byte compress;
    // 请求id
    private int requestId;
    // 请求数据
    private Object data;

}

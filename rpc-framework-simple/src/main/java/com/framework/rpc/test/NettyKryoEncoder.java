package com.framework.rpc.test;

import com.framework.rpc.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;

/**
 * 自定义
 */
@AllArgsConstructor
public class    NettyKryoEncoder extends MessageToByteEncoder<Object> {

    private final Serializer serializer;
    private final Class<?> genericClass;

    /**
     * 将对象转化为字节数组
     * @param ctx
     * @param o
     * @param byteBuf
     * @throws Exception
     */

    @Override
    protected void encode(ChannelHandlerContext ctx, Object o, ByteBuf byteBuf) throws Exception {
        if(genericClass.isInstance(o)){
            // 将对象转化为byte
            byte[] body=serializer.serialize(o);
            // 获取消息的长度
            int dataLength = body.length;
            // 写入消息对应的字节数组的长度 writerIndex+4
            byteBuf.writeInt(dataLength);
            // 将字节数组写入byteBuf中
            byteBuf.writeBytes(body);
        }
    }
}

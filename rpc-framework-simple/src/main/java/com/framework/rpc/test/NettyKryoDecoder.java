package com.framework.rpc.test;

import com.framework.rpc.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@AllArgsConstructor
@Slf4j
public class NettyKryoDecoder extends ByteToMessageDecoder {

    private final Serializer serializer;
    private final Class<?> genericClass;

    // 消息的大小存储在 ByteBuf头部
    private static final int BODY_LENGTH = 4;

    /**
     * 解码 ByteBuf对象
     *
     * @param ctx 解码器关联的 ChannelHandlerContext
     * @param byteBuf "入站 "数据
     * @param list 解码后的对象添加到list中
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        if(byteBuf.readableBytes()>=BODY_LENGTH){
            // 标记当前readIndex的位置，以便后面重置readIndex的时候使用
            byteBuf.markReaderIndex();
            // 读取消息的长度
            int dataLength=byteBuf.readInt();
            // 遇到不合理的地方直接return
            if(dataLength<0||byteBuf.readableBytes()<0){
                log.error("data length or byteBuf readableBytes is not valid");
                return;
            }
            // 可读字节小于消息长度的话，说明是不完整信息，重置readIndex
            if(dataLength>byteBuf.readableBytes()){
                byteBuf.resetReaderIndex();
                return;
            }

            byte[] body=new byte[dataLength];
            byteBuf.readBytes(body);
            // 将bytes数组转换为我们需要的对象
            Object obj=serializer.deserialize(body,genericClass);
            list.add(obj);
            log.info("解码成功");

        }
    }
}

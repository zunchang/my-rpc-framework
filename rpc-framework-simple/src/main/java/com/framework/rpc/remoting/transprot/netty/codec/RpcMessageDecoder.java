package com.framework.rpc.remoting.transprot.netty.codec;

import com.framework.common.enums.CompressTypeEnum;
import com.framework.common.enums.SerializationTypeEnum;
import com.framework.common.extension.ExtensionLoader;
import com.framework.rpc.compress.Compress;
import com.framework.rpc.remoting.constants.RpcConstants;
import com.framework.rpc.remoting.dto.RpcMessage;
import com.framework.rpc.remoting.dto.RpcRequest;
import com.framework.rpc.remoting.dto.RpcResponse;
import com.framework.rpc.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

/**
 * 自定义解码器
 * <pre>
 *   0     1     2     3     4        5     6     7     8         9          10      11     12  13  14   15 16
 *   +-----+-----+-----+-----+--------+----+----+----+------+-----------+-------+----- --+-----+-----+-------+
 *   |   magic   code        |version | full length         | messageType| codec|compress|    RequestId       |
 *   +-----------------------+--------+---------------------+-----------+-----------+-----------+------------+
 *   |                                                                                                       |
 *   |                                         body                                                          |
 *   |                                                                                                       |
 *   |                                        ... ...                                                        |
 *   +-------------------------------------------------------------------------------------------------------+
 * 4B  magic code（魔法数）   1B version（版本）   4B full length（消息长度）    1B messageType（消息类型）
 * 1B compress（压缩类型） 1B codec（序列化类型）    4B  requestId（请求的Id）
 * body（object类型数据）
 *
 *   {@link LengthFieldBasedFrameDecoder} 基于长度的解码器，用于解决 TCP 解包和粘连问题。
 */

@Slf4j
public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {

    public RpcMessageDecoder() {
        // lengthFieldOffset: 魔数字节为 4 版本号为 1 故为 5
        // lengthFieldLength: 长度是4字节
        // lengthAdjustment: 全长包括全部字段，并向前读9个字节  (fullLength-9). so values is -9
        // initialBytesToStrip: 我们将手动检查魔术代码和版本，所以不要剥离任何字节。所以值为 0
        this(RpcConstants.MAX_FRAME_LENGTH, 5, 4, -9, 0);
    }

    /**
     * @param maxFrameLength      最大帧长度。它决定了可以接收的最大数据长度。 如果超过，数据将被丢弃。
     * @param lengthFieldOffset   长度字段偏移。长度字段是跳过指定字节长度的字段
     * @param lengthFieldLength   长度字段中的字节数
     * @param lengthAdjustment    添加到长度字段值的补偿值
     * @param initialBytesToStrip 跳过的字节数
     *                            如果你需要接收所有的header+body数据，这个值为0
     *                            如果你只想接收body数据，那么你需要跳过header消耗的字节数
     */
    public RpcMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object decoded = super.decode(ctx, in);
        if (decoded instanceof ByteBuf) {
            ByteBuf frame = (ByteBuf) decoded;
            if (frame.readableBytes() >= RpcConstants.TOTAL_LENGTH) {
                try {
                    return decodeFrame(frame);
                } catch (Exception e) {
                    log.error("解码错误", e);
                    throw e;
                } finally {
                    frame.release();
                }
            }
        }
        return decoded;
    }

    private Object decodeFrame(ByteBuf in) {
        // 按顺序读取ByteBuf
        checkMagicNumber(in);
        checkVersion(in);
        int fullLength = in.readInt();
        //构建RpcMessage对象
        byte messageType = in.readByte();
        byte codecType = in.readByte();
        byte compressType = in.readByte();
        int requestId = in.readInt();
        RpcMessage rpcMessage = RpcMessage.builder()
                .codec(codecType)
                .requestId(requestId)
                .messageType(messageType)
                .build();
        // 心跳
        if (messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE) {
            rpcMessage.setData(RpcConstants.PING);
            return rpcMessage;
        }
        if (messageType == RpcConstants.HEARTBEAT_RESPONSE_TYPE) {
            rpcMessage.setData(RpcConstants.PONG);
            return rpcMessage;
        }
        int bodyLength = fullLength - RpcConstants.HEAD_LENGTH;
        if(bodyLength>0){
            byte[] bs=new byte[bodyLength];
            in.readBytes(bs);
            // 解压字节
            String compressName= CompressTypeEnum.getName(compressType);
            Compress compress= ExtensionLoader.getExtensionLoader(Compress.class).getExtension(compressName);
            bs=compress.decompress(bs);
            // 反序列化对象
            String codecName= SerializationTypeEnum.getName(rpcMessage.getCodec());
            log.info("解码器名称：[{}]",codecName);
            Serializer serializer=ExtensionLoader.getExtensionLoader(Serializer.class)
                    .getExtension(codecName);
            if(messageType==RpcConstants.REQUEST_TYPE){
                RpcRequest tmpValue=serializer.deserialize(bs,RpcRequest.class);
                rpcMessage.setData(tmpValue);
            }else {
                RpcResponse tmpValue=serializer.deserialize(bs,RpcResponse.class);
                rpcMessage.setData(tmpValue);
            }
        }
        return rpcMessage;
    }

    /**
     * 检查版本号
     *
     * @param in
     */
    private void checkVersion(ByteBuf in) {
        // read the version and compare
        byte version = in.readByte();
        if (version != RpcConstants.VERSION) {
            throw new RuntimeException("版本不兼容" + version);
        }
    }

    /**
     * 检测魔数
     *
     * @param in
     */
    private void checkMagicNumber(ByteBuf in) {
        // 读取前四位
        int len = RpcConstants.MAGIC_NUMBER.length;
        byte[] tmp = new byte[len];
        in.readBytes(tmp);
        for (int i = 0; i < len; i++) {
            if (tmp[i] != RpcConstants.MAGIC_NUMBER[i]) {
                throw new IllegalArgumentException("未知魔数：" + Arrays.toString(tmp));
            }
        }
    }
}

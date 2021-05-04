package com.common.serialize;

import com.common.protocol.MsgProtobuf;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ProtobufEncoder extends MessageToByteEncoder<MsgProtobuf.ProtocolMsg> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MsgProtobuf.ProtocolMsg protocolMsg, ByteBuf byteBuf) throws Exception {
        byte[] bytes = protocolMsg.toByteArray();
        //写入消息长度
        byteBuf.writeInt(bytes.length);
        byteBuf.writeBytes(bytes);
    }
}

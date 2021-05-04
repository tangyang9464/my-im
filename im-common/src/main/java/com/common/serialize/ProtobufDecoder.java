package com.common.serialize;

import com.common.protocol.MsgProtobuf;
import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

public class ProtobufDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //标记初始读位置
        in.markReaderIndex();
        //判断包头长度
        if(in.readableBytes()<4){
            return;
        }
        //读取消息头
        int len = in.readInt();
        //非法数据
        if(len<0){
            ctx.close();
        }
        //消息长度不足
        if (len > in.readableBytes()) {
            // 重置读取位置
            in.resetReaderIndex();
            return;
        }

        //正常处理
        ByteBuf byteBuf = Unpooled.buffer(len);
        in.readBytes(byteBuf);
        try {
            byte[] bytes = byteBuf.array();
            MsgProtobuf.ProtocolMsg msg = MsgProtobuf.ProtocolMsg.parseFrom(bytes);
            if (msg!=null){
                out.add(msg);
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
    }
}

package com.netty;

import com.common.protocol.MsgProtobuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

/**
 * @author: tangyang9464
 * @create: 2021-05-02 15:00
 **/
public class ClientHandler extends SimpleChannelInboundHandler<MsgProtobuf.ProtocolMsg> {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MsgProtobuf.ProtocolMsg data) throws Exception {
        System.out.println(data.getFromUserId()+":"+data.getMsg());
    }
}

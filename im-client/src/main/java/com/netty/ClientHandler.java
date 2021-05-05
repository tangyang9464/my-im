package com.netty;

import com.common.protocol.MsgProtobuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author: tangyang9464
 * @create: 2021-05-02 15:00
 **/
@Slf4j
@Component
@ChannelHandler.Sharable
public class ClientHandler extends SimpleChannelInboundHandler<MsgProtobuf.ProtocolMsg> {
    @Resource
    NettyClient client;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("已经建立连接");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //用户主动关闭
        if(client.getState()){
            System.out.println("正在关闭当前客户端");
        }
        //服务器掉线
        else{
            System.out.println("当前连接服务器掉线，正在重连..");
            client.start();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MsgProtobuf.ProtocolMsg data) throws Exception {
        if(data.getType()!= MsgProtobuf.ProtocolMsg.MsgEnumType.MSG_TYPE_HEARTBEAT) {
            System.out.println(data.getFromUserId() + ":" + data.getMsg());
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.READER_IDLE)) {
//                log.info("长期没收到服务器推送数据,重新连接");
                //可以选择重新连接
            } else if (event.state().equals(IdleState.WRITER_IDLE)) {
//                log.info("长期未向服务器发送数据");
                //发送心跳包
                ctx.writeAndFlush(MsgProtobuf.ProtocolMsg.newBuilder().setType(MsgProtobuf.ProtocolMsg.MsgEnumType.MSG_TYPE_HEARTBEAT).build());
            } else if (event.state().equals(IdleState.ALL_IDLE)) {
                log.info("ALL");
            }
        }
    }
}

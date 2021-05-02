package com.netty;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author: tangyang9464
 * @create: 2021-05-02 14:04
 **/
@Slf4j
@ChannelHandler.Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {
    private final Set<ChannelHandlerContext> user = new CopyOnWriteArraySet<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("新客户端："+ctx.channel().id());
        user.add(ctx);
        log.info("用户数:"+user.size());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        for(ChannelHandlerContext chc:user){
            if(ctx.equals(chc)){
                continue;
            }
            //转发消息
            chc.channel().writeAndFlush((String)msg);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端断开连接："+ctx.channel().id());
        user.remove(ctx);
    }
}

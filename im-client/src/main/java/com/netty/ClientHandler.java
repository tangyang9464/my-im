package com.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author: tangyang9464
 * @create: 2021-05-02 15:00
 **/
public class ClientHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String o) throws Exception {
        System.out.println("他说："+o);
    }
}

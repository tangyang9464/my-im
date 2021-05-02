package com.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Scanner;

/**
 * @author: tangyang9464
 * @create: 2021-05-02 14:57
 **/
@Component
public class NettyClient {
    private static final Bootstrap bootstrap;
    private static final NioEventLoopGroup group;
    private static final ClientHandler clientHandler;

    static {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        clientHandler = new ClientHandler();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline()
                                .addLast(new StringDecoder())
                                .addLast(new StringEncoder())
                                .addLast(clientHandler);
                    }
                });
    }

    @PostConstruct
    public void start(){
        try{
            ChannelFuture cf = bootstrap.connect("127.0.0.1",9090).sync();
            for(;;){
                Scanner sc = new Scanner(System.in);
                String msg = sc.next();
                if(msg.equals("exit")){
                    break;
                }
                System.out.println("我说："+msg);
                cf.channel().writeAndFlush(msg);
            }
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            group.shutdownGracefully();
        }
    }
}

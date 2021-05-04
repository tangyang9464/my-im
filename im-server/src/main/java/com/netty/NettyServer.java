package com.netty;

import com.common.serialize.ProtobufDecoder;
import com.common.serialize.ProtobufEncoder;
import com.common.vo.ServerInfo;
import com.common.zk.CuratorUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author: tangyang9464
 * @create: 2021-05-02 13:40
 **/
@Component
@Slf4j
public class NettyServer {
    @Value("${im.server.port}")
    private int imPort;
    @Value("${im.kafka.topicName}")
    private String name;

    @Resource
    ServerHandler serverHandler;

    public void start(){
        //注册zookeeper
        register();
        //连接上线
        bind();
    }

    private void bind(){
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {
            serverBootstrap
                    .group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    //保持长连接
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new ProtobufDecoder())
                                    .addLast(new ProtobufEncoder())
                                    .addLast(serverHandler);
                        }
                    });
            ChannelFuture cf = serverBootstrap.bind(imPort).sync();
            log.info("服务器启动");
            log.info("name:"+name);
            //注册关闭钩子
            ShutHook();
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("test");
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
    /**
     * 注册到zookpeer
     */
    private void register(){
        try {
            CuratorUtil.createNodeForServer(new ServerInfo(InetAddress.getLocalHost().getHostAddress(),imPort));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
    /**
     * JVM关闭钩子，用于关闭服务器前删除zookeeper节点（下线）
     */
    private void ShutHook(){
        //销毁服务
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                log.info("服务器关闭");
                try {
                    CuratorUtil.deleteNodeForServer(new ServerInfo(InetAddress.getLocalHost().getHostAddress(),imPort));
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }));
    }
}

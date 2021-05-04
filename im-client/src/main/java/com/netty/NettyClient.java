package com.netty;

import com.common.protocol.MsgProtobuf;
import com.common.redis.RedisUtil;
import com.common.serialize.ProtobufDecoder;
import com.common.serialize.ProtobufEncoder;
import com.common.vo.ServerInfo;
import com.common.zk.CuratorUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * @author: tangyang9464
 * @create: 2021-05-02 14:57
 **/
@Component
@Slf4j
public class NettyClient {
    private static final Bootstrap bootstrap;
    private static final NioEventLoopGroup group;
    private static final ClientHandler clientHandler;
    private ChannelFuture cf;
    @Resource
    private RedisUtil redisUtil;

    @Value("${im.client.userid}")
    private String userid;
    @Value("${im.client.username}")
    private String username;

    static {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        clientHandler = new ClientHandler();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE,true)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline()
                                .addLast(new ProtobufDecoder())
                                .addLast(new ProtobufEncoder())
                                .addLast(clientHandler);
                    }
                });
    }
    public void start(){
        try{
            //登录并获取可用服务器地址
            ServerInfo suitableServer = login();
            //连接上线
            cf = bootstrap.connect(suitableServer.getIp(),suitableServer.getPort()).sync();
            //连接成功就马上把用户名发送过去
            if(cf.isSuccess()){
                System.out.println("本机:"+userid);
                MsgProtobuf.ProtocolMsg.Builder builder = MsgProtobuf.ProtocolMsg.newBuilder();
                builder.setType("login")
                        .setFromUserId(userid);
                cf.channel().writeAndFlush(builder.build());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 登录并获取可用服务器地址
     */
    private ServerInfo login(){
        ServerInfo suitableServer=null;
        //登录验证

        //查询zookeeper
        try{
            List<String> serverNodes = CuratorUtil.getAllNodeForServer();
            //负载均衡
            String[] nodeUrl = serverNodes.get(new Random().nextInt(serverNodes.size())).split("_");
            //
            String ip = nodeUrl[0];
            int port = Integer.parseInt(nodeUrl[1]);
            suitableServer = new ServerInfo(ip,port);
            //redis保存user-server信息
            redisUtil.addUserAndServer(userid,suitableServer.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return suitableServer;
    }
    /**
     * 群发
     */
    public void sendGroupMsg(String msg){
        if(cf!=null){
            MsgProtobuf.ProtocolMsg.Builder builder = MsgProtobuf.ProtocolMsg.newBuilder();
            builder.setType("group")
                    .setFromUserId(userid)
                    .setMsg(msg);
            cf.channel().writeAndFlush(builder.build());
        }
        else{
            log.info("ChannelFuture为空");
        }
    }
    /**
     * 私聊
     */
    public void sendPrivateMsg(String msg,String toUserId){
        if(cf!=null){
            if(!redisUtil.userIsExist(toUserId)){
                System.out.println("用户"+toUserId+"不在线");
                return;
            }
            MsgProtobuf.ProtocolMsg.Builder builder = MsgProtobuf.ProtocolMsg.newBuilder();
            builder.setType("private")
                    .setFromUserId(userid)
                    .setToUserId(toUserId)
                    .setMsg(msg);
            cf.channel().writeAndFlush(builder.build());
        }
        else{
            log.info("ChannelFuture为空");
        }
    }
    /**
     * 获取所有在线用户
     */
    public Set<String> getAllOnlineUser(){
        return redisUtil.getAllUser();
    }
}

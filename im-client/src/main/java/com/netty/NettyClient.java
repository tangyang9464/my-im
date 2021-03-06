package com.netty;

import com.common.protocol.MsgProtobuf;
import com.common.redis.RedisUtil;
import com.common.serialize.ProtobufDecoder;
import com.common.serialize.ProtobufEncoder;
import com.common.spring.SpringContextUtil;
import com.common.vo.ServerInfo;
import com.common.zk.CuratorUtil;
import com.terminal.Scan;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: tangyang9464
 * @create: 2021-05-02 14:57
 **/
@Component
@Slf4j
public class NettyClient {
    private static final Bootstrap bootstrap;
    private static final NioEventLoopGroup group;
    private ChannelFuture cf;
    private static AtomicInteger retryNum;
    private boolean exit=false;
    private final int MAX_RETRY_NUM = 1;
    @Resource
    private RedisUtil redisUtil;

    @Value("${im.client.userid}")
    private String userid;
    @Value("${im.client.username}")
    private String username;

    static {
        retryNum = new AtomicInteger(0);
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE,true)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline()
                                .addLast(new ProtobufDecoder())
                                .addLast(new ProtobufEncoder())
                                .addLast("ping",new IdleStateHandler(60, 20, 60 * 10, TimeUnit.SECONDS))
                                .addLast(SpringContextUtil.getApplicationContext().getBean(ClientHandler.class));
                    }
                });
    }

    public void start(){
        exit=false;
        NettyClient client = this;
        //????????????????????????????????????
            ServerInfo suitableServer = login();
        //????????????
        cf = bootstrap.connect(suitableServer.getIp(),suitableServer.getPort());
        cf.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                //????????????????????????????????????????????????????????????
                if(channelFuture.isSuccess()){
                    System.out.println("??????:"+userid);
                    retryNum.set(0);
                    Thread commandThread = new Thread(new Scan(client));
                    commandThread.start();
                    MsgProtobuf.ProtocolMsg.Builder builder = MsgProtobuf.ProtocolMsg.newBuilder();
                    builder.setType(MsgProtobuf.ProtocolMsg.MsgEnumType.MSG_TYPE_LOGIN)
                            .setFromUserId(userid);
                    channelFuture.channel().writeAndFlush(builder.build());
                }
                else{
                    if(retryNum.get()>=MAX_RETRY_NUM){
                        log.error("?????????????????????????????????");
                        retryNum.set(0);
                        group.shutdownGracefully();
                        throw new Exception("?????????????????????"+suitableServer.getIp()+":"+suitableServer.getPort());
                    }
                    else{
                        log.warn("???????????????5????????????");
                        log.warn("???????????????"+retryNum.incrementAndGet());
                        channelFuture.channel().eventLoop().schedule(()->start(),5,TimeUnit.SECONDS);
                    }
                }
            }
        });
    }

    public boolean getState(){
        return exit;
    }

    /**
     * ????????????????????????????????????
     */
    private ServerInfo login(){
        ServerInfo suitableServer=null;
        //????????????

        //??????zookeeper
        try{
            List<String> serverNodes = CuratorUtil.getAllNodeForServer();
            //????????????
            if (serverNodes.size()==0){
                log.error("??????????????????");
                throw new Exception("??????????????????");
            }
            String[] nodeUrl = serverNodes.get(new Random().nextInt(serverNodes.size())).split("_");
            //
            String ip = nodeUrl[0];
            int port = Integer.parseInt(nodeUrl[1]);
            suitableServer = new ServerInfo(ip,port);
            //redis??????user-server??????
            redisUtil.addUserAndServer(userid,suitableServer.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return suitableServer;
    }
    /**
     * ??????
     */
    public void sendGroupMsg(String msg){
        if(cf!=null){
            MsgProtobuf.ProtocolMsg.Builder builder = MsgProtobuf.ProtocolMsg.newBuilder();
            builder.setType(MsgProtobuf.ProtocolMsg.MsgEnumType.MSG_TYPE_GROUP)
                    .setFromUserId(userid)
                    .setMsg(msg);
            cf.channel().writeAndFlush(builder.build());
        }
        else{
            log.info("ChannelFuture??????");
        }
    }
    /**
     * ??????
     */
    public void sendPrivateMsg(String msg,String toUserId){
        if(cf!=null){
            if(!redisUtil.userIsExist(toUserId)){
                System.out.println("??????"+toUserId+"?????????");
                return;
            }
            MsgProtobuf.ProtocolMsg.Builder builder = MsgProtobuf.ProtocolMsg.newBuilder();
            builder.setType(MsgProtobuf.ProtocolMsg.MsgEnumType.MSG_TYPE_PRIVATE)
                    .setFromUserId(userid)
                    .setToUserId(toUserId)
                    .setMsg(msg);
            cf.channel().writeAndFlush(builder.build());
        }
        else{
            log.info("ChannelFuture??????");
        }
    }
    /**
     * ????????????????????????
     */
    public Set<String> getAllOnlineUser(){
        return redisUtil.getAllUser();
    }

    public void close(){
        exit=true;
        group.shutdownGracefully();
    }
}

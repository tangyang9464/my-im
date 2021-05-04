package com.netty;

import com.common.kafka.KafkaUtil;
import com.common.protocol.MsgProtobuf;
import com.common.redis.RedisUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.DependsOn;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: tangyang9464
 * @create: 2021-05-02 14:04
 **/
@Slf4j
@ChannelHandler.Sharable
@Component
@DependsOn(value = "kafkaTopicConfig")
public class ServerHandler extends ChannelInboundHandlerAdapter {
    /**
     * 映射关系为ChannelId -> userId -> ChannelHandlerContext
     * 其中userId通过客户端连接后主动发送包来确认，然后建立两个映射关系
     */
    private final Map<String,ChannelHandlerContext> users = new ConcurrentHashMap<>();
    private final Map<ChannelId, String> channelIdAndUserId = new ConcurrentHashMap<>();
    @Resource
    private KafkaUtil kafkaUtil;
    @Resource
    private RedisUtil redisUtil;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object obj) throws Exception {
        MsgProtobuf.ProtocolMsg data = (MsgProtobuf.ProtocolMsg)obj;
        String type = data.getType();
        String fromUserId = data.getFromUserId();
        String toUserId = data.getToUserId();
        //登录验证包
        switch (type) {
            case "login":
                log.info("新客户端：" + fromUserId);
                users.put(fromUserId, ctx);
                channelIdAndUserId.put(ctx.channel().id(), fromUserId);
                log.info("用户数:" + users.size());
                break;
            case "group":
                kafkaUtil.sendMsg("group", data);
                break;
            case "private":
                if (users.containsKey(toUserId)) {
                    ChannelHandlerContext toUser = users.get(toUserId);
                    toUser.channel().writeAndFlush(data);
                } else {
                    String topic = redisUtil.getServerInfo(toUserId);
                    kafkaUtil.sendMsg(topic, data);
                }
                break;
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ChannelId channelId = ctx.channel().id();
        String userId = channelIdAndUserId.get(channelId);
        redisUtil.deleteUserAndServer(userId);
        users.remove(userId);
        channelIdAndUserId.remove(channelId);
        log.info("客户端断开连接："+userId);
    }

    @KafkaListener(topics = "#{'${im.kafka.topicName}'.split(',')}",groupId = "#{'${im.server.port}'}")
    private void consumer(ConsumerRecord<String,MsgProtobuf.ProtocolMsg> record){
        MsgProtobuf.ProtocolMsg msg = record.value();
        String fromUserId = msg.getFromUserId();
        for(ChannelHandlerContext user :users.values()){
            if(channelIdAndUserId.get(user.channel().id()).equals(fromUserId)){
                continue;
            }
            //转发消息
            user.channel().writeAndFlush(msg);
        }
    }
}

package com.common.kafka;

import com.common.protocol.MsgProtobuf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import javax.annotation.Resource;

@Component
@Slf4j
public class KafkaUtil {
    @Resource
    private KafkaTemplate<String,MsgProtobuf.ProtocolMsg> kafkaTemplate;

    public void sendMsg(String topic, MsgProtobuf.ProtocolMsg msg){
        ListenableFuture<SendResult<String, MsgProtobuf.ProtocolMsg>> future = kafkaTemplate.send(topic,msg);
        future.addCallback(success -> log.info("成功，生产者发送消息"),
                fail -> {
                    log.info("失败，生产者发送消息，原因：{}",fail.getMessage());
                    kafkaTemplate.send(topic, msg);
                });
    }
}

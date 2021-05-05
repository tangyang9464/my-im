package com.config;

import com.common.kafka.uitl.KafkaTopicEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Configuration
@Slf4j
public class KafkaTopicConfig implements InitializingBean {
    @Value("${im.server.port}")
    private String port;

    @Override
    public void afterPropertiesSet() {
        String topics = KafkaTopicEnum.GROUP_CHAT.getName();
        try {
            topics += ","+InetAddress.getLocalHost().getHostAddress()+"_"+port;
            } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        System.setProperty("im.kafka.topicName", topics);
        log.info("本机订阅topic为:"+topics);
    }
}

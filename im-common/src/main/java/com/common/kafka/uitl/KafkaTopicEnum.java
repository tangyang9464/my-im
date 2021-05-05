package com.common.kafka.uitl;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum KafkaTopicEnum {
    GROUP_CHAT("group");

    private final String name;
}

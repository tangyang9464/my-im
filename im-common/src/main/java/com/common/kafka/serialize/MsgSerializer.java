package com.common.kafka.serialize;

import com.common.protocol.MsgProtobuf;
import org.apache.kafka.common.serialization.Serializer;

public class MsgSerializer<ProtocolMsg> implements Serializer<MsgProtobuf.ProtocolMsg> {
    @Override
    public byte[] serialize(String s, MsgProtobuf.ProtocolMsg protocolMsg) {
        return protocolMsg.toByteArray();
    }
}
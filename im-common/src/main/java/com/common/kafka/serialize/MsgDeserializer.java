package com.common.kafka.serialize;

import com.common.protocol.MsgProtobuf;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.kafka.common.serialization.Deserializer;


public class MsgDeserializer<ProtocolMsg> implements Deserializer<MsgProtobuf.ProtocolMsg> {
    @Override
    public MsgProtobuf.ProtocolMsg deserialize(String s, byte[] bytes) {
        MsgProtobuf.ProtocolMsg msg = null;
        try {
            msg = MsgProtobuf.ProtocolMsg.parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return msg;
    }
}

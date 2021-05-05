package com.common.protocol;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProtocolMsg {
    private MsgEnumType type;
    private String fromUserId;
    private String toUserId;
    private String msg;
}

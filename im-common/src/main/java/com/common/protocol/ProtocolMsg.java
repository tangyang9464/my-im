package com.common.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProtocolMsg {
    private String type;
    private String fromUserId;
    private String toUserId;
    private String msg;
}

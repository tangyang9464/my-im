package com.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServerInfo {
    private String ip;
    private int port;
    @Override
    public String toString(){
        return ip+"_"+port;
    }
}

package com.terminal;

import com.common.protocol.MsgProtobuf;
import com.netty.NettyClient;

import java.util.Scanner;

/**
 * @author: tangyang9464
 * @create: 2021-05-02 20:18
 **/
public class Scan implements Runnable{
    NettyClient client;

    public Scan(NettyClient client){
        this.client=client;
    }

    @Override
    public void run() {
        boolean exit = false;
        while (!exit){
            Scanner sc = new Scanner(System.in);
            String text = sc.next();

            switch (text){
                case "ls":
                    System.out.println(client.getAllOnlineUser());
                    break;
                case "exit":
                    client.close();
                    exit=true;
                    break;
                default:
                    String[] arr = text.split(":");
                    if(arr.length==1){
                        client.sendGroupMsg(arr[0]);
                    }
                    else{
                        client.sendPrivateMsg(arr[1],arr[0]);
                    }
            }
        }
    }
}

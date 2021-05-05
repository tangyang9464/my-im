package com;

import com.netty.NettyClient;
import com.terminal.Scan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;


/**
 * @author: tangyang9464
 * @create: 2021-05-02 14:54
 **/
@SpringBootApplication
public class ImClientApplication implements CommandLineRunner {
    @Resource
    NettyClient client;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ImClientApplication.class);
        //设置非web服务
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
    }

    //启动服务器和终端
    @Override
    public void run(String... args) throws Exception {
        client.start();
//        Thread commandThread = new Thread(new Scan(client));
//        commandThread.start();
    }
}

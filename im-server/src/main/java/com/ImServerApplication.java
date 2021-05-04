package com;

import com.netty.NettyServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

/**
 * @author: tangyang9464
 * @create: 2021-05-02 13:55
 **/
@SpringBootApplication
@Slf4j
public class ImServerApplication implements CommandLineRunner {
    @Resource
    private NettyServer nettyServer;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ImServerApplication.class);
        //设置非web服务
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        nettyServer.start();
    }
}

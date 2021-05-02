package com;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author: tangyang9464
 * @create: 2021-05-02 13:55
 **/
@SpringBootApplication
@Slf4j
public class ImServerApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ImServerApplication.class);
        //设置非web服务
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
    }
}

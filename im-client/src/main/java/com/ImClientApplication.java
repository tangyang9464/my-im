package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author: tangyang9464
 * @create: 2021-05-02 14:54
 **/
@SpringBootApplication
public class ImClientApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(ImClientApplication.class);
        //设置非web服务
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);
    }
}

package com.tdk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @description: 服务启动类
 * @author: Dingkang Tang
 * @create: 2020-11-29 13:51
 **/

@SpringBootApplication
public class baseServerApplication {
    public static void main(String[] args) {
        try {
            SpringApplication.run(baseServerApplication.class, args);
        }catch(Throwable e) {
            e.printStackTrace();
        }
    }
}
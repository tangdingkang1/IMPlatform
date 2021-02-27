package com.tdk;

import com.corundumstudio.socketio.SocketIOServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import sun.rmi.runtime.Log;

import javax.swing.*;

/**
 * @description: 聊天模块的启动类
 * @author: Dingkang Tang
 * @create: 2020-11-18 19:50
 **/
@Slf4j
@SpringBootApplication
public class NettySocketioApplication  {
    public static void main(String[] args) {
        SpringApplication.run(NettySocketioApplication.class);
    }

}
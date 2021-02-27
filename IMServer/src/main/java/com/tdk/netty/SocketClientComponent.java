package com.tdk.netty;

import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOClient;
import com.tdk.entity.ChatList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: 用于操作socketio客户端的组件，不同的用户
 * @author: Dingkang Tang
 * @create: 2020-11-19 17:10
 **/
@Component
@Slf4j
public class SocketClientComponent {

    //用来存放客户端连接的请求。因为是通过 A客户端-服务端-B客户端。
    //这个是线程安全的（相对于HashMap来说，之后还要研究一下，socket.io这个多线程怎么操作的。）
    ConcurrentHashMap<String, SocketIOClient> clients = new ConcurrentHashMap<>();

    //这里是将在线的userId存储在map中，发送消息的时候就取出来
    public void storeClient(SocketIOClient client){
        HandshakeData handshakeData = client.getHandshakeData();
        String userId = handshakeData.getSingleUrlParam("userId");
        log.info("userId="+userId+"已经连接!");
        clients.put(userId,client);//userId和当前它所拥有的client一一相对应
    }
    //这里就不用说了吧，和上面一样的。
    public void delClient(SocketIOClient client){
        HandshakeData handshakeData =  client.getHandshakeData();
        String userId = handshakeData.getSingleUrlParam("userId");
        log.info("userId="+userId+"已经断开连接了");
        clients.remove(userId);
    }

    //发送消息的部分，有可能是单聊，也有可能是广播，（类似系统管理员和每个用户发送消息那样？）
    //或者是游戏里面的大喇叭那种？全部人都可以看得见，或许应该是这样的
    //businessName就是用来辨别这个的。从上端传来
    public void send(String toUserId,Object data,String businessName){
        //找到被发送消息的client，然后将消息传递过去。
        SocketIOClient client = clients.get(toUserId);
        //如果在线就发送过去，如果不在线，在上一层已经将消息存储下来了。等到他下次上线的时候，就能够看到消息了。
        if (client!=null) {
            client.sendEvent(businessName,data);
        }
    }
}
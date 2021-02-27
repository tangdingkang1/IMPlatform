package com.tdk.netty;

import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.tdk.util.DateUtil;
import com.tdk.dao.ChatDao;
import com.tdk.entity.ChatHistory;
import com.tdk.entity.ChatHistoryRequest;
import com.tdk.entity.ChatList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: Socket服务器，用来处理信息
 * @author: Dingkang Tang
 * @create: 2020-11-18 20:39
 **/
@Component
@Slf4j
public class NettySocketioServer implements InitializingBean, DisposableBean {
    @Autowired
    private SocketIOServer socketIoServer;
    @Autowired
    private SocketClientComponent socketClientComponent;
    @Autowired
    ChatDao chatDao;


    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("我倒看看是什么时候启动");
        startSocketServer();
    }

    @Override
    public void destroy() throws Exception {
            if(socketIoServer!=null)
                socketIoServer.stop();
            log.info("这里是关闭server");
    }
    //创建多线程
    public void startSocketServer(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                socketIoServer.start();
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        log.info("这里是启动线程了，看一下先后执行顺序");
    }

    /*
    todo：
    理论上这里，已连接了，就要显示和当前用户聊天过的所有用户的聊天信息，
    因为这是一个没有涉及到本地缓存，所有信息都要存储在服务器上面，所以这是必须的操作
     */
    @OnConnect
    public void onConnect(SocketIOClient client) {
        //这里是将userid存放进去
        socketClientComponent.storeClient(client);
        //把列表发送回去
        HandshakeData handshakeData = client.getHandshakeData();
        String userId = handshakeData.getSingleUrlParam("userId");
        List<ChatList> list = chatDao.getChatList(Integer.valueOf(userId));
        for (ChatList cl:list
             ) {
            System.out.println(cl);
        }
        socketClientComponent.send(userId,list,"receiveChatList");
    }

    /**
     * 客户端关闭连接时触发
     *
     * @param client
     */
    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        socketClientComponent.delClient(client);
    }

    //客户端发送消息
    @OnEvent(value = "sendMsg")
    public void sendMsg(SocketIOClient client, Map<String,Object> data) {
        //下面要将消息存储到msql里面去。
        String userId = (String)data.get("userId");
        String toUserId = (String)data.get("toUserId");
        String msg  = (String)data.get("msg");
        String publishTime = DateUtil.currentDate();
        String msgFlag;
        if(userId.compareTo(toUserId)<0){
            msgFlag=userId+":"+toUserId;
        }else{
            msgFlag=toUserId+":"+userId;
        }
        int ans = chatDao.sendMsg(Integer.valueOf(userId),msg,Integer.valueOf(toUserId),publishTime,msgFlag);
        if(ans>0){
            log.info("现在只能说存储信息成功！");
            Map<String,Object> map =new HashMap<>();
            map.put("userId",userId);
            map.put("msg",msg);
            map.put("toUserId",toUserId);
            map.put("publishTime",publishTime);
            //客户端的标识是接收到消息。对方收到消息
            socketClientComponent.send(toUserId,map,"receiveMsg");
            log.info("现在就是发送成功了。");
        }else{
            log.error("哦豁，出问题了，发送消息失败了。");
        }
    }
    @OnEvent("getHistory")
    public void getHistory(SocketIOClient socketIOClient, ChatHistoryRequest request){
            int userId = request.getUserId();
            String msgFlag = request.getMsgFlag();
            List<ChatHistory> list = chatDao.getHistory(msgFlag);
            socketClientComponent.send(String.valueOf(userId),list,"history");
            log.info(list.toString());
    }
}
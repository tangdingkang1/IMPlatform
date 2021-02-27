package com.tdk.test;

import com.tdk.dao.ChatDao;
import com.tdk.entity.ChatList;
import javafx.beans.binding.ObjectExpression;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;

/**
 * @description: 测试redispool能否正常工作
 * @author: Dingkang Tang
 * @create: 2020-11-19 10:33
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class testRedisPool {

    @Autowired
    JedisPool jedisPool;
    @Autowired
    ChatDao chatDao;

    @Test
    public void test(){

        Jedis jedis=jedisPool.getResource();
        String a= jedis.get("name");
        System.out.println(a);
    }
    @Test
    public void testGetChatMsg(){
        List<ChatList> list = chatDao.getChatList(1);
    }
}
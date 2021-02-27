package com.tdk.test;

import com.tdk.dao.MomentDao;
import com.tdk.dao.UserDao;
import com.tdk.entity.MomentDetail;
import com.tdk.entity.User;
import com.tdk.service.MomentService;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;

/**
 * @description:
 * @author: Dingkang Tang
 * @create: 2020-12-02 11:27
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class Test {
    @Autowired
    JedisPool jedisPool;
    @Autowired
    UserDao userDao;
    @Autowired
    MomentDao momentDao;
    @Autowired
    MomentService momentService;
    public void jedisdao(){
        Jedis jedis=jedisPool.getResource();
        String a= jedis.set("name","tangdingkang");
    }
    @org.junit.Test
    public void userDaoTest(){
        String password  = userDao.getPasswordByUsername("tangdingkang");
        if(password.equals("123")){
            User u=userDao.getUserInfoByName("tangdingkang");
            System.out.println(u);
        }
    }
    //测试要加这个先这个东西，不然不启动springboot..
    @org.junit.Test
    public void momentDaoTest(){
        List<MomentDetail> list = momentService.getOnesMoments(1);
        for (MomentDetail momentDetail:
             list) {
            System.out.println(momentDetail);
        }
    }
    @org.junit.Test
    public void moments(){
        List<MomentDetail> list = momentService.getFriendMoments(1);
        for (MomentDetail momentDetail:
                list) {
            System.out.println(momentDetail);
        }
    }
    @org.junit.Test
    public void LikeDao(){
        momentService.deleteMomentExtra(1,3);
    }
    @org.junit.Test

    public void TransactionTest(){
        momentDao.deleteFeedMoments(1,2);
        int i = 1/0;
        momentDao.deleteFeedMoments(2,1);
    }

}
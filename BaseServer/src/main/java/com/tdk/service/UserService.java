package com.tdk.service;

import com.tdk.dao.MomentDao;
import com.tdk.dao.UserDao;
import com.tdk.entity.*;
import com.tdk.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;

/**
 * @description:
 * @author: Dingkang Tang
 * @create: 2020-11-30 19:32
 **/
@Service
public class UserService {
    @Autowired
    UserDao userDao;
    @Autowired
    JedisPool jedisPool;
    @Autowired
    MomentDao momentDao;

    public LoginReturn login(String username, String password) {
        if (password.equals(userDao.getPasswordByUsername(username))) {
            User userInfo = userDao.getUserInfoByName(username);
            String token = UUIDUtil.uuid() + userInfo.getUserId();
            //将得到的信息返回，真是血泪啊，之前写了要改...2021年2月19日01:50:26
            LoginReturn info = new LoginReturn();
            info.setAvatar(userInfo.getAvatar());
            info.setNickname(userInfo.getNickname());
            info.setToken(token);

            int  userId = userInfo.getUserId();
            Jedis jedis = jedisPool.getResource();
            //如果现在已经登录了，把之前的挤掉....或者修改密码也是这个操作。
            if (jedis.exists(RedisKeys.token(userId))) {
                // NX是不存在时才set， XX是存在时才set， EX是秒，PX是毫秒
                jedis.set(RedisKeys.token(userId), token, "XX", "EX", 1800);
            } else { //第一次登陆才将信息写入，重复登陆不写。
                jedis.set(RedisKeys.token(userId), token, "NX", "EX", 1800);
                //将信息存入个人信息存入当中。
                jedis.hset(RedisKeys.userInfo(userId), "avatar", userInfo.getAvatar());
                jedis.hset(RedisKeys.userInfo(userId), "nickname", userInfo.getNickname());
            }
            jedis.close();
            return info;
        } else
            return null;
    }

    public boolean logout(String token) {
        Jedis jedis = jedisPool.getResource();
        //从32位开始切出来，得到userId
        int userId = Integer.valueOf(token.substring(32));
        //删掉登陆凭证
        if (jedis.del(RedisKeys.token(userId)) > 0) {
            jedis.del(RedisKeys.userInfo(userId));
            return true;
        } else
            return false;
    }

    public boolean register(User user) {
        if (userDao.existUser(user.getUsername()) > 0)
            return false;
        return userDao.register(user) > 0;
    }

    public int applyFriend(int userId, int toUserId, String msg) {
        return userDao.applyFriend(userId, toUserId, msg);
    }

    //: 同意或拒绝好友申请
    //: 同意了应该把两个人的瞬间加到个自己的moment_feed里面。好麻烦哦
    @Transactional   //这个是操作出问题是要回滚的，
    public boolean acceptOrRefuse(int applyRequestId, int determination) {
        //同意好友申请。
        if (determination == 1) {
            userDao.acceptOrRefuse(applyRequestId, determination);
            //这里得到两个人的user_id
            userAndToUser idInfo = userDao.queryRequestInfo(applyRequestId);
            userDao.addFriendToList(idInfo.getUserId(), idInfo.getToUserId());
            //这里写个函数将moment添加到对方的moment_feed里面。
            momentDao.addFeedMoments(idInfo.getUserId(),idInfo.getToUserId());
            momentDao.addFeedMoments(idInfo.getToUserId(),idInfo.getUserId());
        }
        else if(determination == 0) {
            userDao.acceptOrRefuse(applyRequestId, determination);
        }
        return true;
    }
    //finished
    //：删除好友，
    //:要把两个人的朋友圈列表删除。feed列表。
    //删除就要一起删除，不然删了好友，还能看到朋友圈岂不是很尴尬。
    @Transactional
    public int deleteFriend(int userId, int toUserId) {
        //返回成功删除的条数？
        momentDao.deleteFeedMoments(userId,toUserId);
        momentDao.deleteFeedMoments(toUserId,userId);
        return userDao.deleteFriend(userId,toUserId);
    }

    public List<friendInfo> getFriendInfo(int userId) {
        List<friendInfo> list  =userDao.getFriendList(userId);
        return list;
    }
}
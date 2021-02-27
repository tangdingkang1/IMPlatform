package com.tdk.service;

import com.tdk.dao.CommentDao;
import com.tdk.entity.Comment;
import com.tdk.util.RedisKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;

/**
 * @description:
 * @author: Dingkang Tang
 * @create: 2020-12-06 15:48
 **/
@Service
public class CommentService {
    @Autowired
    CommentDao commentDao;
    @Autowired
    JedisPool jedisPool;
    public  int  publishComment(Comment comment) {
        return commentDao.addComment(comment);
    }

    public List<Comment> getDetail(int momentID) {
        List<Comment> comments = commentDao.getDetail(momentID);
        Jedis jedis = jedisPool.getResource();

        //这里是从缓存取到评论的每个人的个人昵称和头像。
        for (Comment comment:comments
             ) {
             int userId = comment.getUserId();
            comment.setAvatar(jedis.hget(RedisKeys.userInfo(userId),"avatar"));
            comment.setNickname(jedis.hget(RedisKeys.userInfo(userId),"nickname"));
        }
        return   comments;
    }
    public int deleteComment(int commentId){
        return commentDao.deleteComment(commentId);
    }
}
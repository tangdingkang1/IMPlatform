package com.tdk.service;

import com.tdk.dao.MomentDao;
import com.tdk.dao.UserDao;
import com.tdk.entity.Moment;
import com.tdk.entity.MomentDetail;
import com.tdk.util.RedisKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Arrays;
import java.util.List;

/**
 * @description:
 * @author: Dingkang Tang
 * @create: 2020-12-04 17:34
 **/
@Service
public class MomentService {

    @Autowired
    MomentDao momentDao;
    @Autowired
    UserDao userDao;
    @Autowired
    JedisPool jedisPool;
    //发表瞬间。
    public int publishMoment(Moment moment) {
        int res= momentDao.publishMoment(moment);   //成功了会返回1.
        Jedis jedis = jedisPool.getResource();
        jedis.hset(RedisKeys.momentInfo(moment.getMomentId()),"likeCount","0");
        jedis.hset(RedisKeys.momentInfo(moment.getMomentId()),"commentCount","0");
        momentDao.likeAndCount(moment.getMomentId()); //将传回来的主键，创建一个评论和点赞量的表。
        addFeed(moment.getUserId(),moment);//喂粉丝。
        jedis.close();
        return  res;
    }

    //两个条件就能确定删除的一定是自己的，不会误删别人的瞬间。userId从token里面提取出来。
    public int deleteMoment(int momentId,int userId){
        int res = momentDao.deleteMoment(momentId,userId);
        //如果成功删除了，就把评论删除，feed——moment 删除，点赞删除。 这个可以异步进行，因为不用返回了。唯一主键都被删除了。
        if(res>0){
            System.out.println("res="+res);
            deleteMomentExtra(userId,momentId);
        }
        return res;
    }

    public List<MomentDetail> getOnesMoments(int userId) {
        Jedis jedis = jedisPool.getResource();
        List<MomentDetail> list= momentDao.getMomentsByUserId(userId);
        for(MomentDetail momentDetail:list){
            momentDetail.setUserId(userId);
            momentDetail.setAvatar(jedis.hget(RedisKeys.userInfo(userId),"avatar"));
            momentDetail.setNickname(jedis.hget(RedisKeys.userInfo(userId),"nickname"));
            int momentId = momentDetail.getMomentId();
            //从redis拿到评论数和点赞数，todo 要是拿不到的话，还要从mysql里面查。
            momentDetail.setCommentCount(Math.toIntExact(jedis.hincrBy(RedisKeys.momentInfo(momentId), "commentCount", 0)));
            momentDetail.setLikeCount(Math.toIntExact(jedis.hincrBy(RedisKeys.momentInfo(momentId), "likeCount", 0)));
            momentDetail.setIfLike(ifLike(momentId,userId));
        }
        jedis.close();
        return list;
    }

    public List<MomentDetail> getFriendMoments(int userId) {
        Jedis jedis =jedisPool.getResource();
        List<MomentDetail> list = momentDao.getFriendMoments(userId);

        for (MomentDetail momentDetail:list
             ) {
            momentDetail.setAvatar(jedis.hget(RedisKeys.userInfo(userId),"avatar"));
            momentDetail.setNickname(jedis.hget(RedisKeys.userInfo(userId),"nickname"));
            int momentId = momentDetail.getMomentId();
            //还要把评论数和点赞数量加入
            //这里是获取当前点赞次数。我直接加0返回一个long，因为我不想string转int
            momentDetail.setCommentCount(Math.toIntExact(jedis.hincrBy(RedisKeys.momentInfo(momentId), "commentCount", 0)));
            momentDetail.setLikeCount(Math.toIntExact(jedis.hincrBy(RedisKeys.momentInfo(momentId), "likeCount", 0)));
            //判断是不是点赞了
            momentDetail.setIfLike(ifLike(momentId,userId));
        }
        return  list;
    }

    public int likeMoment(int userId, int momentId) {
        Jedis jedis  =jedisPool.getResource();
        //如果存放的是Integer，可以通过这个自加 1； 给点赞数 +1；
        jedis.hincrBy(RedisKeys.momentInfo(momentId),"likeCount",1);
        //还要将是否点赞存入， 有个表 userId 点赞 moment，这个存在redis的set里面。还要加一个mysql
        jedis.sadd(RedisKeys.likeMoment(momentId),""+userId);
        jedis.close();
        int res = momentDao.likeMoment(momentId,userId);
        if(res>0){
            return momentDao.updateMoment(momentId,1,0);
        }
        //todo 还要通知给被点赞人。 （有个通知，xxx给你的瞬间点赞了。）
        return res;
    }

    public int cancelLike(int userId,int momentId){
        Jedis jedis = jedisPool.getResource();
        //删除点赞的人的userId。
        jedis.srem(RedisKeys.likeMoment(momentId),""+userId);
        jedis.hincrBy(RedisKeys.momentInfo(momentId),"likeCount",-1);
        int res = momentDao.cancelLikeMoment(momentId,userId);
        jedis.close();
        //删除成功了，才 -1 ，不然可能出现问题。
        if(res>0){
            return momentDao.updateMoment(momentId,-1,0);
        }
        return res;
    }

    //这里异步将朋友圈发到他的粉丝的队列里面。
    @Async("asyncServiceExecutor")
    public void addFeed(int userId, Moment moment){
        //1.首先要查到userId的粉丝。然后将momentId查到消息。
        List<Integer> followUserId =userDao.queryFollowUserId(userId);
        momentDao.feedMoment(followUserId,moment);
    }
    //删除瞬间额外的一些东西，点赞数，评论表。redis里面的。最重要的是feed里面的。
    @Async("asyncServiceExecutor")
    public void deleteMomentExtra(int userId,int momentId){
        Jedis jedis = jedisPool.getResource();
        jedis.hdel(RedisKeys.momentInfo(userId),"likeCount");
        jedis.hdel(RedisKeys.momentInfo(userId),"CommentCount");
        List<Integer> followUserId = userDao.queryFollowUserId(userId);
        momentDao.deleteMomentFeed(momentId,followUserId);
        momentDao.deleteMomentExtra(momentId);
    }

    //判断当前这个人是否点赞了该瞬间的赞。就是红心和非红心的区别。
    public boolean ifLike(int momentId,int userId){
        Jedis jedis = jedisPool.getResource();
        //如果存在
        if(jedis.sismember(RedisKeys.likeMoment(momentId),""+userId)){
            return  true;
        }else{
            //todo ：查不到可能是因为redis内存不够或者啥的。 从mysql查
            if(momentDao.ifLike(momentId,userId)!=null){
                return true;
            }else{
                return false;
            }
        }
    }
}
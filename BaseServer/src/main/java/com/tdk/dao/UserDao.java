package com.tdk.dao;

import com.tdk.entity.User;
import com.tdk.entity.friendInfo;
import com.tdk.entity.userAndToUser;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description:
 * @author: Dingkang Tang
 * @create: 2020-12-01 16:52
 **/
@Mapper
@Component
public interface UserDao {

    //根据用户名查密码
    @Select("select pwd from t_user where username=#{username}")
    String getPasswordByUsername(String username);

    //得到个人信息
    @Select("select user_id,avatar,nickname from t_user where username=#{username}")
    User getUserInfoByName(String username);

    //注册用户
    @Insert("insert into t_user(username,nickname,pwd,avatar,email) values (#{username},#{nickname},#{password},#{avatar},#{email})")
    int register(User user);

    //查看是否存在用户名。...
    @Select("select ifnull((select 1 from t_user where username=#{username} limit 1),0)")
    int existUser(String username);

    //好友申请
    @Insert("insert into t_friend_request(user_id,to_user_id,msg) values (#{userId},#{toUserId},#{msg})")
    int applyFriend(int userId,int toUserId,String msg);

    //同意还是拒绝好友申请.....
    @Update("update t_friend_request set if_accept=#{determination} where id = #{applyRequestId}")
    void acceptOrRefuse(int applyRequestId, int determination);

    //同意好友申请后,需要拿到两个人的 id，
    @Select("select user_id,to_user_id from t_friend_request where id=#{applyRequestId}")
    userAndToUser queryRequestInfo(int applyRequestId);

    // 两个人互相关注，要插入两个数据
    @Insert("insert into t_friend_list(user_id,follow_user_id) values(#{userId},#{toUserId});"+
             "insert into t_friend_list(user_id,follow_user_id) values(#{toUserId},#{userId});")
    void addFriendToList(int userId, int toUserId);

    //删除好友也是一样。 哎 ,不知道双向删好友残忍还是单向删好友残忍。
    @Delete("delete from t_friend_list where user_id=#{userId} and follow_user_id=#{toUserId};"
            +"delete from t_friend_list where user_id=#{toUserId} and follow_user_id=#{userId}")
    int deleteFriend(int userId, int toUserId);

    //关注我的粉丝，用于feed流。
    @Select("select follow_user_id from t_friend_list where user_id=#{userId}")
    List<Integer> queryFollowUserId(int userId);

    //获得所有好友的信息。
    @Select("SELECT nickname,user_id,avatar FROM t_user WHERE user_id IN\n" +
            "\t(SELECT user_id FROM t_friend_list WHERE follow_user_id = #{userId}) ORDER BY nickname DESC")
    List<friendInfo> getFriendList(int userId);
}
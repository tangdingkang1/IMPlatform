package com.tdk.dao;

import com.tdk.entity.ChatHistory;
import com.tdk.entity.ChatList;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface ChatDao {

    @Select("SELECT temp2.user_id AS to_user_id,t_user.nickname,temp2.msg,temp2.publishtime,t_user.avatar,IFNULL(unread.unreadcount,0) AS unreadcount FROM\n" +
            "(SELECT user_id,msg,publishtime\n" +
            "FROM \t\n" +
            "(SELECT user_id,msg,publishtime\n" +
            "FROM t_chat \n" +
            "WHERE to_user_id =#{userid}\n" +
            "UNION \n" +
            "SELECT to_user_id,msg,publishtime\n" +
            "FROM t_chat \n" +
            "WHERE user_id =#{userid} ORDER BY publishtime DESC)\n" +
            "AS temp\n" +
            "GROUP BY temp.user_id )AS temp2\n" +
            "LEFT JOIN t_user ON temp2.user_id=t_user.user_id \n" +
            "LEFT JOIN t_chat_unread AS unread ON (unread.user_id=#{userid} AND unread.to_user_id=temp2.user_id)\n")
    List<ChatList> getChatList(int userId);

    @Insert("INSERT INTO t_chat(user_id,msg,to_user_id,publishTime,msg_flag) VALUES(#{userId},#{msg},#{toUserId},#{publishTime},#{msgFlag})")
    int sendMsg(int userId,String msg,int toUserId,String publishTime,String msgFlag);

    @Select("SELECT user_id,msg,to_user_id,publishTime from t_chat WHERE msg_flag =#{msgFlag} ORDER BY publishTime")
    List<ChatHistory> getHistory(String msgFlag);
}

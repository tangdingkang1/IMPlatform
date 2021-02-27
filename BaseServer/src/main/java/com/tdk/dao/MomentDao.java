package com.tdk.dao;

import com.tdk.entity.Moment;
import com.tdk.entity.MomentDetail;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface MomentDao {

    //发表瞬间
    @Insert("insert into t_moment(user_id,content,image,time,location)values(#{userId},#{content},#{image},#{time},#{location})")
    @Options(useGeneratedKeys = true,keyProperty = "momentId",keyColumn = "moment_id")
    int publishMoment(Moment moment);

    //发表评论后，就要加一个评论和点赞的行
    @Insert("insert into t_moment_extra(moment_id) values (#{momentId})")
    int likeAndCount(int momentId);

    //获取某个人所有的瞬间，如点别人的朋友圈。或者自己的。我
    @Select("select * from t_moment where user_id = #{userId}")
    List<MomentDetail> getMomentsByUserId(int userId);

    //这里是将userId的的全部瞬间查出来，然后添加到 toUserId（fans）的feed表中去。这样就只要一步了。
    @Insert("INSERT INTO t_feed_moment(moment_id,user_id,follow_user_id,content,image,TIME,location)" +
            "SELECT moment_id,user_id,#{toUserId},content,image,TIME,location FROM t_moment WHERE user_id= #{userId}")
    int addFeedMoments(int userId,int toUserId);

    //删除好友，两个人的feed表都要把对方的瞬间删干净。删除果然比添加容易.....
    @Delete("delete from t_feed_moment where user_id = #{userId} and follow_user_id=#{toUserId}")
    int deleteFeedMoments(int userId,int toUserId);

    // 下面是一个瞬间插入多个粉丝，
    //将自己的发布的朋友，放到feed队列里面。所有粉丝队列都要添加。
    @Insert({
            "<script>",
            "insert into t_feed_moment (moment_id, user_id, follow_user_id,content,image,location,time) values ",
            "<foreach collection='idList' item='item'  separator=','>",
            "(#{moment.userId},#{moment.userId}, #{item}, #{moment.content},#{moment.image},#{moment.location},#{moment.time})",
            "</foreach>",
            "</script>"
    })
    int feedMoment(@Param("idList") List<Integer> followUserId,@Param("moment") Moment moment);

    //获得朋友圈，
    @Select("SELECT * FROM t_moment WHERE user_id IN\n" +
            "(SELECT follow_user_id FROM t_friend_list WHERE user_id =#{userId}) ORDER BY TIME DESC ")
    List<MomentDetail> getFriendMoments(int userId);

    //点赞动态
    @Insert("insert t_like_list(moment_id,user_id) values(#{momentId},#{userId});" )
    int likeMoment(int momentId, int userId);

    //取消点赞。
    @Delete("delete from t_like_list where moment_id=#{momentId} and user_id=#{userId};")
    int cancelLikeMoment(int momentId,int userId);

    //更新点赞数和评论数。为什么要分出来，因为有可能删除失败。或者插入失败，那么数量就可能 小于0这种。
    @Update("update t_moment_extra set like_count=like_count+#{likeCount},comment_count=comment_count+#{commentCount} " +
            "where moment_id=#{momentId}")
    int updateMoment(int momentId,int likeCount,int commentCount);

    //判断是否点赞了。
    @Select("SELECT 1 FROM t_like_list WHERE moment_id =#{momentId} AND user_id =#{userId} LIMIT 1")
    Integer ifLike(int momentId, int userId);

    //删除瞬间。成功删除就返回 1，不成功（可能别人删除别人的瞬间这种，一定要和userId匹配）
    @Delete("delete from t_moment where moment_id=#{momentId} and user_id=#{userId}")
    int deleteMoment(int momentId,int userId);

    //删除每个每个好友列表里面的feed。（因为主人瞬间删除了，粉丝的feed里面也应该没有）
    @Delete("<script> delete from t_feed_moment where moment_id=#{momentId} and follow_user_id in " +
            "<foreach collection='idList' item='item' open='(' separator=',' close=')'> " +
            " #{item} " +
            "</foreach>" +
            "</script>")
    int deleteMomentFeed(@Param("momentId") int momentId,@Param("idList")List<Integer> followUserId);

    //这个和上面的不能一一起删，放在一起会出问题。这个是点赞数量和评论数量，还有点赞的人的列表
    @Delete("delete from t_moment_extra where moment_id=#{momentId};" +
            "delete from t_like_list where moment_id=#{momentId};")
    int deleteMomentExtra(int momentId);
}

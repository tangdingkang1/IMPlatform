package com.tdk.dao;

import com.tdk.entity.Comment;
import com.tdk.entity.Moment;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface CommentDao {

    @Select("select comment_id,moment_id,user_id,content,time from t_comment where moment_id=#{momentId}")
    List<Comment> getDetail(int momentId);

    @Insert("insert t_comment(moment_id,user_id,content,time) values (#{momentId},#{userId},#{content},#{time})")
    int addComment(Comment comment);

    @Delete("delete from t_comment where comment_id=#{commentId}")
    int deleteComment(int commentId);
}

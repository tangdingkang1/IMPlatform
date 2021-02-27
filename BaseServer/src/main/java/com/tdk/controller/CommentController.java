package com.tdk.controller;

import com.tdk.entity.Comment;
import com.tdk.service.CommentService;
import com.tdk.util.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @description: 评论功能
 * @author: Dingkang Tang
 * @create: 2020-12-06 15:46
 **/
@RestController
public class CommentController {
    @Autowired
    CommentService commentService;
    //发表评论
    @PostMapping("/comment/publish")
    public RespBean publishComment(@RequestBody Comment comment) {
       return commentService.publishComment(comment)>0?RespBean.success("成功发表评论"):RespBean.success("评论失败");
    }
    //获取当前瞬间的评论
    @GetMapping("/comment/detail")
    public RespBean getCommentDetail(int momentId){
        List<Comment> comments=  commentService.getDetail(momentId);
        return comments!=null?RespBean.success("得到评论成功",comments):RespBean.error("失败了哦。没有成功得到评论信息");
    }
    //删除评论。
    @GetMapping("/comment/delete")
    public RespBean deleteComment(int commentId){
        return commentService.deleteComment(commentId)>0?RespBean.success("删除评论成功"):RespBean.error("删除评论失败");
    }

}
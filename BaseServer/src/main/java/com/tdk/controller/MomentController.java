package com.tdk.controller;

import com.tdk.entity.Moment;
import com.tdk.service.MomentService;
import com.tdk.util.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @description: 发布动态，
 * @author: Dingkang Tang
 * @create: 2020-12-04 16:41
 **/
@RestController
public class MomentController {
    @Autowired
    MomentService momentService;

    @PostMapping("/moment/publish")
    public RespBean publishMoment(@RequestBody  Moment moment){
        return momentService.publishMoment(moment)>0?RespBean.success("成功发表瞬间"):RespBean.error("瞬间发表失败了！");
    }
    //删除动态
    @GetMapping("/moment/delete")
    public RespBean deleteMoment(int momentId,int userId){
        return momentService.deleteMoment(momentId,userId)>0?RespBean.success("成功删除动态了"):RespBean.error("删除动态失败");
    }
    //获取UserId为xx的用户的所有瞬间。
    @CrossOrigin
    @GetMapping("/moment/getAll")
    public RespBean getOnesMoments(int userId){
        return RespBean.success(userId+"的瞬间", momentService.getOnesMoments(userId));
    }

    //好友动态（朋友圈），微信的朋友圈就是翻译成Moments；
    @GetMapping("/moment/getMoments")
    public RespBean moments(int userId){
        return RespBean.success("获得朋友圈",momentService.getFriendMoments(userId));
    }

    //给瞬间点赞。
    @GetMapping("/moment/likeMoment")
    public RespBean likeMoment(int userId,int momentId){
        return momentService.likeMoment(userId,momentId)>0?RespBean.success("成功点赞惹"):RespBean.error("点赞失败惹");
    }
    //取消点赞
    @GetMapping("/moment/cancelLikeMoment")
    public RespBean cancelLike(int userId,int momentId){
        return momentService.cancelLike(userId,momentId)>0?RespBean.success("成功取消点赞"):RespBean.error("取消点赞失败");
    }
}
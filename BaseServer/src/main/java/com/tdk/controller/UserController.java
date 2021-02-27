package com.tdk.controller;

import com.tdk.entity.LoginReturn;
import com.tdk.entity.User;
import com.tdk.entity.friendInfo;
import com.tdk.service.UserService;
import com.tdk.util.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: Dingkang Tang
 * @create: 2020-11-30 22:01
 **/
@RestController
@Slf4j
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/user/register")
    public RespBean register(@RequestBody User user){
        return userService.register(user)?RespBean.success("注册成功"):RespBean.error("用户名已存在！");
    }

    @GetMapping("/user/login")
    public RespBean login(String username, String password){
        LoginReturn info = userService.login(username,password);
        //如果前端获得的token为空，就说明登录失败
            return info!=null?RespBean.success("登录成功了",info):RespBean.error("账号名或密码不正确！");
    }

    @GetMapping("/user/logout")
    public RespBean logout(@RequestHeader("token") String token){
        return userService.logout(token)?RespBean.success("注销成功"):RespBean.error("注销失败");
    }
    @GetMapping("/friend/apply")
    public RespBean applyFriend(int userId,int toUserId,String msg){
        return userService.applyFriend(userId,toUserId,msg)>0?RespBean.success("申请成功了！"):RespBean.error("申请失败啦");
    }
    @GetMapping("/friend/acceptOrRefuse")
    public RespBean applyRequestOperate(int applyRequestId,int acceptOrRefuse){ //1同意，0拒绝。
        return userService.acceptOrRefuse(applyRequestId,acceptOrRefuse)?RespBean.success("操作好友申请成功"):RespBean.error("操作好友申请是失败");
    }
    @GetMapping("/friend/delete")
    public RespBean deleteFriend(int userId,int toUserId){
        return userService.deleteFriend(userId,toUserId)>0?RespBean.success("删除好友成功"):RespBean.error("删除好友失败");
    }
    @GetMapping("friend/info")
    public List<friendInfo> getFriendInfo(int userId){
        List<friendInfo> info = userService.getFriendInfo(userId);
        return info;
    }
}
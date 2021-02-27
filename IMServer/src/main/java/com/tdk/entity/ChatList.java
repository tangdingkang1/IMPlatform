package com.tdk.entity;

import lombok.Data;

/**
 * @description: 聊天的人和最新消息
 * @author: Dingkang Tang
 * @create: 2020-11-26 18:33
 **/
@Data
public class ChatList {
    private int toUserId;
    private String nickname;
    private String avatar;
    private String msg;
    private String publishTime;
    private int unReadCount;
}
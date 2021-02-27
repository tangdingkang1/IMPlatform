package com.tdk.entity;

import lombok.Data;

/**
 * @description: 用于返回历史消息的实体类
 * @author: Dingkang Tang
 * @create: 2020-11-24 19:42
 **/
@Data
public class ChatHistory {
    int UserId;
    String msg;
    int toUserId;
    String publishTime;
}
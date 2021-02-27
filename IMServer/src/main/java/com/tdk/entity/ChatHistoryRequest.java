package com.tdk.entity;

import lombok.Data;

/**
 * @description: 用来存放前端传来的history请求的属性
 * @author: Dingkang Tang
 * @create: 2020-11-24 19:28
 **/
@Data
public class ChatHistoryRequest {
    private int userId;
    private String msgFlag;
}
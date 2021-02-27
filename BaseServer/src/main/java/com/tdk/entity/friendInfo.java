package com.tdk.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 用来返回好友列表的
 * @author: Dingkang Tang
 * @create: 2021-02-19 19:57
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class friendInfo {
    int userId;
    String nickname;
    String avatar;
}
package com.tdk.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 好友验证时候，成功要返回一个这个类，然后在添加到friend_list里面
 * @author: Dingkang Tang
 * @create: 2020-12-06 21:22
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class userAndToUser {
    int userId;
    int toUserId;
}
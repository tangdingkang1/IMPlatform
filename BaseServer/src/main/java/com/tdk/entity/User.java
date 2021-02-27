package com.tdk.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 用户信息的实体类
 * @author: Dingkang Tang
 * @create: 2020-11-30 19:33
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    int userId;
    String username;
    String nickname;
    String avatar;
    String email;
}
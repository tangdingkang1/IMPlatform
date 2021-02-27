package com.tdk.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 用来返回登录成功的信息
 * @author: Dingkang Tang
 * @create: 2021-02-19 01:45
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginReturn {
        //token里面有了 userId，最后两位。
        String token;
        String nickname;
        String avatar;
}
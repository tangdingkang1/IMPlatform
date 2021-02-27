package com.tdk.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 评论的详细信息
 * @author: Dingkang Tang
 * @create: 2020-12-09 22:16
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    private int commentId; //要回传给前台的，不然不知道删哪条评论了。
    private int userId;
    private String nickname;
    private String avatar;
    private int momentId;
    private String content;
    private String time;
}
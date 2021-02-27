package com.tdk.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description:
 * @author: Dingkang Tang
 * @create: 2020-12-04 16:54
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MomentDetail {
    //自己会有一个自增activity_id。
    private int momentId;    //唯一主键！！！
    private int userId; //这个瞬间属于哪个主人。有删除的权限的
    private String nickname;
    private String avatar;
    private String content;
    private String time;
    private String location;
    private String image;
    private boolean ifLike;   //现在看的这个人是不是点赞。
    private int likeCount;  //点赞量
    private int commentCount;//评论数量
}
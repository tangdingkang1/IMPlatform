package com.tdk.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author: Dingkang Tang
 * @create: 2020-12-04 16:54
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Moment {
    //自己会有一个自增activity_id。
    private int momentId; // 一开始是不存的，通过后面返回主键id。
    private int userId;  //发布人的userId,要通过这个拿到头像和昵称。
    private String content;
    private String time;
    private String location;
    private String image; //图片用；分开，最多九张这种？
}
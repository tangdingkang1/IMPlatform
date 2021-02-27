package com.tdk.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @description:
 * @author: Dingkang Tang
 * @create: 2020-11-24 15:44
 **/
public class DateUtil {
    public static String currentDate(){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }
}
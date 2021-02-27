package com.tdk.util;

public class SortUtil {
    /**
     * @title 基于Reddit算法
     * @description 由于没有反对票,因此使用三个维度进行权重计算:viewCount,commentCount,shareCount
     * @author lidongming
     * @updateTime 2020/4/16 14:27
     */
    public static double reddit(int viewCount,int commentCount,int shareCount){
        // 帖子的新旧程度t
        long ts=(System.currentTimeMillis()-1583661840991L)/1000;
        /**
         * 帖子的受肯定（否定）的程度z,
         * z表示赞成票与反对票之间差额的绝对值。如果对某个帖子的评价，越是一边倒，z就越大。如果赞成票等于反对票，z就等于1。
         */
        double z=shareCount*3+commentCount*1+viewCount*0.8+1;// 兜底逻辑处理

        double score=Math.log10(z)+ts/45000;// 改进版reddit算法
        return score;
    }
}

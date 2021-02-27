package com.tdk.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @description: 用来创建jedisPool的实例
 * @author: Dingkang Tang
 * @create: 2020-11-19 09:53
 **/
@Configuration
public class RedisPool {
    @Autowired
    RedisPoolConfig RedisPoolConfig;

    @Bean
    public JedisPool jedisPool(){
        // jedis连接池
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(RedisPoolConfig.getPoolMaxIdle());
        poolConfig.setMaxTotal(RedisPoolConfig.getPoolMaxTotal());
        poolConfig.setMaxWaitMillis(RedisPoolConfig.getPoolMaxWait() * 1000);

        //创建连接池实例
        JedisPool jedisPool = new JedisPool(poolConfig,RedisPoolConfig.getHost(),RedisPoolConfig.getPort(),RedisPoolConfig.getTimeout(),null);
        return  jedisPool;
    }
}
package com.tdk.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @description: redis和jedispool的配置信息
 * @author: Dingkang Tang
 * @create: 2020-11-19 09:50
 **/
@Component
@Data
@ConfigurationProperties(prefix = "spring.redis")
public class RedisPoolConfig {
    /*
    database: 0
    host: 192.168.1.123
    port: 6379
    timeout: 1000
    MaxTotal: 1000
    MaxIdle: 500
    MaxWait: 500
     */
    private String host;
    private int port;
    private int timeout;
//  private String password; //我的redis忘记设密码了
    private int poolMaxTotal;
    private int poolMaxIdle;
    private int poolMaxWait;
}
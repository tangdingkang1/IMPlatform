package com.tdk.interceptor;

import com.tdk.util.RedisKeys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @description: 自定义登录拦截器
 * @author: Dingkang Tang
 * @create: 2020-11-29 21:35
 **/
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    JedisPool jedisPool;
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) throws Exception {
        //拦截器取到请求先进行判断，如果是OPTIONS请求，则放行
        if("OPTIONS".equals(httpServletRequest.getMethod().toUpperCase())) {
            log.info("option请求进来了");
            return true;
        }
        String token=httpServletRequest.getHeader("token");
        //没登录还想进来？做梦呢。嘻嘻嘻嘻
        if(token==null||token.length()<32){
            return  false;
        }
        int userId = Integer.valueOf(token.substring(32));
        Jedis jedis = jedisPool.getResource();
        String realToken = jedis.get(RedisKeys.token(userId));
        //存在并且更新持续时间，再给用户续三十分钟。
        if(token.equals(realToken)){
            jedis.expire(RedisKeys.token(userId),1800);
            return true;
        }
        return  false;
    }
}
package com.tdk.config;

import com.tdk.interceptor.LoginInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@Slf4j
public class WebConfigurer implements WebMvcConfigurer {
    //要先创建一个这个，如果是autowire的话，那时候它还没实例化，就是空的，
    //自然不能进行相应的拦截了。
    @Bean
    public LoginInterceptor getInterceptor(){
        return  new LoginInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //添加自定义拦截器和拦截路径，此处对所有请求进行拦截，除了登录界面和登录接口
        registry.addInterceptor(getInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/user/register", "/user/login","/swagger-ui.html","/error");
        //addPathPatterns 用于添加拦截规则
        //excludePathPatterns 用于排除拦截
    }
    //跨域请求
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true)
                .maxAge(3600)   //options效验的有效时间
                .allowedHeaders("*");
    }
}

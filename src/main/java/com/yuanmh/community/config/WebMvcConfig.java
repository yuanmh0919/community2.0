package com.yuanmh.community.config;

import com.yuanmh.community.controller.interceptor.LoginRequiredInterceptor;
import com.yuanmh.community.controller.interceptor.LoginTicketInterceptor;
import com.yuanmh.community.controller.interceptor.MessageInterceptor;
import com.yuanmh.community.controller.interceptor.TestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Author: Yuanmh
 * @Date: 下午6:50 2024/6/20
 * @Describe:
 */

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private TestInterceptor testInterceptor;

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    @Autowired
    private LoginRequiredInterceptor loginRequiredInterceptor;


    @Autowired
    private MessageInterceptor messageInterceptor;

    /**
     * 注册拦截器
     * excludePathPatterns 排除不需要拦截的路径
     * addPathPatterns 包含需要拦截的路径
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(testInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.jpg", "/**/*.png", "/**/*.gif")
                .addPathPatterns("/login", "/register");

//        排除静态资源的拦截 其他路径都需要拦截 就不用增加addPathPatterns了
        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.jpg", "/**/*.png", "/**/*.gif");

        //去首页做一些处理 登录之后才能去访问消息页面

        registry.addInterceptor(loginRequiredInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.jpg", "/**/*.png", "/**/*.gif");

        registry.addInterceptor(messageInterceptor)
                .excludePathPatterns("/**/*.css", "/**/*.js", "/**/*.jpg", "/**/*.png", "/**/*.gif");

    }


}

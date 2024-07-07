package com.yuanmh.community.controller.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * @Author: Yuanmh
 * @Date: 下午6:36 2024/6/20
 * @Describe: 拦截器
 */

@Component
public class TestInterceptor implements HandlerInterceptor {


    @Autowired
    private static final Logger logger = LoggerFactory.getLogger(TestInterceptor.class);

    /**
     * 在Controller之前执行
     *
     * @param request
     * @param response
     * @param handler
     * @return true 放行，false 拦截
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        logger.debug("preHandle:" + handler.toString());


        return true;
    }

    /**
     * 在Controller之后执行
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
        logger.debug("postHandle:" + handler.toString());
    }

    /**
     * 在整个请求结束之后执行
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
        logger.debug("afterCompletion:" + handler.toString());
    }

    /*
    如何实现拦截呢？
    1. 实现HandlerInterceptor接口，重写preHandle、postHandle、afterCompletion方法
    2. 配置拦截器WebMvcConfig，实现WebMvcConfigurer接口，添加拦截器bean
     */
}

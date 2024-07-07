package com.yuanmh.community.controller.advice;

import com.yuanmh.community.utils.CommunityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.io.PrintWriter;


/**
 * @Author: Yuanmh
 * @Date: 上午10:50 2024/6/25
 * @Describe:
 */

@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler(Exception.class)
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        LOGGER.error("服务器发送异常：", e.getMessage());
        // 遍历异常堆栈信息 每一个element代表一个堆栈信息
        for (StackTraceElement element : e.getStackTrace()){
            LOGGER.error(element.toString());
        }
        //判断是普通请求还是异步请求
        String xRequestedWith = request.getHeader("x-requested-With");
        //异步请求
        if ("XMLHttpRequest".equals(xRequestedWith)){
            //异步请求响应一个字符串 panin标识一个普通的字符串 需要人为转换为json格式
            response.setContentType("application/plain;charset=UTF-8");
            //获取输出流输出字符串
            PrintWriter writer = response.getWriter();
            //输出字符串
            writer.write(CommunityUtil.getJsonString(1,"服务器异常！"));
        }else{
            //普通请求 重定向到错误页面
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }
}

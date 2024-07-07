package com.yuanmh.community.controller.interceptor;

import com.yuanmh.community.entity.User;
import com.yuanmh.community.service.MessageService;
import com.yuanmh.community.utils.HostHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * @Author: Yuanmh
 * @Date: 下午1:14 2024/7/2
 * @Describe:
 */

@Component
public class MessageInterceptor implements HandlerInterceptor {

    //需要当前登录用户
    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private MessageService messageService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null) {
            int lettersUnreadCount = messageService.findLettersUnreadCount(user.getId(), null);
            int noticeCount = messageService.findNoticeCount(user.getId(), null);
            modelAndView.addObject("allUnreadCount", lettersUnreadCount + noticeCount);
            //去配置拦截器
        }
    }
}

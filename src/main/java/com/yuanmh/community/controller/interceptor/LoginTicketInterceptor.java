package com.yuanmh.community.controller.interceptor;

import com.yuanmh.community.entity.LoginTicket;
import com.yuanmh.community.entity.User;
import com.yuanmh.community.service.UserService;
import com.yuanmh.community.utils.CookieUtil;
import com.yuanmh.community.utils.HostHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

/**
 * @Author: Yuanmh
 * @Date: 下午10:37 2024/6/20
 * @Describe:
 */

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    /**
     * 拦截器
     * 用户登录之前，判断用户是否有登录凭证，如果有，则放行，否则跳转到登录页面
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从cookie中获取ticket凭证
        String ticket = CookieUtil.getValue(request, "ticket");
        if (ticket != null) {
            //查询凭证是否有效
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            //检查凭证是否有效
            if (loginTicket != null && loginTicket.getExpired().after(new Date()) && loginTicket.getStatus() == 0) {
                //凭证有效 ,根据凭证查询用户Id，再根据用户Id查询用户信息
                User user = userService.findUserById(loginTicket.getUserId());
                //在本次请求中持有用户信息
                //这个user在什么地方使用呢？在后面thymeleaf中使用或者后面controller处理业务中使用
                //为了后面的使用，需要将user暂存一下
                //考虑到浏览器访问服务器是多对一的，并发的
                //每一个浏览器访问服务器都需要创建一个独立的线程来处理，这一个多线程的环境
                //此时将一个数据存储到一个地方，让多线程并发都没有问题，需要考虑线程隔离问题，每一个线程单独存储一份，互相之间不干扰
                //封装到工具ThreadLocal中,在多线程环境中隔离存储这个对象
                hostHolder.setUser(user);
            }
        }
        return true;
    }

    /**
     * Controller处理之后，Thymeleaf渲染视图之前
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //拿到当前线程的user信息
        User user = hostHolder.getUser();
        //需要将数据存储到ModelAndView中，供Thymeleaf渲染
        if (user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

    /**
     * 整个请求处理完之后
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //清除ThreadLocal中的user信息
        hostHolder.clear();
    }
}

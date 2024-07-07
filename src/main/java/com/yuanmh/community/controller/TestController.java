package com.yuanmh.community.controller;

import com.google.code.kaptcha.Producer;
import com.yuanmh.community.utils.CommunityUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * @Author: Yuanmh
 * @Date: 下午7:55 2024/6/19
 * @Describe:
 */

@Controller
@RequestMapping("/test")
public class TestController {

    //测试cookie发送和接收
    @GetMapping("/setCookie")
    @ResponseBody
    public Cookie setCookie(HttpServletResponse response) {
        //创建cookie对象
        Cookie cookie = new Cookie("cookie", CommunityUtil.generateUUID());
        //设置cookie属性 只有访问/路径下的页面才能访问到cookie
        cookie.setPath("/");
        //设置cookie有效期 单位秒
        cookie.setMaxAge(60 * 10);//10分钟
        //发送cookie
        response.addCookie(cookie);
        return cookie;
    }


    @GetMapping("/getCookie")
    @ResponseBody
    public Cookie getCookie(@CookieValue("cookie") Cookie cookie) {
        return cookie;
    }


    //测试session发送和接收
    @GetMapping("/setSession")
    @ResponseBody
    public String setSession(HttpSession session) {//session会被自动创建
        session.setAttribute("username", "yuanmh");
        session.setAttribute("password", "a440882");
        return session.toString();
    }

    @GetMapping("/getSession")
    @ResponseBody
    public String getSession(HttpSession session) {
        return session.getAttribute("username") + " " + session.getAttribute("password");
    }


    //测试ajax
    @PostMapping("/ajax")
    @ResponseBody
    public String ajaxTest(String name, int age) {
        System.out.println(name);
        System.out.println(age);
        return CommunityUtil.getJsonString(0, "操作成功");
    }
}

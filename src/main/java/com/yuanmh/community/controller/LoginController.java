package com.yuanmh.community.controller;

import com.google.code.kaptcha.Producer;
import com.yuanmh.community.entity.User;
import com.yuanmh.community.service.UserService;
import com.yuanmh.community.utils.CommunityConstant;
import com.yuanmh.community.utils.CommunityUtil;
import com.yuanmh.community.utils.RedisKeyUtil;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Yuanmh
 * @Date: 上午11:04 2024/6/19
 * @Describe:
 */

@Controller
public class LoginController implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private Producer kaptchaProducer;


    @Autowired
    private UserService userService;


    @Value("${server.servlet.context-path}")
    private String contextPath;

    @GetMapping("/login")

    public String getLoginPage() {
        return "site/login";
    }

    @PostMapping("/login")
    public String login(Model model, /*HttpSession session,*/ HttpServletResponse response,
                        String username, String password, String code, boolean rememberMe,
                        @CookieValue("kaptchaOwner") String kaptchaOwner) {
        //验证码校验
        //从session中获取验证码
//        String kaptcha = (String) session.getAttribute("kaptcha");

        //从redis中获取验证码
        String kaptcha = null;
        //从cookie中获取redisKey
        if (kaptchaOwner != null) {
            String kaptchaKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
            kaptcha = (String) redisTemplate.opsForValue().get(kaptchaKey);
        }

        if (StringUtils.isBlank(kaptcha) || StringUtils.isBlank(code) || !code.equalsIgnoreCase(kaptcha)) {
            model.addAttribute("codeMsg", "验证码错误！");
            return "site/login";
        }
        //检查账号 密码
        //如果选择了记住我 那么失效时间为100天，没有选择记住我 那么失效时间为12小时
        int expiredSeconds = rememberMe ? REMEMBER_EXPIRED_SECONDS : DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);
        //如果map中包含键值ticket 则登录成功
        if (map.containsKey("ticket")) {
            //登录成功，跳转到首页
            Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
            //设置指定访问路径才能访问到cookie
            cookie.setPath(contextPath);
            //设置cookie有效期
            cookie.setMaxAge(expiredSeconds);
            //添加cookie到response中
            response.addCookie(cookie);
            return "redirect:/index";
        } else {
            //登录失败，将错误信息添加到model中，并返回登录页面
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            return "site/login";
        }
    }


    @GetMapping("/logout")
    public String logout(@CookieValue("ticket") String ticket) {
        userService.logout(ticket);
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String getRegisterPage() {
        return "site/register";
    }

    @PostMapping("/register")
    public String register(Model model, User user) {
        //user 会自动传入moder中

        Map<String, Object> map = userService.register(user);
        //如果map中没有信息，说明注册成功，跳转到操作成功页面
        if (map == null || map.isEmpty()) {
            model.addAttribute("msg", "注册成功，已经向您的邮箱发送了激活邮件，请尽快激活！");
            //添加跳转目标页面
            model.addAttribute("target", "/index");
            //封装信息跳转到操作结果页面
            return "site/operate-result";
        } else {
            //如果map中有信息，说明注册失败，返回错误信息
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));

            return "site/register";
        }
    }

    //htt://localhost:8080/community/activation/userId/code

    @GetMapping("/activation/{userId}/{code}")
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code) {
        int statusCode = userService.activation(userId, code);
        //根据statusCode的值，封装不同对的信息
        if (statusCode == ACTIVATION_SUCCESS) {
            model.addAttribute("msg", "激活成功，您的账号可以正常使用了！");
            model.addAttribute("target", "/login");
        } else if (statusCode == ACTIVATION_REPEAT) {
            model.addAttribute("msg", "无效操作，您的账号已经激活过了！");
            model.addAttribute("target", "/index");
        } else {
            model.addAttribute("msg", "激活失败，您提供的激活码不正确！");
            model.addAttribute("target", "/index");
        }
        return "site/operate-result";
    }


    //验证码
    @GetMapping("/kaptcha")
    public void getKaptcha(/*HttpSession session,*/ HttpServletResponse response) {
        //生成验证码
        String text = kaptchaProducer.createText();
        //生成图片
        BufferedImage image = kaptchaProducer.createImage(text);
        //将验证码保存到session
//        session.setAttribute("kaptcha", text);

        //优化：将验证码存储在redis中
        //验证码的归属
        String kaptchaOwner = CommunityUtil.generateUUID();
        Cookie cookie = new Cookie("kaptchaOwner", kaptchaOwner);
        cookie.setPath(contextPath);
        cookie.setMaxAge(60);//验证码有效期1分钟
        response.addCookie(cookie);
        //将验证码保存到redis
        //获取redisKey
        String kaptchaKey = RedisKeyUtil.getKaptchaKey(kaptchaOwner);
        redisTemplate.opsForValue().set(kaptchaKey, text, 60, TimeUnit.SECONDS);//验证码有效期1分钟


        //将图片输出给浏览器
        response.setContentType("image/png");
        try {
            ServletOutputStream os = response.getOutputStream();//不用关闭流，被springmvc管理，会自动关闭
            //将图片写出到输出流中输出到浏览器
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            logger.error("验证码响应失败:", e.getMessage());
        }
    }


}

package com.yuanmh.community.service.impl;

import com.yuanmh.community.dao.UserMapper;
import com.yuanmh.community.entity.LoginTicket;
import com.yuanmh.community.entity.User;
import com.yuanmh.community.service.UserService;
import com.yuanmh.community.utils.CommunityConstant;
import com.yuanmh.community.utils.CommunityUtil;
import com.yuanmh.community.utils.MailClient;
import com.yuanmh.community.utils.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Yuanmh
 * @Date: 上午10:27 2024/6/18
 * @Describe:
 */

@Service
public class UserServiceImpl implements UserService, CommunityConstant {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserMapper userMapper;

    //用户注册需要使用到邮箱功能
    @Autowired
    private MailClient mailClient;

    //注入模板引擎
    @Autowired
    private TemplateEngine templateEngine;

//    @Autowired
//    private LoginTicketMapper loginTicketMapper;

    //生成激活码需要使用到域名和项目名 还需要将域名和项目名注入进来
    @Value("${community.path.domain-name}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    /**
     * 用户注册功能
     *
     * @return
     */
    public Map<String, Object> register(User user) {
        HashMap<String, Object> map = new HashMap<>();
        //空值处理
        //对象为空是业务异常信息
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        //账号邮箱不能为空
        //账号邮箱为空是业务上的漏洞 所以不是报错 而是将提示信息返回
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }
        //验证账号
        User u = userMapper.selectByName(user.getUsername());
        if (u != null) {
            map.put("usernameMsg", "账号已存在");
            return map;
        }
        //验证邮箱
        u = userMapper.selectByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg", "邮箱已存在");
            return map;
        }

        //信息没有问题 可以注册用户
        //截取UUID前五位作为salt
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        //使用salt和MD5加密密码
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setStatus(0);//0-未激活; 1-已激活;
        user.setType(0);//0-普通用户; 1-超级管理员; 2-版主;
        //激活码
        user.setActivationCode(CommunityUtil.generateUUID());
        //随机头像
        // 牛客网提供的随机头像 总共有1000张随机头像
        // https://images.nowcoder.com/head/1t.png
        //设置随机头像
        user.setHeaderUrl(String.format("https://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);
        //给用户发送激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        //htt://localhost:8080/community/activation/userId/code
        context.setVariable("url", domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode());
        //使用模板引擎渲染邮件内容
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "仿牛客社区-账号激活码", content);
        return map;
    }


    /**
     * 激活用户
     *
     * @return int
     */
    public int activation(int userId, String activationCode) {
        //先查询用户时候存在 并且激活码是否正确
        User user = userMapper.selectById(userId);
        //查看激活状态
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(activationCode)) {
            userMapper.updateStatus(userId, 1);
            //修改用户信息 删除redis中的缓存信息
            deleteUserCache(userId);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }

    @Override
    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        HashMap<String, Object> map = new HashMap<>();

        //空值处理
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }
        //验证账号
        User user = userMapper.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "账号不存在");
            return map;
        }
        //验证密码
        if (!user.getPassword().equals(CommunityUtil.md5(password + user.getSalt()))) {
            map.put("passwordMsg", "密码错误");
            return map;
        }
        //验证是否激活
        if (user.getStatus() == 0) {
            map.put("statusMsg", "账号未激活");
            return map;
        }
        //生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);//0：有效 1：无效
        //设置失效时间：当前时间+有效期（秒）
        loginTicket.setExpired(new Date(System.currentTimeMillis() + (expiredSeconds * 1000L)));

//        loginTicketMapper.insertLoginTicket(loginTicket);

        //优化 将登录凭证存储在redis中
        //先获取key
        String ticketKey = RedisKeyUtil.getLoginTicketKey(loginTicket.getTicket());
        //设置登录凭证到redis loginTicket对象序列化成为字符串存储在redis中
        redisTemplate.opsForValue().set(ticketKey, loginTicket);


        //返回登录凭证
        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    /**
     * 退出登录 将登录状态设置为1
     *
     * @param ticket
     */
    @Override
    public void logout(String ticket) {
//        loginTicketMapper.updateStatus(ticket, 1);
        //拿到登录凭证key
        String ticketKey = RedisKeyUtil.getLoginTicketKey(ticket);
        //根据key获取value
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
        loginTicket.setStatus(1);
        //更新redis
        redisTemplate.opsForValue().set(ticketKey, loginTicket);
    }

    @Override
    public LoginTicket findLoginTicket(String ticket) {
//        return loginTicketMapper.selectByTicket(ticket);
        String ticketKey = RedisKeyUtil.getLoginTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(ticketKey);
    }

    @Override
    public int updateHeader(int userId, String headerUrl) {
        int rows = userMapper.updateHeader(userId, headerUrl);
        //数据变更时删除缓存中的数据
        deleteUserCache(userId);
        return rows;
    }

    @Override
    public int updatePassword(int userId, String newPassword) {
        int rows = userMapper.updatePassword(userId, newPassword);
        deleteUserCache(userId);
        return rows;
    }


    @Override
    public List<User> findAllUsers() {
        return userMapper.selectAll();
    }

    @Override
    public User findUserById(int id) {
//        return userMapper.selectById(id);
        //优化 查询用户信息，优先从缓存中查询
        User user = getUserByCache(id);
        if (user == null) {
            user = initUserCache(id);
        }
        return user;
    }

    @Override
    public User findUserByName(String username) {
        return userMapper.selectByName(username);
    }


    //优化查询用户功能，将查询出来的用户信息存储在缓存中，减少数据库查询次数
    // 优先从缓存中查询用户信息
    //1、优先从缓存中取值
    public User getUserByCache(int userId) {
        //先获取redis中存储的key
        String key = RedisKeyUtil.getUserKey(userId);
        //从redis中获取用户信息
        return (User) redisTemplate.opsForValue().get(key);
    }

    //2、缓存中没有，从数据库查询，并将用户信息存储在缓存中
    public User initUserCache(int userId) {
        User user = userMapper.selectById(userId);
        //将用户存储在redis中 并设置过期时间为1小时
        String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(userKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }

    //3、数据变更时删除缓存中的数据
    public void deleteUserCache(int userId) {
        String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(userKey);
    }

}

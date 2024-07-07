package com.yuanmh.community.service;

import com.yuanmh.community.entity.LoginTicket;
import com.yuanmh.community.entity.User;

import java.util.List;
import java.util.Map;

/**
 * @Author: Yuanmh
 * @Date: 上午10:19 2024/6/18
 * @Describe:
 */
public interface UserService {


    /**
     * 查找所有用户
     */
    List<User> findAllUsers();

    User findUserById(int id);

    Map<String, Object> register(User user);

    int activation(int userId, String activationCode);

    Map<String, Object> login(String username, String password, int expiredSeconds);

    void logout(String ticket);

    LoginTicket findLoginTicket(String ticket);

    int updateHeader(int userId, String headerUrl);

    int updatePassword(int userId, String newPassword);

    User findUserByName(String username);

}

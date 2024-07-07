package com.yuanmh.community.utils;

import com.yuanmh.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * @Author: Yuanmh
 * @Date: 下午11:09 2024/6/20
 * @Describe: 起到容器的作用，用户存储用户信息，起到代替session的作用
 */
@Component
public class HostHolder {
    //使用ThreadLocal实现线程隔离 以线程为key存取值
    private ThreadLocal<User> users = new ThreadLocal<>();

    public User getUser() {
        return users.get();
    }

    public void setUser(User user) {
        users.set(user);
    }

    public void clear() {
        users.remove();
    }
}

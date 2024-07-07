package com.yuanmh.community.dao;

import com.yuanmh.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: Yuanmh
 * @Date: 下午2:37 2024/6/17
 * @Describe:
 */
@Mapper
public interface UserMapper {
    //根据ID查询用户信息
    User selectById(int id);

    //根据用户名查询用户信息
    User selectByName(String username);

    //根据邮箱查询用户信息
    User selectByEmail(String email);

    //查询所有用户信息
    List<User> selectAll();

    //新增用户信息
    int insertUser(User user);

    //修改用户状态
    int updateStatus(int id, int status);

    //修改头像
    int updateHeader(int id, String headerUrl);

    //修改密码
    int updatePassword(int id, String password);
}

package com.yuanmh.community.dao;

import com.yuanmh.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Author: Yuanmh
 * @Date: 下午1:53 2024/6/17
 * @Describe:
 */
@Mapper
public interface DiscussPostMapper {
    //查询用户个人的所有帖子 + 分页查询 起始行数offset和每页显示的行数limit
    List<DiscussPost> selectDiscussPosts(Integer userId, int offset, int limit);

    //根据用户id查询行数
    int selectDiscussPostRows(@Param("userId") Integer userId);

    //根据id查询帖子
    DiscussPost selectDiscussPostById(int id);

    //插入帖子
    int insertDiscussPost(DiscussPost discussPost);

    //更新帖子的回复数量
    int updateCommentCount(int id, int commentCount);

}

package com.yuanmh.community.service;

import com.yuanmh.community.entity.DiscussPost;

import java.util.List;

/**
 * @Author: Yuanmh
 * @Date: 上午11:08 2024/6/18
 * @Describe:
 */
public interface DiscussPostsService {
    List<DiscussPost> findDiscussPosts(int userId, int offset, int limit);

    int findDiscussPostsRows(int userId);

    //根据id查询帖子
    DiscussPost findDiscussPostById(int id);

    //添加帖子
    int addDiscussPost(DiscussPost discussPost);

    //更新帖子回复数量
    int updateCommentCount(int id, int commentCount);

}

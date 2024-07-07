package com.yuanmh.community.service;

import com.yuanmh.community.entity.Comment;

import java.util.List;

/**
 * @Author: Yuanmh
 * @Date: 下午3:07 2024/6/23
 * @Describe:
 */
public interface CommentService {

    //根据实体查询评论
    List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit);

    //    获取评论总数
    int findCommentCount(int entityType, int entityId);

    //    新增评论
    int addComment(Comment comment);

    //根据id查询回复
    Comment findCommentById(int id);
}

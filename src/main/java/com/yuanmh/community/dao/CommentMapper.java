package com.yuanmh.community.dao;

import com.yuanmh.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: Yuanmh
 * @Date: 下午12:17 2024/6/23
 * @Describe:
 */
@Mapper
public interface CommentMapper {

    //根据实体查询评论
    List<Comment> selectByEntity(int entityType, int entityId, int offset, int limit);

    //获取评论总数
    int selectCountByEntity(int entityType, int entityId);

    //添加评论
    int insertComment(Comment comment);

    //根据id查询评论
    Comment selectCommentById(int id);

}

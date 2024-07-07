package com.yuanmh.community.service.impl;

import com.yuanmh.community.dao.CommentMapper;
import com.yuanmh.community.entity.Comment;
import com.yuanmh.community.service.CommentService;
import com.yuanmh.community.service.DiscussPostsService;
import com.yuanmh.community.utils.CommunityConstant;
import com.yuanmh.community.utils.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @Author: Yuanmh
 * @Date: 下午3:10 2024/6/23
 * @Describe:
 */

@Service
public class CommentServiceImpl implements CommentService, CommunityConstant {

    @Autowired
    private CommentMapper commentMapper;

    //添加评论也需要过滤敏感词，需要注入敏感词过滤器
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostsService discussPostsService;

    @Override
    public List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectByEntity(entityType, entityId, offset, limit);
    }

    @Override
    public int findCommentCount(int entityType, int entityId) {
        return commentMapper.selectCountByEntity(entityType, entityId);
    }

    //包括两次dml操作，新增评论和更新评论数，需要事务管理
    //设置读已提交隔离级别，避免脏读
    //还需要添加传播机制，Propagation.REQUIRED 表示如果当前已经存在一个事务，那么当前方法将在这个已有的事务内运行。
    // 如果当前没有事务，Spring会为该方法创建一个新的事务。
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    @Override
    public int addComment(Comment comment) {
        //判断是否为空
        if (comment == null) {
            throw new NullPointerException("参数不能为空！");
        }
        //过滤标签
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        //过滤敏感词
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        //插入评论
        commentMapper.insertComment(comment);

        //更新帖子评论数
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            //查询帖子评论数量
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            //更新帖子评论数量
            discussPostsService.updateCommentCount(comment.getEntityId(), count);
        }


        return 0;
    }

    @Override
    public Comment findCommentById(int id) {
        return commentMapper.selectCommentById(id);
    }
}
